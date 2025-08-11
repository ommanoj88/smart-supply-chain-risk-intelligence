package com.supplychainrisk.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response from ML prediction service
 */
@Data
@Builder
public class MLPredictionResponse {
    private Double predictedDelayHours;
    private Double confidenceScore;
    private String riskLevel;
    private List<String> recommendations;
    private boolean success;
    private boolean fallback;
    private String error;
}