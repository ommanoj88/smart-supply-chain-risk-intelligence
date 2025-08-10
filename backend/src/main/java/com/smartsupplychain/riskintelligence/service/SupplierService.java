package com.smartsupplychain.riskintelligence.service;

import com.smartsupplychain.riskintelligence.dto.SupplierDto;
import com.smartsupplychain.riskintelligence.exception.ResourceNotFoundException;
import com.smartsupplychain.riskintelligence.model.Supplier;
import com.smartsupplychain.riskintelligence.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    private final Random random = new Random();

    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SupplierDto> getActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SupplierDto getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return convertToDto(supplier);
    }

    public List<SupplierDto> getSuppliersByLocation(String location) {
        return supplierRepository.findByLocationContainingIgnoreCase(location).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SupplierDto> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SupplierDto> getSuppliersByRiskRange(BigDecimal minRisk, BigDecimal maxRisk) {
        return supplierRepository.findByRiskScoreRange(minRisk, maxRisk).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<SupplierDto> getHighRiskSuppliers() {
        BigDecimal highRiskThreshold = new BigDecimal("7.0");
        return supplierRepository.findHighRiskSuppliers(highRiskThreshold).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public SupplierDto createSupplier(SupplierDto supplierDto) {
        Supplier supplier = new Supplier();
        supplier.setName(supplierDto.getName());
        supplier.setLocation(supplierDto.getLocation());
        supplier.setContactInfo(supplierDto.getContactInfo());
        
        // Calculate initial risk score
        BigDecimal riskScore = calculateRiskScore(supplier);
        supplier.setRiskScore(riskScore);
        
        supplier = supplierRepository.save(supplier);
        return convertToDto(supplier);
    }

    public SupplierDto updateSupplier(Long id, SupplierDto supplierDto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        if (supplierDto.getName() != null) {
            supplier.setName(supplierDto.getName());
        }
        if (supplierDto.getLocation() != null) {
            supplier.setLocation(supplierDto.getLocation());
        }
        if (supplierDto.getContactInfo() != null) {
            supplier.setContactInfo(supplierDto.getContactInfo());
        }
        if (supplierDto.getIsActive() != null) {
            supplier.setIsActive(supplierDto.getIsActive());
        }

        // Recalculate risk score
        BigDecimal riskScore = calculateRiskScore(supplier);
        supplier.setRiskScore(riskScore);

        supplier = supplierRepository.save(supplier);
        return convertToDto(supplier);
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }

    public SupplierDto updateRiskScore(Long id, BigDecimal newRiskScore) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        if (newRiskScore.compareTo(BigDecimal.ZERO) < 0 || newRiskScore.compareTo(new BigDecimal("10")) > 0) {
            throw new IllegalArgumentException("Risk score must be between 0 and 10");
        }
        
        supplier.setRiskScore(newRiskScore);
        supplier = supplierRepository.save(supplier);
        return convertToDto(supplier);
    }

    private BigDecimal calculateRiskScore(Supplier supplier) {
        // Mock risk calculation algorithm
        // In a real application, this would consider various factors like:
        // - Historical performance
        // - Financial stability
        // - Geographic risk factors
        // - Compliance records
        // - Delivery history
        
        BigDecimal baseScore = new BigDecimal("5.0"); // Base risk score
        
        // Location-based risk factors (simplified)
        String location = supplier.getLocation().toLowerCase();
        if (location.contains("china") || location.contains("bangladesh")) {
            baseScore = baseScore.add(new BigDecimal("1.5"));
        } else if (location.contains("usa") || location.contains("germany") || location.contains("japan")) {
            baseScore = baseScore.subtract(new BigDecimal("1.0"));
        }
        
        // Add some randomness to simulate real-world variability
        BigDecimal randomFactor = new BigDecimal(random.nextGaussian() * 1.0);
        baseScore = baseScore.add(randomFactor);
        
        // Ensure score is within bounds
        if (baseScore.compareTo(BigDecimal.ZERO) < 0) {
            baseScore = BigDecimal.ZERO;
        } else if (baseScore.compareTo(new BigDecimal("10")) > 0) {
            baseScore = new BigDecimal("10");
        }
        
        return baseScore.setScale(2, RoundingMode.HALF_UP);
    }

    private SupplierDto convertToDto(Supplier supplier) {
        SupplierDto dto = new SupplierDto();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setLocation(supplier.getLocation());
        dto.setContactInfo(supplier.getContactInfo());
        dto.setRiskScore(supplier.getRiskScore());
        dto.setIsActive(supplier.getIsActive());
        return dto;
    }
}