package com.supplychainrisk.service;

import com.supplychainrisk.entity.RiskAlert;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.repository.RiskAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RiskAlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(RiskAlertService.class);
    
    @Autowired
    private RiskAlertRepository riskAlertRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public RiskAlert createRiskAlert(String alertType, RiskAlert.Severity severity, String title, 
                                   String description, String sourceType, Long sourceId) {
        RiskAlert alert = new RiskAlert(alertType, severity, title, description);
        alert.setSourceType(sourceType);
        alert.setSourceId(sourceId);
        
        RiskAlert savedAlert = riskAlertRepository.save(alert);
        
        // Send notifications for high and critical alerts
        if (severity == RiskAlert.Severity.HIGH || severity == RiskAlert.Severity.CRITICAL) {
            notificationService.sendRiskAlertNotification(savedAlert);
        }
        
        logger.info("Created risk alert: {} with severity: {}", title, severity);
        return savedAlert;
    }
    
    public Optional<RiskAlert> getRiskAlert(Long id) {
        return riskAlertRepository.findById(id);
    }
    
    public Page<RiskAlert> getAllRiskAlerts(Pageable pageable) {
        return riskAlertRepository.findAll(pageable);
    }
    
    public Page<RiskAlert> getRiskAlertsByStatus(RiskAlert.Status status, Pageable pageable) {
        return riskAlertRepository.findByStatus(status, pageable);
    }
    
    public Page<RiskAlert> getRiskAlertsBySeverity(RiskAlert.Severity severity, Pageable pageable) {
        return riskAlertRepository.findBySeverity(severity, pageable);
    }
    
    public List<RiskAlert> getActiveCriticalAlerts() {
        return riskAlertRepository.findActiveCriticalAlerts();
    }
    
    public RiskAlert acknowledgeAlert(Long alertId, User acknowledgedBy) {
        Optional<RiskAlert> optionalAlert = riskAlertRepository.findById(alertId);
        if (optionalAlert.isPresent()) {
            RiskAlert alert = optionalAlert.get();
            alert.setStatus(RiskAlert.Status.ACKNOWLEDGED);
            alert.setAcknowledgedBy(acknowledgedBy);
            alert.setAcknowledgedAt(LocalDateTime.now());
            
            RiskAlert savedAlert = riskAlertRepository.save(alert);
            logger.info("Risk alert {} acknowledged by user {}", alertId, acknowledgedBy.getId());
            return savedAlert;
        }
        throw new RuntimeException("Risk alert not found with id: " + alertId);
    }
    
    public RiskAlert resolveAlert(Long alertId, User resolvedBy, String resolutionNotes) {
        Optional<RiskAlert> optionalAlert = riskAlertRepository.findById(alertId);
        if (optionalAlert.isPresent()) {
            RiskAlert alert = optionalAlert.get();
            alert.setStatus(RiskAlert.Status.RESOLVED);
            alert.setResolvedBy(resolvedBy);
            alert.setResolvedAt(LocalDateTime.now());
            alert.setResolutionNotes(resolutionNotes);
            
            RiskAlert savedAlert = riskAlertRepository.save(alert);
            logger.info("Risk alert {} resolved by user {}", alertId, resolvedBy.getId());
            return savedAlert;
        }
        throw new RuntimeException("Risk alert not found with id: " + alertId);
    }
    
    public void dismissAlert(Long alertId, User dismissedBy) {
        Optional<RiskAlert> optionalAlert = riskAlertRepository.findById(alertId);
        if (optionalAlert.isPresent()) {
            RiskAlert alert = optionalAlert.get();
            alert.setStatus(RiskAlert.Status.DISMISSED);
            riskAlertRepository.save(alert);
            logger.info("Risk alert {} dismissed by user {}", alertId, dismissedBy.getId());
        } else {
            throw new RuntimeException("Risk alert not found with id: " + alertId);
        }
    }
    
    public long getActiveAlertCount() {
        return riskAlertRepository.countByStatusSince(RiskAlert.Status.ACTIVE, LocalDateTime.now().minusDays(30));
    }
    
    public List<Object[]> getAlertCountBySeverity() {
        return riskAlertRepository.countActiveBySeverity();
    }
    
    // Create supplier-specific risk alerts
    public RiskAlert createSupplierRiskAlert(Long supplierId, String alertType, RiskAlert.Severity severity, 
                                           String title, String description, Integer riskScore) {
        RiskAlert alert = createRiskAlert(alertType, severity, title, description, "SUPPLIER", supplierId);
        alert.setRiskScore(riskScore);
        return riskAlertRepository.save(alert);
    }
    
    // Create shipment-specific risk alerts
    public RiskAlert createShipmentRiskAlert(Long shipmentId, String alertType, RiskAlert.Severity severity, 
                                            String title, String description) {
        return createRiskAlert(alertType, severity, title, description, "SHIPMENT", shipmentId);
    }
    
    // Evaluate supplier risk and create alerts if needed
    public void evaluateSupplierRisk(Long supplierId, Integer currentRiskScore, Integer previousRiskScore) {
        if (currentRiskScore > 75 && (previousRiskScore == null || previousRiskScore <= 75)) {
            createSupplierRiskAlert(supplierId, "SUPPLIER_HIGH_RISK", RiskAlert.Severity.HIGH,
                    "Supplier Risk Score Exceeded Threshold", 
                    "Supplier risk score has increased above the critical threshold of 75", 
                    currentRiskScore);
        } else if (currentRiskScore > 50 && currentRiskScore > previousRiskScore + 15) {
            createSupplierRiskAlert(supplierId, "SUPPLIER_RISK_INCREASE", RiskAlert.Severity.MEDIUM,
                    "Significant Supplier Risk Increase", 
                    "Supplier risk score has increased significantly", 
                    currentRiskScore);
        }
    }
}