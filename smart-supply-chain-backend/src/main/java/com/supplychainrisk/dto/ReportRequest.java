package com.supplychainrisk.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for report generation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private String reportType; // "executive", "supplier", "risk"
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> includeMetrics;
    private String format; // "pdf", "excel", "html"
    private boolean includeCharts;
    private List<Long> supplierIds; // for supplier-specific reports
    private String timeZone;
    private String language;
}