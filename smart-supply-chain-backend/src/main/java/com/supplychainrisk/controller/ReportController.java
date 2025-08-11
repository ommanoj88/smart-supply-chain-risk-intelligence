package com.supplychainrisk.controller;

import com.supplychainrisk.dto.ReportRequest;
import com.supplychainrisk.service.ReportGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * REST Controller for report generation
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "PDF report generation and analytics")
@CrossOrigin(origins = "${cors.allowed.origins}")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ReportGenerationService reportGenerationService;

    @PostMapping("/executive")
    @Operation(summary = "Generate executive summary report")
    @ApiResponse(responseCode = "200", description = "Executive report generated successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ByteArrayResource> generateExecutiveReport(@RequestBody ReportRequest request) {
        logger.info("Generating executive report for user request");
        
        try {
            byte[] pdfData = reportGenerationService.generateExecutiveReport(request);
            
            String filename = String.format("Executive_Report_%s.pdf", 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            ByteArrayResource resource = new ByteArrayResource(pdfData);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfData.length)
                .body(resource);
                
        } catch (Exception e) {
            logger.error("Error generating executive report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/supplier")
    @Operation(summary = "Generate supplier performance report")
    @ApiResponse(responseCode = "200", description = "Supplier report generated successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ByteArrayResource> generateSupplierReport(@RequestBody ReportRequest request) {
        logger.info("Generating supplier performance report");
        
        try {
            byte[] pdfData = reportGenerationService.generateSupplierReport(request);
            
            String filename = String.format("Supplier_Report_%s.pdf", 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            ByteArrayResource resource = new ByteArrayResource(pdfData);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfData.length)
                .body(resource);
                
        } catch (Exception e) {
            logger.error("Error generating supplier report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/risk")
    @Operation(summary = "Generate risk assessment report")
    @ApiResponse(responseCode = "200", description = "Risk report generated successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ByteArrayResource> generateRiskReport(@RequestBody ReportRequest request) {
        logger.info("Generating risk assessment report");
        
        try {
            byte[] pdfData = reportGenerationService.generateRiskReport(request);
            
            String filename = String.format("Risk_Report_%s.pdf", 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            ByteArrayResource resource = new ByteArrayResource(pdfData);
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfData.length)
                .body(resource);
                
        } catch (Exception e) {
            logger.error("Error generating risk report: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/templates")
    @Operation(summary = "Get available report templates")
    @ApiResponse(responseCode = "200", description = "Report templates retrieved successfully")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER') or hasRole('VIEWER')")
    public ResponseEntity<Object> getReportTemplates() {
        try {
            var templates = java.util.Map.of(
                "executive", java.util.Map.of(
                    "name", "Executive Summary Report",
                    "description", "High-level overview of supply chain performance and risks",
                    "requiredFields", java.util.List.of("startDate", "endDate"),
                    "optionalFields", java.util.List.of("includeCharts", "timeZone")
                ),
                "supplier", java.util.Map.of(
                    "name", "Supplier Performance Report", 
                    "description", "Detailed analysis of supplier performance metrics",
                    "requiredFields", java.util.List.of("startDate", "endDate"),
                    "optionalFields", java.util.List.of("supplierIds", "includeCharts")
                ),
                "risk", java.util.Map.of(
                    "name", "Risk Assessment Report",
                    "description", "Comprehensive risk analysis and predictions",
                    "requiredFields", java.util.List.of("startDate", "endDate"),
                    "optionalFields", java.util.List.of("includeCharts", "timeZone")
                )
            );
            
            return ResponseEntity.ok(templates);
            
        } catch (Exception e) {
            logger.error("Error retrieving report templates: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}