package com.smartsupplychain.riskintelligence.controller;

import com.smartsupplychain.riskintelligence.dto.ApiResponse;
import com.smartsupplychain.riskintelligence.enums.RiskLevel;
import com.smartsupplychain.riskintelligence.model.RiskPrediction;
import com.smartsupplychain.riskintelligence.service.RiskPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/risk-predictions")
public class RiskPredictionController {

    @Autowired
    private RiskPredictionService riskPredictionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RiskPrediction>>> getAllPredictions() {
        List<RiskPrediction> predictions = riskPredictionService.getAllPredictions();
        return ResponseEntity.ok(ApiResponse.success(predictions));
    }

    @GetMapping("/shipment/{shipmentId}")
    public ResponseEntity<ApiResponse<List<RiskPrediction>>> getPredictionsByShipment(@PathVariable Long shipmentId) {
        List<RiskPrediction> predictions = riskPredictionService.getPredictionsByShipment(shipmentId);
        return ResponseEntity.ok(ApiResponse.success(predictions));
    }

    @GetMapping("/risk-level/{riskLevel}")
    public ResponseEntity<ApiResponse<List<RiskPrediction>>> getPredictionsByRiskLevel(@PathVariable RiskLevel riskLevel) {
        List<RiskPrediction> predictions = riskPredictionService.getPredictionsByRiskLevel(riskLevel);
        return ResponseEntity.ok(ApiResponse.success(predictions));
    }

    @GetMapping("/high-risk")
    public ResponseEntity<ApiResponse<List<RiskPrediction>>> getHighRiskPredictions() {
        List<RiskPrediction> predictions = riskPredictionService.getHighRiskPredictions();
        return ResponseEntity.ok(ApiResponse.success(predictions));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<RiskPrediction>>> getPredictionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<RiskPrediction> predictions = riskPredictionService.getPredictionsByDateRange(start, end);
        return ResponseEntity.ok(ApiResponse.success(predictions));
    }

    @PostMapping("/generate/{shipmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<RiskPrediction>> generatePrediction(@PathVariable Long shipmentId) {
        RiskPrediction prediction = riskPredictionService.generatePredictionForShipment(shipmentId);
        return ResponseEntity.ok(ApiResponse.success("Risk prediction generated", prediction));
    }

    @PostMapping("/generate-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> generatePredictionsForAllShipments() {
        riskPredictionService.generatePredictionsForAllActiveShipments();
        return ResponseEntity.ok(ApiResponse.success("Risk predictions generated for all active shipments"));
    }
}