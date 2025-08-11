package com.supplychainrisk.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Request for ML prediction service
 */
@Data
@Builder
public class MLPredictionRequest {
    private String shipmentId;
    private String carrier;
    private Double distanceKm;
    private Integer routeComplexity; // 1-5 scale
    private Integer weatherRisk; // 1-5 scale
    private Integer priorityLevel; // 1-3 scale
    private Double supplierRiskScore; // 0-100 scale
    private String createdDate;
    private Integer geopoliticalRisk; // 1-5 scale
}