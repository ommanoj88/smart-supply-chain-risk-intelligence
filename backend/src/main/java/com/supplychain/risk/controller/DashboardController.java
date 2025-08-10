package com.supplychain.risk.controller;

import com.supplychain.risk.dto.ApiResponse;
import com.supplychain.risk.dto.DashboardStatsDto;
import com.supplychain.risk.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics and analytics endpoints")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER', 'VIEWER')")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        try {
            DashboardStatsDto stats = dashboardService.getDashboardStats();
            return ResponseEntity.ok(ApiResponse.success("Dashboard statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve dashboard statistics", e.getMessage()));
        }
    }
    
    @GetMapping("/stats/filtered")
    @Operation(summary = "Get filtered dashboard statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER', 'VIEWER')")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getFilteredStats(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) String region) {
        try {
            DashboardStatsDto stats = dashboardService.getFilteredStats(timeRange, region);
            return ResponseEntity.ok(ApiResponse.success("Filtered dashboard statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Failed to retrieve filtered dashboard statistics", e.getMessage()));
        }
    }
}