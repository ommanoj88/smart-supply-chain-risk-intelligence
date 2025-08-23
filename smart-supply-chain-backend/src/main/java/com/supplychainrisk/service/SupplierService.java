package com.supplychainrisk.service;

import com.supplychainrisk.dto.SupplierDTO;
import com.supplychainrisk.dto.SupplierCategoryDTO;
import com.supplychainrisk.entity.*;
import com.supplychainrisk.exception.BusinessException;
import com.supplychainrisk.repository.*;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced Supplier Service with enterprise patterns and caching
 */
@Service
@Transactional
public class SupplierService {
    
    private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private SupplierCategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RiskAssessmentService riskAssessmentService;
    
    /**
     * Get all suppliers with pagination and sorting
     */
    @Cacheable(value = "suppliers", key = "#page + '_' + #size + '_' + #sortBy + '_' + #sortDirection")
    public Page<SupplierDTO> getAllSuppliers(int page, int size, String sortBy, String sortDirection) {
        logger.debug("Fetching suppliers - page: {}, size: {}, sortBy: {}, direction: {}", 
                    page, size, sortBy, sortDirection);
        
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        return suppliers.map(this::convertToDTO);
    }
    
    /**
     * Get supplier by ID with caching
     */
    @Cacheable(value = "suppliers", key = "#id")
    public SupplierDTO getSupplierById(Long id) {
        logger.debug("Fetching supplier by ID: {}", id);
        
        Supplier supplier = supplierRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Supplier not found with id: " + id));
        return convertToDTO(supplier);
    }
    
    /**
     * Get supplier by supplier code with caching
     */
    @Cacheable(value = "suppliers", key = "'code_' + #supplierCode")
    public SupplierDTO getSupplierByCode(String supplierCode) {
        logger.debug("Fetching supplier by code: {}", supplierCode);
        
        Supplier supplier = supplierRepository.findBySupplierCode(supplierCode)
            .orElseThrow(() -> new BusinessException("Supplier not found with code: " + supplierCode));
        return convertToDTO(supplier);
    }
    
    /**
     * Create new supplier with cache eviction
     */
    @CacheEvict(value = "suppliers", allEntries = true)
    public SupplierDTO createSupplier(SupplierDTO supplierDTO, Long userId) {
        logger.info("Creating new supplier with code: {}", supplierDTO.getSupplierCode());
        
        // Validate supplier code uniqueness
        if (supplierRepository.existsBySupplierCode(supplierDTO.getSupplierCode())) {
            throw new ValidationException("Supplier code already exists: " + supplierDTO.getSupplierCode());
        }
        
        Supplier supplier = convertToEntity(supplierDTO);
        
        // Set audit fields
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("User not found with id: " + userId));
        supplier.setCreatedBy(user);
        supplier.setUpdatedBy(user);
        
        // Calculate initial risk scores
        riskAssessmentService.calculateRiskScores(supplier);
        
        supplier = supplierRepository.save(supplier);
        logger.info("Successfully created supplier with ID: {}", supplier.getId());
        
        return convertToDTO(supplier);
    }
    
    /**
     * Update existing supplier with cache update
     */
    @CachePut(value = "suppliers", key = "#id")
    @CacheEvict(value = "suppliers", allEntries = true, condition = "#result != null")
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO, Long userId) {
        logger.info("Updating supplier with ID: {}", id);
        
        Supplier existingSupplier = supplierRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Supplier not found with id: " + id));
        
        // Check if supplier code is being changed and if it's unique
        if (!existingSupplier.getSupplierCode().equals(supplierDTO.getSupplierCode())) {
            if (supplierRepository.existsBySupplierCode(supplierDTO.getSupplierCode())) {
                throw new ValidationException("Supplier code already exists: " + supplierDTO.getSupplierCode());
            }
        }
        
        // Update fields
        updateSupplierFields(existingSupplier, supplierDTO);
        
        // Set audit fields
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        existingSupplier.setUpdatedBy(user);
        existingSupplier.setUpdatedAt(LocalDateTime.now());
        
        // Recalculate risk scores
        riskAssessmentService.calculateRiskScores(existingSupplier);
        
        existingSupplier = supplierRepository.save(existingSupplier);
        return convertToDTO(existingSupplier);
    }
    
    /**
     * Delete supplier
     */
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }
    
    /**
     * Search suppliers with advanced filtering
     */
    public Page<SupplierDTO> searchSuppliers(
            String searchTerm,
            Supplier.SupplierStatus status,
            Supplier.SupplierTier tier,
            String country,
            String industry,
            Integer minRiskScore,
            Integer maxRiskScore,
            int page,
            int size,
            String sortBy,
            String sortDirection) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<Supplier> suppliers;
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            suppliers = supplierRepository.findByNameOrSupplierCodeContaining(searchTerm, pageable);
        } else {
            suppliers = supplierRepository.findByMultipleCriteria(
                status, tier, country, industry, minRiskScore, maxRiskScore, pageable);
        }
        
        return suppliers.map(this::convertToDTO);
    }
    
    /**
     * Get suppliers by status
     */
    public Page<SupplierDTO> getSuppliersByStatus(Supplier.SupplierStatus status, Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findByStatus(status, pageable);
        return suppliers.map(this::convertToDTO);
    }
    
    /**
     * Get suppliers by tier
     */
    public Page<SupplierDTO> getSuppliersByTier(Supplier.SupplierTier tier, Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findByTier(tier, pageable);
        return suppliers.map(this::convertToDTO);
    }
    
    /**
     * Get preferred suppliers
     */
    public Page<SupplierDTO> getPreferredSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findByPreferredSupplierTrue(pageable);
        return suppliers.map(this::convertToDTO);
    }
    
    /**
     * Get strategic suppliers
     */
    public Page<SupplierDTO> getStrategicSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findByStrategicSupplierTrue(pageable);
        return suppliers.map(this::convertToDTO);
    }
    
    /**
     * Get supplier entity by ID (for enhanced services)
     */
    public Supplier getSupplierEntityById(Long id) {
        return supplierRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }
    
    /**
     * Get suppliers requiring audit
     */
    public List<SupplierDTO> getSuppliersRequiringAudit() {
        List<Supplier> suppliers = supplierRepository.findSuppliersRequiringAudit();
        return suppliers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * Get supplier statistics
     */
    public Map<String, Object> getSupplierStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total count
        stats.put("totalSuppliers", supplierRepository.count());
        
        // Count by status
        List<Object[]> statusCounts = supplierRepository.getSupplierCountByStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] row : statusCounts) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("countByStatus", statusMap);
        
        // Count by tier
        List<Object[]> tierCounts = supplierRepository.getSupplierCountByTier();
        Map<String, Long> tierMap = new HashMap<>();
        for (Object[] row : tierCounts) {
            tierMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("countByTier", tierMap);
        
        // Average risk scores
        List<Object[]> avgRiskScores = supplierRepository.getAverageRiskScores(Supplier.SupplierStatus.ACTIVE);
        if (!avgRiskScores.isEmpty()) {
            Object[] scores = avgRiskScores.get(0);
            Map<String, Double> riskScoreMap = new HashMap<>();
            riskScoreMap.put("overall", (Double) scores[0]);
            riskScoreMap.put("financial", (Double) scores[1]);
            riskScoreMap.put("operational", (Double) scores[2]);
            riskScoreMap.put("compliance", (Double) scores[3]);
            riskScoreMap.put("geographic", (Double) scores[4]);
            stats.put("averageRiskScores", riskScoreMap);
        }
        
        return stats;
    }
    
    /**
     * Get distinct countries
     */
    public List<String> getDistinctCountries() {
        return supplierRepository.findDistinctCountries();
    }
    
    /**
     * Get distinct industries
     */
    public List<String> getDistinctIndustries() {
        return supplierRepository.findDistinctIndustries();
    }
    
    /**
     * Convert Supplier entity to DTO
     */
    private SupplierDTO convertToDTO(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        
        dto.setId(supplier.getId());
        dto.setSupplierCode(supplier.getSupplierCode());
        dto.setName(supplier.getName());
        dto.setLegalName(supplier.getLegalName());
        dto.setTier(supplier.getTier());
        
        // Contact Information
        dto.setPrimaryContactName(supplier.getPrimaryContactName());
        dto.setPrimaryContactEmail(supplier.getPrimaryContactEmail());
        dto.setPrimaryContactPhone(supplier.getPrimaryContactPhone());
        dto.setSecondaryContactName(supplier.getSecondaryContactName());
        dto.setSecondaryContactEmail(supplier.getSecondaryContactEmail());
        dto.setSecondaryContactPhone(supplier.getSecondaryContactPhone());
        dto.setWebsite(supplier.getWebsite());
        
        // Address Information
        dto.setStreetAddress(supplier.getStreetAddress());
        dto.setCity(supplier.getCity());
        dto.setStateProvince(supplier.getStateProvince());
        dto.setPostalCode(supplier.getPostalCode());
        dto.setCountry(supplier.getCountry());
        dto.setLatitude(supplier.getLatitude());
        dto.setLongitude(supplier.getLongitude());
        
        // Business Information
        dto.setIndustry(supplier.getIndustry());
        dto.setBusinessType(supplier.getBusinessType());
        dto.setAnnualRevenue(supplier.getAnnualRevenue());
        dto.setEmployeeCount(supplier.getEmployeeCount());
        dto.setYearsInBusiness(supplier.getYearsInBusiness());
        
        // Risk and Performance Metrics
        dto.setOverallRiskScore(supplier.getOverallRiskScore());
        dto.setFinancialRiskScore(supplier.getFinancialRiskScore());
        dto.setOperationalRiskScore(supplier.getOperationalRiskScore());
        dto.setComplianceRiskScore(supplier.getComplianceRiskScore());
        dto.setGeographicRiskScore(supplier.getGeographicRiskScore());
        
        // Performance KPIs
        dto.setOnTimeDeliveryRate(supplier.getOnTimeDeliveryRate());
        dto.setQualityRating(supplier.getQualityRating());
        dto.setCostCompetitivenessScore(supplier.getCostCompetitivenessScore());
        dto.setResponsivenessScore(supplier.getResponsivenessScore());
        
        // Certifications and Compliance
        dto.setIsoCertifications(supplier.getIsoCertifications());
        dto.setComplianceCertifications(supplier.getComplianceCertifications());
        dto.setLastAuditDate(supplier.getLastAuditDate());
        dto.setNextAuditDueDate(supplier.getNextAuditDueDate());
        
        // Financial Information
        dto.setCreditRating(supplier.getCreditRating());
        dto.setPaymentTerms(supplier.getPaymentTerms());
        dto.setCurrency(supplier.getCurrency());
        
        // Status and Metadata
        dto.setStatus(supplier.getStatus());
        dto.setPreferredSupplier(supplier.getPreferredSupplier());
        dto.setStrategicSupplier(supplier.getStrategicSupplier());
        
        // Audit Fields
        if (supplier.getCreatedBy() != null) {
            dto.setCreatedById(supplier.getCreatedBy().getId());
            dto.setCreatedByName(supplier.getCreatedBy().getName());
        }
        if (supplier.getUpdatedBy() != null) {
            dto.setUpdatedById(supplier.getUpdatedBy().getId());
            dto.setUpdatedByName(supplier.getUpdatedBy().getName());
        }
        dto.setCreatedAt(supplier.getCreatedAt());
        dto.setUpdatedAt(supplier.getUpdatedAt());
        
        // Categories
        if (supplier.getCategories() != null) {
            dto.setCategories(supplier.getCategories().stream()
                .map(this::convertCategoryToDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
    
    /**
     * Convert SupplierDTO to entity
     */
    private Supplier convertToEntity(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        
        supplier.setId(dto.getId());
        supplier.setSupplierCode(dto.getSupplierCode());
        supplier.setName(dto.getName());
        supplier.setLegalName(dto.getLegalName());
        supplier.setTier(dto.getTier() != null ? dto.getTier() : Supplier.SupplierTier.SECONDARY);
        
        // Contact Information
        supplier.setPrimaryContactName(dto.getPrimaryContactName());
        supplier.setPrimaryContactEmail(dto.getPrimaryContactEmail());
        supplier.setPrimaryContactPhone(dto.getPrimaryContactPhone());
        supplier.setSecondaryContactName(dto.getSecondaryContactName());
        supplier.setSecondaryContactEmail(dto.getSecondaryContactEmail());
        supplier.setSecondaryContactPhone(dto.getSecondaryContactPhone());
        supplier.setWebsite(dto.getWebsite());
        
        // Address Information
        supplier.setStreetAddress(dto.getStreetAddress());
        supplier.setCity(dto.getCity());
        supplier.setStateProvince(dto.getStateProvince());
        supplier.setPostalCode(dto.getPostalCode());
        supplier.setCountry(dto.getCountry());
        supplier.setLatitude(dto.getLatitude());
        supplier.setLongitude(dto.getLongitude());
        
        // Business Information
        supplier.setIndustry(dto.getIndustry());
        supplier.setBusinessType(dto.getBusinessType());
        supplier.setAnnualRevenue(dto.getAnnualRevenue());
        supplier.setEmployeeCount(dto.getEmployeeCount());
        supplier.setYearsInBusiness(dto.getYearsInBusiness());
        
        // Risk and Performance Metrics (will be calculated by risk assessment service)
        supplier.setOverallRiskScore(dto.getOverallRiskScore() != null ? dto.getOverallRiskScore() : 0);
        supplier.setFinancialRiskScore(dto.getFinancialRiskScore() != null ? dto.getFinancialRiskScore() : 0);
        supplier.setOperationalRiskScore(dto.getOperationalRiskScore() != null ? dto.getOperationalRiskScore() : 0);
        supplier.setComplianceRiskScore(dto.getComplianceRiskScore() != null ? dto.getComplianceRiskScore() : 0);
        supplier.setGeographicRiskScore(dto.getGeographicRiskScore() != null ? dto.getGeographicRiskScore() : 0);
        
        // Performance KPIs
        supplier.setOnTimeDeliveryRate(dto.getOnTimeDeliveryRate());
        supplier.setQualityRating(dto.getQualityRating());
        supplier.setCostCompetitivenessScore(dto.getCostCompetitivenessScore() != null ? dto.getCostCompetitivenessScore() : 0);
        supplier.setResponsivenessScore(dto.getResponsivenessScore() != null ? dto.getResponsivenessScore() : 0);
        
        // Certifications and Compliance
        supplier.setIsoCertifications(dto.getIsoCertifications());
        supplier.setComplianceCertifications(dto.getComplianceCertifications());
        supplier.setLastAuditDate(dto.getLastAuditDate());
        supplier.setNextAuditDueDate(dto.getNextAuditDueDate());
        
        // Financial Information
        supplier.setCreditRating(dto.getCreditRating());
        supplier.setPaymentTerms(dto.getPaymentTerms());
        supplier.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "USD");
        
        // Status and Metadata
        supplier.setStatus(dto.getStatus() != null ? dto.getStatus() : Supplier.SupplierStatus.ACTIVE);
        supplier.setPreferredSupplier(dto.getPreferredSupplier() != null ? dto.getPreferredSupplier() : false);
        supplier.setStrategicSupplier(dto.getStrategicSupplier() != null ? dto.getStrategicSupplier() : false);
        
        return supplier;
    }
    
    /**
     * Update supplier fields from DTO
     */
    private void updateSupplierFields(Supplier supplier, SupplierDTO dto) {
        supplier.setSupplierCode(dto.getSupplierCode());
        supplier.setName(dto.getName());
        supplier.setLegalName(dto.getLegalName());
        supplier.setTier(dto.getTier());
        
        // Contact Information
        supplier.setPrimaryContactName(dto.getPrimaryContactName());
        supplier.setPrimaryContactEmail(dto.getPrimaryContactEmail());
        supplier.setPrimaryContactPhone(dto.getPrimaryContactPhone());
        supplier.setSecondaryContactName(dto.getSecondaryContactName());
        supplier.setSecondaryContactEmail(dto.getSecondaryContactEmail());
        supplier.setSecondaryContactPhone(dto.getSecondaryContactPhone());
        supplier.setWebsite(dto.getWebsite());
        
        // Address Information
        supplier.setStreetAddress(dto.getStreetAddress());
        supplier.setCity(dto.getCity());
        supplier.setStateProvince(dto.getStateProvince());
        supplier.setPostalCode(dto.getPostalCode());
        supplier.setCountry(dto.getCountry());
        supplier.setLatitude(dto.getLatitude());
        supplier.setLongitude(dto.getLongitude());
        
        // Business Information
        supplier.setIndustry(dto.getIndustry());
        supplier.setBusinessType(dto.getBusinessType());
        supplier.setAnnualRevenue(dto.getAnnualRevenue());
        supplier.setEmployeeCount(dto.getEmployeeCount());
        supplier.setYearsInBusiness(dto.getYearsInBusiness());
        
        // Performance KPIs
        supplier.setOnTimeDeliveryRate(dto.getOnTimeDeliveryRate());
        supplier.setQualityRating(dto.getQualityRating());
        supplier.setCostCompetitivenessScore(dto.getCostCompetitivenessScore());
        supplier.setResponsivenessScore(dto.getResponsivenessScore());
        
        // Certifications and Compliance
        supplier.setIsoCertifications(dto.getIsoCertifications());
        supplier.setComplianceCertifications(dto.getComplianceCertifications());
        supplier.setLastAuditDate(dto.getLastAuditDate());
        supplier.setNextAuditDueDate(dto.getNextAuditDueDate());
        
        // Financial Information
        supplier.setCreditRating(dto.getCreditRating());
        supplier.setPaymentTerms(dto.getPaymentTerms());
        supplier.setCurrency(dto.getCurrency());
        
        // Status and Metadata
        supplier.setStatus(dto.getStatus());
        supplier.setPreferredSupplier(dto.getPreferredSupplier());
        supplier.setStrategicSupplier(dto.getStrategicSupplier());
    }
    
    /**
     * Convert SupplierCategory to DTO
     */
    private SupplierCategoryDTO convertCategoryToDTO(SupplierCategory category) {
        SupplierCategoryDTO dto = new SupplierCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setColor(category.getColor());
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    }
}