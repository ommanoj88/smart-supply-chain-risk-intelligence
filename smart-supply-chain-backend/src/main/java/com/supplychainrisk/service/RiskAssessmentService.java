package com.supplychainrisk.service;

import com.supplychainrisk.entity.Supplier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class RiskAssessmentService {
    
    // Risk weight factors (totaling 100%)
    private static final double FINANCIAL_WEIGHT = 0.25;
    private static final double OPERATIONAL_WEIGHT = 0.30;
    private static final double COMPLIANCE_WEIGHT = 0.25;
    private static final double GEOGRAPHIC_WEIGHT = 0.20;
    
    // High-risk countries (simplified list)
    private static final Set<String> HIGH_RISK_COUNTRIES = Set.of(
        "Afghanistan", "Iran", "North Korea", "Syria", "Yemen", "Somalia", "Libya"
    );
    
    // Medium-risk countries
    private static final Set<String> MEDIUM_RISK_COUNTRIES = Set.of(
        "Venezuela", "Belarus", "Myanmar", "Russia", "China", "Pakistan"
    );
    
    /**
     * Calculate all risk scores for a supplier
     */
    public void calculateRiskScores(Supplier supplier) {
        int financialRisk = calculateFinancialRiskScore(supplier);
        int operationalRisk = calculateOperationalRiskScore(supplier);
        int complianceRisk = calculateComplianceRiskScore(supplier);
        int geographicRisk = calculateGeographicRiskScore(supplier);
        
        supplier.setFinancialRiskScore(financialRisk);
        supplier.setOperationalRiskScore(operationalRisk);
        supplier.setComplianceRiskScore(complianceRisk);
        supplier.setGeographicRiskScore(geographicRisk);
        
        // Calculate overall risk score as weighted average
        double overallRisk = (financialRisk * FINANCIAL_WEIGHT) +
                           (operationalRisk * OPERATIONAL_WEIGHT) +
                           (complianceRisk * COMPLIANCE_WEIGHT) +
                           (geographicRisk * GEOGRAPHIC_WEIGHT);
        
        supplier.setOverallRiskScore((int) Math.round(overallRisk));
    }
    
    /**
     * Calculate financial risk score (0-100, higher is riskier)
     */
    private int calculateFinancialRiskScore(Supplier supplier) {
        int score = 0;
        
        // Credit rating assessment
        String creditRating = supplier.getCreditRating();
        if (creditRating != null) {
            switch (creditRating.toUpperCase()) {
                case "AAA", "AA+", "AA", "AA-" -> score += 5;  // Excellent
                case "A+", "A", "A-" -> score += 15;          // Good
                case "BBB+", "BBB", "BBB-" -> score += 25;    // Fair
                case "BB+", "BB", "BB-" -> score += 40;       // Speculative
                case "B+", "B", "B-" -> score += 60;          // Highly speculative
                default -> score += 80;                       // Default/Unknown
            }
        } else {
            score += 50; // No credit rating available
        }
        
        // Annual revenue assessment (stability indicator)
        BigDecimal revenue = supplier.getAnnualRevenue();
        if (revenue != null) {
            if (revenue.compareTo(BigDecimal.valueOf(1000000000)) >= 0) { // >= $1B
                score += 5;  // Very stable
            } else if (revenue.compareTo(BigDecimal.valueOf(100000000)) >= 0) { // >= $100M
                score += 10; // Stable
            } else if (revenue.compareTo(BigDecimal.valueOf(10000000)) >= 0) { // >= $10M
                score += 20; // Moderate
            } else if (revenue.compareTo(BigDecimal.valueOf(1000000)) >= 0) { // >= $1M
                score += 30; // Small but viable
            } else {
                score += 40; // Very small/risky
            }
        } else {
            score += 30; // Unknown revenue
        }
        
        // Years in business (experience/stability)
        Integer yearsInBusiness = supplier.getYearsInBusiness();
        if (yearsInBusiness != null) {
            if (yearsInBusiness >= 20) {
                score += 5;  // Very established
            } else if (yearsInBusiness >= 10) {
                score += 10; // Established
            } else if (yearsInBusiness >= 5) {
                score += 20; // Moderately established
            } else if (yearsInBusiness >= 2) {
                score += 30; // New but operational
            } else {
                score += 40; // Very new/risky
            }
        } else {
            score += 25; // Unknown
        }
        
        return Math.min(100, score);
    }
    
    /**
     * Calculate operational risk score (0-100, higher is riskier)
     */
    private int calculateOperationalRiskScore(Supplier supplier) {
        int score = 0;
        
        // On-time delivery performance
        BigDecimal deliveryRate = supplier.getOnTimeDeliveryRate();
        if (deliveryRate != null) {
            if (deliveryRate.compareTo(BigDecimal.valueOf(95)) >= 0) {
                score += 5;  // Excellent
            } else if (deliveryRate.compareTo(BigDecimal.valueOf(90)) >= 0) {
                score += 15; // Good
            } else if (deliveryRate.compareTo(BigDecimal.valueOf(80)) >= 0) {
                score += 30; // Fair
            } else if (deliveryRate.compareTo(BigDecimal.valueOf(70)) >= 0) {
                score += 50; // Poor
            } else {
                score += 70; // Very poor
            }
        } else {
            score += 40; // No data
        }
        
        // Quality rating
        BigDecimal qualityRating = supplier.getQualityRating();
        if (qualityRating != null) {
            if (qualityRating.compareTo(BigDecimal.valueOf(9)) >= 0) {
                score += 5;  // Excellent
            } else if (qualityRating.compareTo(BigDecimal.valueOf(8)) >= 0) {
                score += 15; // Good
            } else if (qualityRating.compareTo(BigDecimal.valueOf(7)) >= 0) {
                score += 25; // Fair
            } else if (qualityRating.compareTo(BigDecimal.valueOf(6)) >= 0) {
                score += 40; // Poor
            } else {
                score += 60; // Very poor
            }
        } else {
            score += 30; // No data
        }
        
        // Employee count (capacity indicator)
        Integer employeeCount = supplier.getEmployeeCount();
        if (employeeCount != null) {
            if (employeeCount >= 1000) {
                score += 5;  // Large capacity
            } else if (employeeCount >= 100) {
                score += 10; // Medium capacity
            } else if (employeeCount >= 50) {
                score += 20; // Small but adequate
            } else if (employeeCount >= 10) {
                score += 30; // Very small
            } else {
                score += 40; // Micro/inadequate
            }
        } else {
            score += 25; // Unknown
        }
        
        return Math.min(100, score);
    }
    
    /**
     * Calculate compliance risk score (0-100, higher is riskier)
     */
    private int calculateComplianceRiskScore(Supplier supplier) {
        int score = 0;
        
        // ISO certifications
        List<String> isoCertifications = supplier.getIsoCertifications();
        if (isoCertifications != null && !isoCertifications.isEmpty()) {
            int certCount = isoCertifications.size();
            if (certCount >= 5) {
                score += 5;  // Excellent compliance
            } else if (certCount >= 3) {
                score += 15; // Good compliance
            } else if (certCount >= 1) {
                score += 25; // Basic compliance
            }
        } else {
            score += 40; // No ISO certifications
        }
        
        // Compliance certifications
        List<String> complianceCerts = supplier.getComplianceCertifications();
        if (complianceCerts != null && !complianceCerts.isEmpty()) {
            int certCount = complianceCerts.size();
            if (certCount >= 3) {
                score += 10; // Strong compliance
            } else if (certCount >= 1) {
                score += 20; // Basic compliance
            }
        } else {
            score += 30; // No compliance certifications
        }
        
        // Audit status
        LocalDate lastAudit = supplier.getLastAuditDate();
        LocalDate nextAudit = supplier.getNextAuditDueDate();
        
        if (lastAudit != null) {
            long daysSinceAudit = ChronoUnit.DAYS.between(lastAudit, LocalDate.now());
            if (daysSinceAudit <= 365) {
                score += 5;  // Recent audit
            } else if (daysSinceAudit <= 730) {
                score += 15; // Moderately recent
            } else {
                score += 30; // Old audit
            }
        } else {
            score += 40; // No audit history
        }
        
        if (nextAudit != null) {
            long daysToAudit = ChronoUnit.DAYS.between(LocalDate.now(), nextAudit);
            if (daysToAudit < 0) {
                score += 30; // Overdue audit
            } else if (daysToAudit <= 30) {
                score += 10; // Due soon
            } else {
                score += 5;  // Scheduled appropriately
            }
        } else {
            score += 20; // No audit scheduled
        }
        
        return Math.min(100, score);
    }
    
    /**
     * Calculate geographic risk score (0-100, higher is riskier)
     */
    private int calculateGeographicRiskScore(Supplier supplier) {
        int score = 0;
        String country = supplier.getCountry();
        
        if (country != null) {
            String normalizedCountry = country.trim();
            
            if (HIGH_RISK_COUNTRIES.contains(normalizedCountry)) {
                score += 80; // Very high risk
            } else if (MEDIUM_RISK_COUNTRIES.contains(normalizedCountry)) {
                score += 50; // Medium risk
            } else {
                // Low risk countries (US, EU, developed nations)
                switch (normalizedCountry.toLowerCase()) {
                    case "united states", "usa", "us", "canada", "germany", "france", 
                         "united kingdom", "uk", "japan", "australia", "netherlands", 
                         "switzerland", "sweden", "norway", "denmark" -> score += 10;
                    default -> score += 25; // Other countries - moderate risk
                }
            }
        } else {
            score += 50; // Unknown location
        }
        
        // Additional geographic factors could be added here:
        // - Natural disaster risk
        // - Political stability
        // - Economic indicators
        // - Trade restrictions
        
        return Math.min(100, score);
    }
    
    /**
     * Get risk level description based on score
     */
    public String getRiskLevelDescription(int riskScore) {
        if (riskScore <= 20) {
            return "Very Low Risk";
        } else if (riskScore <= 40) {
            return "Low Risk";
        } else if (riskScore <= 60) {
            return "Medium Risk";
        } else if (riskScore <= 80) {
            return "High Risk";
        } else {
            return "Very High Risk";
        }
    }
    
    /**
     * Get risk color for UI display
     */
    public String getRiskColor(int riskScore) {
        if (riskScore <= 20) {
            return "#22C55E"; // Green
        } else if (riskScore <= 40) {
            return "#84CC16"; // Light green
        } else if (riskScore <= 60) {
            return "#F59E0B"; // Yellow
        } else if (riskScore <= 80) {
            return "#F97316"; // Orange
        } else {
            return "#EF4444"; // Red
        }
    }
    
    /**
     * Get recommendations based on risk assessment
     */
    public List<String> getRiskRecommendations(Supplier supplier) {
        List<String> recommendations = new ArrayList<>();
        
        if (supplier.getFinancialRiskScore() > 60) {
            recommendations.add("Consider requesting financial statements and credit references");
            recommendations.add("Implement shorter payment terms or require advance payments");
        }
        
        if (supplier.getOperationalRiskScore() > 60) {
            recommendations.add("Establish performance monitoring and regular check-ins");
            recommendations.add("Consider backup suppliers for critical items");
        }
        
        if (supplier.getComplianceRiskScore() > 60) {
            recommendations.add("Request compliance documentation and certifications");
            recommendations.add("Schedule compliance audit as soon as possible");
        }
        
        if (supplier.getGeographicRiskScore() > 60) {
            recommendations.add("Implement additional monitoring for political and economic stability");
            recommendations.add("Consider diversifying suppliers across different regions");
        }
        
        if (supplier.getOverallRiskScore() > 70) {
            recommendations.add("Consider downgrading supplier tier or finding alternatives");
            recommendations.add("Implement enhanced monitoring and reporting");
        }
        
        return recommendations;
    }
}