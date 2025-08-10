package com.smartsupplychain.riskintelligence.service;

import com.smartsupplychain.riskintelligence.enums.RiskLevel;
import com.smartsupplychain.riskintelligence.model.RiskPrediction;
import com.smartsupplychain.riskintelligence.model.Shipment;
import com.smartsupplychain.riskintelligence.repository.RiskPredictionRepository;
import com.smartsupplychain.riskintelligence.repository.ShipmentRepository;
import com.smartsupplychain.riskintelligence.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class RiskPredictionService {

    @Autowired
    private RiskPredictionRepository riskPredictionRepository;

    @Autowired
    private ShipmentRepository shipmentRepository;

    private final Random random = new Random();

    public List<RiskPrediction> getAllPredictions() {
        return riskPredictionRepository.findAll();
    }

    public List<RiskPrediction> getPredictionsByShipment(Long shipmentId) {
        return riskPredictionRepository.findByShipmentIdOrderByCreatedAtDesc(shipmentId);
    }

    public List<RiskPrediction> getPredictionsByRiskLevel(RiskLevel riskLevel) {
        return riskPredictionRepository.findByRiskLevel(riskLevel);
    }

    public List<RiskPrediction> getHighRiskPredictions() {
        List<RiskLevel> highRiskLevels = Arrays.asList(RiskLevel.HIGH, RiskLevel.CRITICAL);
        return riskPredictionRepository.findByRiskLevelsOrderByCreatedAtDesc(highRiskLevels);
    }

    public List<RiskPrediction> getPredictionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return riskPredictionRepository.findByCreatedAtBetween(start, end);
    }

    public RiskPrediction generatePredictionForShipment(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment", "id", shipmentId));

        return generatePredictionForShipment(shipment);
    }

    public RiskPrediction generatePredictionForShipment(Shipment shipment) {
        // Mock AI/ML risk prediction algorithm
        // In a real application, this would use machine learning models
        // considering factors like supplier history, weather, geopolitical events, etc.
        
        RiskAssessment assessment = assessRisk(shipment);
        
        RiskPrediction prediction = new RiskPrediction();
        prediction.setShipment(shipment);
        prediction.setRiskLevel(assessment.riskLevel);
        prediction.setProbability(assessment.probability);
        prediction.setDescription(assessment.description);
        prediction.setPredictionFactors(assessment.factors);
        
        return riskPredictionRepository.save(prediction);
    }

    public void generatePredictionsForAllActiveShipments() {
        // Generate predictions for all shipments that don't have recent predictions
        List<Shipment> shipments = shipmentRepository.findAll();
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
        
        for (Shipment shipment : shipments) {
            List<RiskPrediction> recentPredictions = riskPredictionRepository
                    .findByShipmentIdOrderByCreatedAtDesc(shipment.getId());
            
            boolean hasRecentPrediction = recentPredictions.stream()
                    .anyMatch(p -> p.getCreatedAt().isAfter(cutoffTime));
            
            if (!hasRecentPrediction) {
                generatePredictionForShipment(shipment);
            }
        }
    }

    private RiskAssessment assessRisk(Shipment shipment) {
        BigDecimal baseRiskScore = BigDecimal.ZERO;
        StringBuilder factors = new StringBuilder();
        StringBuilder description = new StringBuilder();

        // Factor 1: Supplier risk score
        BigDecimal supplierRisk = shipment.getSupplier().getRiskScore();
        baseRiskScore = baseRiskScore.add(supplierRisk.multiply(new BigDecimal("0.4")));
        factors.append("Supplier Risk Score: ").append(supplierRisk).append("; ");

        // Factor 2: Time to delivery
        if (shipment.getEstimatedArrival() != null) {
            long daysUntilDelivery = ChronoUnit.DAYS.between(LocalDateTime.now(), shipment.getEstimatedArrival());
            if (daysUntilDelivery < 7) {
                baseRiskScore = baseRiskScore.add(new BigDecimal("2.0"));
                factors.append("Short delivery time (").append(daysUntilDelivery).append(" days); ");
            } else if (daysUntilDelivery > 30) {
                baseRiskScore = baseRiskScore.add(new BigDecimal("1.0"));
                factors.append("Long delivery time (").append(daysUntilDelivery).append(" days); ");
            }
        }

        // Factor 3: Geographic risk (simplified)
        String origin = shipment.getOrigin().toLowerCase();
        String destination = shipment.getDestination().toLowerCase();
        
        if (origin.contains("china") || origin.contains("bangladesh") || origin.contains("india")) {
            baseRiskScore = baseRiskScore.add(new BigDecimal("1.5"));
            factors.append("High-risk origin region; ");
        }
        
        if (destination.contains("middle east") || destination.contains("africa")) {
            baseRiskScore = baseRiskScore.add(new BigDecimal("1.0"));
            factors.append("High-risk destination region; ");
        }

        // Factor 4: Random environmental factors (weather, political, etc.)
        double environmentalFactor = random.nextGaussian() * 1.5;
        baseRiskScore = baseRiskScore.add(new BigDecimal(environmentalFactor));
        factors.append("Environmental factors: ").append(String.format("%.2f", environmentalFactor)).append("; ");

        // Normalize score to 0-10 range
        if (baseRiskScore.compareTo(BigDecimal.ZERO) < 0) {
            baseRiskScore = BigDecimal.ZERO;
        } else if (baseRiskScore.compareTo(new BigDecimal("10")) > 0) {
            baseRiskScore = new BigDecimal("10");
        }

        baseRiskScore = baseRiskScore.setScale(2, RoundingMode.HALF_UP);

        // Determine risk level and probability
        RiskLevel riskLevel;
        BigDecimal probability;

        if (baseRiskScore.compareTo(new BigDecimal("8.0")) >= 0) {
            riskLevel = RiskLevel.CRITICAL;
            probability = new BigDecimal("0.85").add(new BigDecimal(random.nextDouble() * 0.15));
            description.append("Critical risk level. High probability of significant delays or disruptions.");
        } else if (baseRiskScore.compareTo(new BigDecimal("6.0")) >= 0) {
            riskLevel = RiskLevel.HIGH;
            probability = new BigDecimal("0.65").add(new BigDecimal(random.nextDouble() * 0.20));
            description.append("High risk level. Possible delays or minor disruptions expected.");
        } else if (baseRiskScore.compareTo(new BigDecimal("3.0")) >= 0) {
            riskLevel = RiskLevel.MEDIUM;
            probability = new BigDecimal("0.35").add(new BigDecimal(random.nextDouble() * 0.30));
            description.append("Medium risk level. Some monitoring recommended.");
        } else {
            riskLevel = RiskLevel.LOW;
            probability = new BigDecimal("0.05").add(new BigDecimal(random.nextDouble() * 0.25));
            description.append("Low risk level. Shipment expected to proceed smoothly.");
        }

        probability = probability.setScale(4, RoundingMode.HALF_UP);

        return new RiskAssessment(riskLevel, probability, description.toString(), factors.toString());
    }

    private static class RiskAssessment {
        final RiskLevel riskLevel;
        final BigDecimal probability;
        final String description;
        final String factors;

        RiskAssessment(RiskLevel riskLevel, BigDecimal probability, String description, String factors) {
            this.riskLevel = riskLevel;
            this.probability = probability;
            this.description = description;
            this.factors = factors;
        }
    }
}