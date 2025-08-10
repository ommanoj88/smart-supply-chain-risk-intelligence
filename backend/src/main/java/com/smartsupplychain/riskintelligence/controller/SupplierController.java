package com.smartsupplychain.riskintelligence.controller;

import com.smartsupplychain.riskintelligence.dto.ApiResponse;
import com.smartsupplychain.riskintelligence.dto.SupplierDto;
import com.smartsupplychain.riskintelligence.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierDto>>> getAllSuppliers() {
        List<SupplierDto> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SupplierDto>>> getActiveSuppliers() {
        List<SupplierDto> suppliers = supplierService.getActiveSuppliers();
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDto>> getSupplierById(@PathVariable Long id) {
        SupplierDto supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(ApiResponse.success(supplier));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SupplierDto>>> searchSuppliers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location) {
        
        List<SupplierDto> suppliers;
        if (name != null && !name.trim().isEmpty()) {
            suppliers = supplierService.searchSuppliersByName(name);
        } else if (location != null && !location.trim().isEmpty()) {
            suppliers = supplierService.getSuppliersByLocation(location);
        } else {
            suppliers = supplierService.getActiveSuppliers();
        }
        
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }

    @GetMapping("/risk")
    public ResponseEntity<ApiResponse<List<SupplierDto>>> getSuppliersByRisk(
            @RequestParam(required = false) BigDecimal minRisk,
            @RequestParam(required = false) BigDecimal maxRisk) {
        
        List<SupplierDto> suppliers;
        if (minRisk != null && maxRisk != null) {
            suppliers = supplierService.getSuppliersByRiskRange(minRisk, maxRisk);
        } else {
            suppliers = supplierService.getHighRiskSuppliers();
        }
        
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }

    @GetMapping("/high-risk")
    public ResponseEntity<ApiResponse<List<SupplierDto>>> getHighRiskSuppliers() {
        List<SupplierDto> suppliers = supplierService.getHighRiskSuppliers();
        return ResponseEntity.ok(ApiResponse.success(suppliers));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<SupplierDto>> createSupplier(@Valid @RequestBody SupplierDto supplierDto) {
        SupplierDto createdSupplier = supplierService.createSupplier(supplierDto);
        return ResponseEntity.ok(ApiResponse.success("Supplier created successfully", createdSupplier));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<SupplierDto>> updateSupplier(
            @PathVariable Long id, 
            @Valid @RequestBody SupplierDto supplierDto) {
        SupplierDto updatedSupplier = supplierService.updateSupplier(id, supplierDto);
        return ResponseEntity.ok(ApiResponse.success("Supplier updated successfully", updatedSupplier));
    }

    @PutMapping("/{id}/risk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<SupplierDto>> updateSupplierRisk(
            @PathVariable Long id, 
            @RequestParam BigDecimal riskScore) {
        SupplierDto updatedSupplier = supplierService.updateRiskScore(id, riskScore);
        return ResponseEntity.ok(ApiResponse.success("Supplier risk score updated", updatedSupplier));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(ApiResponse.success("Supplier deactivated successfully"));
    }
}