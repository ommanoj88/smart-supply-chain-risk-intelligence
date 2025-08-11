package com.supplychainrisk.controller;

import com.supplychainrisk.dto.SupplierDTO;
import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.service.SupplierService;
import com.supplychainrisk.service.RiskAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Management", description = "APIs for managing suppliers in the supply chain")
@CrossOrigin(origins = "*")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private RiskAssessmentService riskAssessmentService;
    
    @GetMapping
    @Operation(summary = "Get all suppliers with pagination and sorting")
    public ResponseEntity<Page<SupplierDTO>> getAllSuppliers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Page<SupplierDTO> suppliers = supplierService.getAllSuppliers(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }
    
    @GetMapping("/code/{supplierCode}")
    @Operation(summary = "Get supplier by supplier code")
    public ResponseEntity<SupplierDTO> getSupplierByCode(@PathVariable String supplierCode) {
        SupplierDTO supplier = supplierService.getSupplierByCode(supplierCode);
        return ResponseEntity.ok(supplier);
    }
    
    @PostMapping
    @Operation(summary = "Create new supplier")
    public ResponseEntity<SupplierDTO> createSupplier(
            @Valid @RequestBody SupplierDTO supplierDTO,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update existing supplier")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierDTO supplierDTO,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO, user.getId());
        return ResponseEntity.ok(updatedSupplier);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search suppliers with advanced filtering")
    public ResponseEntity<Page<SupplierDTO>> searchSuppliers(
            @Parameter(description = "Search term for name/code") @RequestParam(required = false) String searchTerm,
            @Parameter(description = "Filter by status") @RequestParam(required = false) Supplier.SupplierStatus status,
            @Parameter(description = "Filter by tier") @RequestParam(required = false) Supplier.SupplierTier tier,
            @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
            @Parameter(description = "Filter by industry") @RequestParam(required = false) String industry,
            @Parameter(description = "Minimum risk score") @RequestParam(required = false) Integer minRiskScore,
            @Parameter(description = "Maximum risk score") @RequestParam(required = false) Integer maxRiskScore,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Page<SupplierDTO> suppliers = supplierService.searchSuppliers(
            searchTerm, status, tier, country, industry, minRiskScore, maxRiskScore,
            page, size, sortBy, sortDirection);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get suppliers by status")
    public ResponseEntity<Page<SupplierDTO>> getSuppliersByStatus(
            @PathVariable Supplier.SupplierStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SupplierDTO> suppliers = supplierService.getSuppliersByStatus(status, pageable);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/tier/{tier}")
    @Operation(summary = "Get suppliers by tier")
    public ResponseEntity<Page<SupplierDTO>> getSuppliersByTier(
            @PathVariable Supplier.SupplierTier tier,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SupplierDTO> suppliers = supplierService.getSuppliersByTier(tier, pageable);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/preferred")
    @Operation(summary = "Get preferred suppliers")
    public ResponseEntity<Page<SupplierDTO>> getPreferredSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SupplierDTO> suppliers = supplierService.getPreferredSuppliers(pageable);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/strategic")
    @Operation(summary = "Get strategic suppliers")
    public ResponseEntity<Page<SupplierDTO>> getStrategicSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SupplierDTO> suppliers = supplierService.getStrategicSuppliers(pageable);
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/audit-required")
    @Operation(summary = "Get suppliers requiring audit")
    public ResponseEntity<List<SupplierDTO>> getSuppliersRequiringAudit() {
        List<SupplierDTO> suppliers = supplierService.getSuppliersRequiringAudit();
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get supplier statistics and analytics")
    public ResponseEntity<Map<String, Object>> getSupplierStatistics() {
        Map<String, Object> statistics = supplierService.getSupplierStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/countries")
    @Operation(summary = "Get distinct countries")
    public ResponseEntity<List<String>> getDistinctCountries() {
        List<String> countries = supplierService.getDistinctCountries();
        return ResponseEntity.ok(countries);
    }
    
    @GetMapping("/industries")
    @Operation(summary = "Get distinct industries")
    public ResponseEntity<List<String>> getDistinctIndustries() {
        List<String> industries = supplierService.getDistinctIndustries();
        return ResponseEntity.ok(industries);
    }
    
    @PostMapping("/{id}/recalculate-risk")
    @Operation(summary = "Recalculate risk scores for a supplier")
    public ResponseEntity<SupplierDTO> recalculateRiskScores(
            @PathVariable Long id,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        SupplierDTO supplier = supplierService.getSupplierById(id);
        
        // Convert to entity, recalculate, and update
        // This would typically be handled in the service layer
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplier, user.getId());
        return ResponseEntity.ok(updatedSupplier);
    }
    
    @GetMapping("/{id}/risk-assessment")
    @Operation(summary = "Get detailed risk assessment for a supplier")
    public ResponseEntity<Map<String, Object>> getSupplierRiskAssessment(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        
        Map<String, Object> riskAssessment = Map.of(
            "supplierId", supplier.getId(),
            "supplierName", supplier.getName(),
            "overallRiskScore", supplier.getOverallRiskScore(),
            "overallRiskLevel", riskAssessmentService.getRiskLevelDescription(supplier.getOverallRiskScore()),
            "overallRiskColor", riskAssessmentService.getRiskColor(supplier.getOverallRiskScore()),
            "financialRisk", Map.of(
                "score", supplier.getFinancialRiskScore(),
                "level", riskAssessmentService.getRiskLevelDescription(supplier.getFinancialRiskScore()),
                "color", riskAssessmentService.getRiskColor(supplier.getFinancialRiskScore())
            ),
            "operationalRisk", Map.of(
                "score", supplier.getOperationalRiskScore(),
                "level", riskAssessmentService.getRiskLevelDescription(supplier.getOperationalRiskScore()),
                "color", riskAssessmentService.getRiskColor(supplier.getOperationalRiskScore())
            ),
            "complianceRisk", Map.of(
                "score", supplier.getComplianceRiskScore(),
                "level", riskAssessmentService.getRiskLevelDescription(supplier.getComplianceRiskScore()),
                "color", riskAssessmentService.getRiskColor(supplier.getComplianceRiskScore())
            ),
            "geographicRisk", Map.of(
                "score", supplier.getGeographicRiskScore(),
                "level", riskAssessmentService.getRiskLevelDescription(supplier.getGeographicRiskScore()),
                "color", riskAssessmentService.getRiskColor(supplier.getGeographicRiskScore())
            )
        );
        
        return ResponseEntity.ok(riskAssessment);
    }
    
    @GetMapping("/{id}/recommendations")
    @Operation(summary = "Get risk-based recommendations for a supplier")
    public ResponseEntity<List<String>> getSupplierRecommendations(@PathVariable Long id) {
        SupplierDTO supplierDTO = supplierService.getSupplierById(id);
        
        // Convert DTO to entity for risk assessment (in real implementation, 
        // this should be handled properly in the service layer)
        // For now, we'll create a basic entity with the risk scores
        // This is a simplified approach for the MVP
        List<String> recommendations = List.of(
            "Monitor supplier performance regularly",
            "Establish clear communication channels",
            "Review contracts and terms annually"
        );
        
        return ResponseEntity.ok(recommendations);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", ex.getMessage()));
    }
    
    @ExceptionHandler(jakarta.validation.ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(jakarta.validation.ValidationException ex) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", ex.getMessage()));
    }
}