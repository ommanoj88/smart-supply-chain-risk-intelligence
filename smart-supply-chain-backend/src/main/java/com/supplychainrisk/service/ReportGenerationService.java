package com.supplychainrisk.service;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.supplychainrisk.dto.ReportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * PDF Report Generation Service
 * Creates executive reports with analytics and insights
 */
@Service
public class ReportGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerationService.class);

    @Autowired
    private MLPredictionService mlPredictionService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ShipmentService shipmentService;

    /**
     * Generate executive summary report
     */
    public byte[] generateExecutiveReport(ReportRequest request) {
        try {
            logger.info("Generating executive report for period: {} to {}", 
                request.getStartDate(), request.getEndDate());

            Map<String, Object> reportData = gatherReportData(request);
            String htmlContent = generateExecutiveReportHtml(reportData);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            
            logger.info("Executive report generated successfully");
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error generating executive report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate executive report", e);
        }
    }

    /**
     * Generate supplier performance report
     */
    public byte[] generateSupplierReport(ReportRequest request) {
        try {
            logger.info("Generating supplier performance report");

            Map<String, Object> reportData = gatherSupplierReportData(request);
            String htmlContent = generateSupplierReportHtml(reportData);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            
            logger.info("Supplier performance report generated successfully");
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error generating supplier report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate supplier report", e);
        }
    }

    /**
     * Generate risk assessment report
     */
    public byte[] generateRiskReport(ReportRequest request) {
        try {
            logger.info("Generating risk assessment report");

            Map<String, Object> reportData = gatherRiskReportData(request);
            String htmlContent = generateRiskReportHtml(reportData);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            
            logger.info("Risk assessment report generated successfully");
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error generating risk report: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate risk report", e);
        }
    }

    /**
     * Gather data for executive report
     */
    private Map<String, Object> gatherReportData(ReportRequest request) {
        Map<String, Object> data = new HashMap<>();
        
        try {
            // Get basic metrics
            data.put("reportDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            data.put("periodStart", request.getStartDate());
            data.put("periodEnd", request.getEndDate());
            
            // Get shipment statistics
            // data.put("totalShipments", shipmentService.getShipmentCount(request.getStartDate(), request.getEndDate()));
            // data.put("onTimeDeliveryRate", shipmentService.getOnTimeDeliveryRate(request.getStartDate(), request.getEndDate()));
            data.put("totalShipments", 2847);
            data.put("onTimeDeliveryRate", 87.3);
            
            // Get supplier statistics
            // data.put("totalSuppliers", supplierService.getActiveSupplierCount());
            // data.put("supplierAnomalies", mlPredictionService.getSupplierAnomaliesCount());
            data.put("totalSuppliers", 156);
            data.put("supplierAnomalies", 3);
            
            // Get risk metrics
            data.put("riskDistribution", Map.of(
                "low", 45,
                "medium", 35,
                "high", 15,
                "critical", 5
            ));
            
            // Get predictive insights
            data.put("predictions", Map.of(
                "nextWeekRisk", 42.5,
                "delayProbability", 18.3,
                "costImpact", 125000
            ));
            
            // Get top risks
            data.put("topRisks", List.of(
                Map.of("type", "Weather Disruption", "probability", 65, "impact", "High"),
                Map.of("type", "Supplier Capacity", "probability", 45, "impact", "Medium"),
                Map.of("type", "Transportation Delay", "probability", 38, "impact", "Medium")
            ));
            
            // ML service status
            data.put("mlServiceHealthy", mlPredictionService.isMLServiceHealthy());
            
        } catch (Exception e) {
            logger.error("Error gathering report data: {}", e.getMessage(), e);
            // Use fallback data
            data.put("error", "Some data may be incomplete due to service unavailability");
        }
        
        return data;
    }

    /**
     * Gather supplier-specific report data
     */
    private Map<String, Object> gatherSupplierReportData(ReportRequest request) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("reportDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        data.put("reportType", "Supplier Performance Analysis");
        
        // Mock supplier data - in real implementation, this would come from database
        data.put("suppliers", List.of(
            Map.of("name", "Acme Manufacturing", "performance", 92.5, "risk", 15.2, "onTime", 94.1),
            Map.of("name", "Global Tech Solutions", "performance", 88.3, "risk", 22.7, "onTime", 91.2),
            Map.of("name", "Premier Components", "performance", 95.1, "risk", 8.4, "onTime", 97.3),
            Map.of("name", "Advanced Materials Co", "performance", 82.7, "risk", 31.5, "onTime", 86.8),
            Map.of("name", "Precision Parts Inc", "performance", 90.4, "risk", 18.9, "onTime", 92.7)
        ));
        
        return data;
    }

    /**
     * Gather risk-specific report data
     */
    private Map<String, Object> gatherRiskReportData(ReportRequest request) {
        Map<String, Object> data = new HashMap<>();
        
        data.put("reportDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        data.put("reportType", "Supply Chain Risk Assessment");
        
        // Mock risk data - in real implementation, this would come from ML service
        data.put("overallRiskScore", 42.3);
        data.put("riskTrend", "Increasing");
        data.put("riskFactors", List.of(
            Map.of("factor", "Geopolitical Events", "score", 68, "impact", "High"),
            Map.of("factor", "Weather Conditions", "score", 45, "impact", "Medium"),
            Map.of("factor", "Supplier Financial Health", "score", 32, "impact", "Medium"),
            Map.of("factor", "Transportation Network", "score", 28, "impact", "Low")
        ));
        
        return data;
    }

    /**
     * Generate HTML content for executive report
     */
    private String generateExecutiveReportHtml(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Executive Supply Chain Report</title>");
        html.append(getReportStyles());
        html.append("</head><body>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>Supply Chain Executive Report</h1>");
        html.append("<p class='report-date'>Generated on ").append(data.get("reportDate")).append("</p>");
        html.append("</div>");
        
        // Executive Summary
        html.append("<div class='section'>");
        html.append("<h2>Executive Summary</h2>");
        html.append("<div class='metrics-grid'>");
        
        html.append("<div class='metric-card'>");
        html.append("<h3>Total Shipments</h3>");
        html.append("<div class='metric-value'>").append(data.get("totalShipments")).append("</div>");
        html.append("</div>");
        
        html.append("<div class='metric-card'>");
        html.append("<h3>On-Time Delivery</h3>");
        html.append("<div class='metric-value'>").append(data.get("onTimeDeliveryRate")).append("%</div>");
        html.append("</div>");
        
        html.append("<div class='metric-card'>");
        html.append("<h3>Active Suppliers</h3>");
        html.append("<div class='metric-value'>").append(data.get("totalSuppliers")).append("</div>");
        html.append("</div>");
        
        html.append("<div class='metric-card'>");
        html.append("<h3>Supplier Anomalies</h3>");
        html.append("<div class='metric-value critical'>").append(data.get("supplierAnomalies")).append("</div>");
        html.append("</div>");
        
        html.append("</div>");
        html.append("</div>");
        
        // Risk Analysis
        html.append("<div class='section'>");
        html.append("<h2>Risk Analysis</h2>");
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> riskDist = (Map<String, Integer>) data.get("riskDistribution");
        
        html.append("<div class='risk-distribution'>");
        html.append("<h3>Risk Distribution</h3>");
        html.append("<div class='risk-bars'>");
        
        riskDist.forEach((level, percentage) -> {
            html.append("<div class='risk-bar'>");
            html.append("<span class='risk-label'>").append(level.toUpperCase()).append("</span>");
            html.append("<div class='risk-bar-container'>");
            html.append("<div class='risk-bar-fill ").append(level).append("' style='width: ").append(percentage).append("%'></div>");
            html.append("</div>");
            html.append("<span class='risk-percentage'>").append(percentage).append("%</span>");
            html.append("</div>");
        });
        
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        
        // Predictive Insights
        html.append("<div class='section'>");
        html.append("<h2>Predictive Insights</h2>");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> predictions = (Map<String, Object>) data.get("predictions");
        
        html.append("<div class='predictions'>");
        html.append("<div class='prediction-item'>");
        html.append("<h4>Next Week Risk Score</h4>");
        html.append("<div class='prediction-value'>").append(predictions.get("nextWeekRisk")).append("</div>");
        html.append("</div>");
        
        html.append("<div class='prediction-item'>");
        html.append("<h4>Delay Probability</h4>");
        html.append("<div class='prediction-value'>").append(predictions.get("delayProbability")).append("%</div>");
        html.append("</div>");
        
        html.append("<div class='prediction-item'>");
        html.append("<h4>Potential Cost Impact</h4>");
        html.append("<div class='prediction-value'>$").append(String.format("%,d", (Integer) predictions.get("costImpact"))).append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        
        // Top Risks
        html.append("<div class='section'>");
        html.append("<h2>Top Risk Factors</h2>");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topRisks = (List<Map<String, Object>>) data.get("topRisks");
        
        html.append("<table class='risks-table'>");
        html.append("<thead><tr><th>Risk Type</th><th>Probability</th><th>Impact</th></tr></thead>");
        html.append("<tbody>");
        
        topRisks.forEach(risk -> {
            html.append("<tr>");
            html.append("<td>").append(risk.get("type")).append("</td>");
            html.append("<td>").append(risk.get("probability")).append("%</td>");
            html.append("<td><span class='impact ").append(risk.get("impact").toString().toLowerCase()).append("'>")
                .append(risk.get("impact")).append("</span></td>");
            html.append("</tr>");
        });
        
        html.append("</tbody></table>");
        html.append("</div>");
        
        // Footer
        html.append("<div class='footer'>");
        html.append("<p>Smart Supply Chain Risk Intelligence Platform</p>");
        html.append("<p>ML Service Status: ").append((Boolean) data.get("mlServiceHealthy") ? "Healthy" : "Degraded").append("</p>");
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }

    /**
     * Generate HTML content for supplier report
     */
    private String generateSupplierReportHtml(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Supplier Performance Report</title>");
        html.append(getReportStyles());
        html.append("</head><body>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>").append(data.get("reportType")).append("</h1>");
        html.append("<p class='report-date'>Generated on ").append(data.get("reportDate")).append("</p>");
        html.append("</div>");
        
        // Supplier Performance Table
        html.append("<div class='section'>");
        html.append("<h2>Supplier Performance Metrics</h2>");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> suppliers = (List<Map<String, Object>>) data.get("suppliers");
        
        html.append("<table class='supplier-table'>");
        html.append("<thead><tr><th>Supplier Name</th><th>Performance Score</th><th>Risk Score</th><th>On-Time Delivery</th></tr></thead>");
        html.append("<tbody>");
        
        suppliers.forEach(supplier -> {
            html.append("<tr>");
            html.append("<td>").append(supplier.get("name")).append("</td>");
            html.append("<td class='performance'>").append(supplier.get("performance")).append("</td>");
            html.append("<td class='risk'>").append(supplier.get("risk")).append("</td>");
            html.append("<td>").append(supplier.get("onTime")).append("%</td>");
            html.append("</tr>");
        });
        
        html.append("</tbody></table>");
        html.append("</div>");
        
        html.append("<div class='footer'>");
        html.append("<p>Smart Supply Chain Risk Intelligence Platform</p>");
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }

    /**
     * Generate HTML content for risk report
     */
    private String generateRiskReportHtml(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<title>Risk Assessment Report</title>");
        html.append(getReportStyles());
        html.append("</head><body>");
        
        // Header
        html.append("<div class='header'>");
        html.append("<h1>").append(data.get("reportType")).append("</h1>");
        html.append("<p class='report-date'>Generated on ").append(data.get("reportDate")).append("</p>");
        html.append("</div>");
        
        // Risk Overview
        html.append("<div class='section'>");
        html.append("<h2>Risk Overview</h2>");
        html.append("<div class='risk-overview'>");
        html.append("<div class='overall-risk'>");
        html.append("<h3>Overall Risk Score</h3>");
        html.append("<div class='risk-score'>").append(data.get("overallRiskScore")).append("</div>");
        html.append("</div>");
        html.append("<div class='risk-trend'>");
        html.append("<h3>Trend</h3>");
        html.append("<div class='trend-value'>").append(data.get("riskTrend")).append("</div>");
        html.append("</div>");
        html.append("</div>");
        html.append("</div>");
        
        // Risk Factors
        html.append("<div class='section'>");
        html.append("<h2>Risk Factor Analysis</h2>");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> riskFactors = (List<Map<String, Object>>) data.get("riskFactors");
        
        html.append("<table class='risks-table'>");
        html.append("<thead><tr><th>Risk Factor</th><th>Score</th><th>Impact Level</th></tr></thead>");
        html.append("<tbody>");
        
        riskFactors.forEach(factor -> {
            html.append("<tr>");
            html.append("<td>").append(factor.get("factor")).append("</td>");
            html.append("<td>").append(factor.get("score")).append("</td>");
            html.append("<td><span class='impact ").append(factor.get("impact").toString().toLowerCase()).append("'>")
                .append(factor.get("impact")).append("</span></td>");
            html.append("</tr>");
        });
        
        html.append("</tbody></table>");
        html.append("</div>");
        
        html.append("<div class='footer'>");
        html.append("<p>Smart Supply Chain Risk Intelligence Platform</p>");
        html.append("</div>");
        
        html.append("</body></html>");
        
        return html.toString();
    }

    /**
     * Get CSS styles for reports
     */
    private String getReportStyles() {
        return "<style>" +
            "body { font-family: 'Arial', sans-serif; margin: 0; padding: 20px; color: #333; }" +
            ".header { text-align: center; border-bottom: 2px solid #1976d2; padding-bottom: 20px; margin-bottom: 30px; }" +
            ".header h1 { color: #1976d2; margin: 0; font-size: 28px; }" +
            ".report-date { color: #666; margin: 10px 0 0 0; }" +
            ".section { margin-bottom: 30px; }" +
            ".section h2 { color: #1976d2; border-bottom: 1px solid #ddd; padding-bottom: 10px; }" +
            ".metrics-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin: 20px 0; }" +
            ".metric-card { background: #f5f5f5; padding: 20px; border-radius: 8px; text-align: center; }" +
            ".metric-card h3 { margin: 0 0 10px 0; color: #666; font-size: 14px; }" +
            ".metric-value { font-size: 32px; font-weight: bold; color: #1976d2; }" +
            ".metric-value.critical { color: #d32f2f; }" +
            ".risk-distribution { margin: 20px 0; }" +
            ".risk-bars { margin: 20px 0; }" +
            ".risk-bar { display: flex; align-items: center; margin: 10px 0; }" +
            ".risk-label { width: 80px; font-weight: bold; text-transform: uppercase; }" +
            ".risk-bar-container { flex: 1; height: 20px; background: #eee; margin: 0 10px; border-radius: 10px; }" +
            ".risk-bar-fill { height: 100%; border-radius: 10px; }" +
            ".risk-bar-fill.low { background: #4caf50; }" +
            ".risk-bar-fill.medium { background: #ff9800; }" +
            ".risk-bar-fill.high { background: #f44336; }" +
            ".risk-bar-fill.critical { background: #d32f2f; }" +
            ".risk-percentage { width: 40px; text-align: right; font-weight: bold; }" +
            ".predictions { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; margin: 20px 0; }" +
            ".prediction-item { background: #f9f9f9; padding: 20px; border-radius: 8px; text-align: center; }" +
            ".prediction-item h4 { margin: 0 0 10px 0; color: #666; }" +
            ".prediction-value { font-size: 24px; font-weight: bold; color: #ff9800; }" +
            ".risks-table, .supplier-table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
            ".risks-table th, .risks-table td, .supplier-table th, .supplier-table td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }" +
            ".risks-table th, .supplier-table th { background: #f5f5f5; font-weight: bold; }" +
            ".impact.high { background: #ffebee; color: #d32f2f; padding: 4px 8px; border-radius: 4px; }" +
            ".impact.medium { background: #fff3e0; color: #f57c00; padding: 4px 8px; border-radius: 4px; }" +
            ".impact.low { background: #e8f5e8; color: #388e3c; padding: 4px 8px; border-radius: 4px; }" +
            ".performance { color: #4caf50; font-weight: bold; }" +
            ".risk { color: #f44336; font-weight: bold; }" +
            ".risk-overview { display: grid; grid-template-columns: repeat(2, 1fr); gap: 30px; margin: 20px 0; }" +
            ".overall-risk, .risk-trend { background: #f9f9f9; padding: 20px; border-radius: 8px; text-align: center; }" +
            ".risk-score { font-size: 36px; font-weight: bold; color: #ff9800; }" +
            ".trend-value { font-size: 24px; font-weight: bold; color: #f44336; }" +
            ".footer { text-align: center; margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; color: #666; }" +
            "</style>";
    }
}