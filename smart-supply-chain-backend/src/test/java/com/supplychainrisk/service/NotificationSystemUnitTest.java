package com.supplychainrisk.service;

import com.supplychainrisk.entity.Alert;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationSystemUnitTest {

    @Test
    void testAlertEnumValues() {
        // Test that alert enum values are accessible
        assertNotNull(Alert.AlertType.RISK_ALERT);
        assertNotNull(Alert.AlertStatus.NEW);
        assertNotNull(Alert.AlertStatus.ACKNOWLEDGED);
        assertNotNull(Alert.AlertStatus.RESOLVED);
        assertNotNull(Alert.ResolutionType.FIXED);
        assertNotNull(Alert.AlertSeverity.HIGH);
    }

    @Test
    void testAlertCreation() {
        // Test that Alert entity can be created with all required fields
        Alert alert = new Alert();
        alert.setTitle("Test Alert");
        alert.setDescription("Test Description");
        alert.setAlertType(Alert.AlertType.RISK_ALERT);
        alert.setSeverity(Alert.AlertSeverity.HIGH);
        alert.setStatus(Alert.AlertStatus.NEW);
        
        assertEquals("Test Alert", alert.getTitle());
        assertEquals("Test Description", alert.getDescription());
        assertEquals(Alert.AlertType.RISK_ALERT, alert.getAlertType());
        assertEquals(Alert.AlertSeverity.HIGH, alert.getSeverity());
        assertEquals(Alert.AlertStatus.NEW, alert.getStatus());
    }

    @Test
    void testAlertStatusTransitions() {
        // Test valid alert status transitions
        Alert alert = new Alert();
        alert.setStatus(Alert.AlertStatus.NEW);
        assertEquals(Alert.AlertStatus.NEW, alert.getStatus());

        alert.setStatus(Alert.AlertStatus.ACKNOWLEDGED);
        assertEquals(Alert.AlertStatus.ACKNOWLEDGED, alert.getStatus());

        alert.setStatus(Alert.AlertStatus.RESOLVED);
        assertEquals(Alert.AlertStatus.RESOLVED, alert.getStatus());
    }

    @Test
    void testAlertResolution() {
        // Test alert resolution functionality
        Alert alert = new Alert();
        alert.setStatus(Alert.AlertStatus.NEW);
        
        // Simulate resolution
        alert.setStatus(Alert.AlertStatus.RESOLVED);
        alert.setResolvedBy("testuser");
        alert.setResolutionNote("Issue resolved successfully");
        alert.setResolutionType(Alert.ResolutionType.FIXED);
        alert.setResolvedAt(LocalDateTime.now());
        
        assertEquals(Alert.AlertStatus.RESOLVED, alert.getStatus());
        assertEquals("testuser", alert.getResolvedBy());
        assertEquals("Issue resolved successfully", alert.getResolutionNote());
        assertEquals(Alert.ResolutionType.FIXED, alert.getResolutionType());
        assertNotNull(alert.getResolvedAt());
    }

    @Test
    void testAlertAcknowledgment() {
        // Test alert acknowledgment functionality
        Alert alert = new Alert();
        alert.setStatus(Alert.AlertStatus.NEW);
        
        // Simulate acknowledgment
        alert.setStatus(Alert.AlertStatus.ACKNOWLEDGED);
        alert.setAcknowledgedBy("testuser");
        alert.setAcknowledgmentNote("Alert acknowledged by test user");
        alert.setAcknowledgedAt(LocalDateTime.now());
        
        assertEquals(Alert.AlertStatus.ACKNOWLEDGED, alert.getStatus());
        assertEquals("testuser", alert.getAcknowledgedBy());
        assertEquals("Alert acknowledged by test user", alert.getAcknowledgmentNote());
        assertNotNull(alert.getAcknowledgedAt());
    }

    @Test
    void testAlertWithAllFields() {
        // Test creating an alert with all possible fields
        Alert alert = new Alert();
        alert.setId(1L);
        alert.setTitle("Comprehensive Test Alert");
        alert.setDescription("This is a comprehensive test alert with all fields");
        alert.setSummary("Test summary");
        alert.setAlertType(Alert.AlertType.RISK_ALERT);
        alert.setSeverity(Alert.AlertSeverity.CRITICAL);
        alert.setCategory(Alert.AlertCategory.SUPPLIER_RISK);
        alert.setStatus(Alert.AlertStatus.NEW);
        alert.setSourceSystem("TEST_SYSTEM");
        alert.setSourceEntityType("SUPPLIER");
        alert.setSourceEntityId("SUP001");
        alert.setRiskScore(0.85);
        alert.setImpactScore(0.90);
        alert.setTriggerCondition("risk_score > 0.8");
        alert.setThresholdValue("0.8");
        alert.setActualValue("0.85");
        alert.setAssignedTo("testuser");
        alert.setAssignedTeam("risk_team");
        alert.setCreatedAt(LocalDateTime.now());
        alert.setDetectedAt(LocalDateTime.now());
        
        // Verify all fields are set correctly
        assertEquals(1L, alert.getId());
        assertEquals("Comprehensive Test Alert", alert.getTitle());
        assertEquals("This is a comprehensive test alert with all fields", alert.getDescription());
        assertEquals("Test summary", alert.getSummary());
        assertEquals(Alert.AlertType.RISK_ALERT, alert.getAlertType());
        assertEquals(Alert.AlertSeverity.CRITICAL, alert.getSeverity());
        assertEquals(Alert.AlertCategory.SUPPLIER_RISK, alert.getCategory());
        assertEquals(Alert.AlertStatus.NEW, alert.getStatus());
        assertEquals("TEST_SYSTEM", alert.getSourceSystem());
        assertEquals("SUPPLIER", alert.getSourceEntityType());
        assertEquals("SUP001", alert.getSourceEntityId());
        assertEquals(0.85, alert.getRiskScore());
        assertEquals(0.90, alert.getImpactScore());
        assertEquals("risk_score > 0.8", alert.getTriggerCondition());
        assertEquals("0.8", alert.getThresholdValue());
        assertEquals("0.85", alert.getActualValue());
        assertEquals("testuser", alert.getAssignedTo());
        assertEquals("risk_team", alert.getAssignedTeam());
        assertNotNull(alert.getCreatedAt());
        assertNotNull(alert.getDetectedAt());
    }

    @Test
    void testValidResolutionTypes() {
        // Test all resolution types are valid
        Alert alert = new Alert();
        
        alert.setResolutionType(Alert.ResolutionType.FIXED);
        assertEquals(Alert.ResolutionType.FIXED, alert.getResolutionType());
        
        alert.setResolutionType(Alert.ResolutionType.WORKAROUND);
        assertEquals(Alert.ResolutionType.WORKAROUND, alert.getResolutionType());
        
        alert.setResolutionType(Alert.ResolutionType.ACKNOWLEDGED);
        assertEquals(Alert.ResolutionType.ACKNOWLEDGED, alert.getResolutionType());
        
        alert.setResolutionType(Alert.ResolutionType.FALSE_POSITIVE);
        assertEquals(Alert.ResolutionType.FALSE_POSITIVE, alert.getResolutionType());
        
        alert.setResolutionType(Alert.ResolutionType.DUPLICATE);
        assertEquals(Alert.ResolutionType.DUPLICATE, alert.getResolutionType());
    }

    @Test
    void testValidAlertTypes() {
        // Test all alert types are valid
        Alert alert = new Alert();
        
        alert.setAlertType(Alert.AlertType.RISK_ALERT);
        assertEquals(Alert.AlertType.RISK_ALERT, alert.getAlertType());
        
        alert.setAlertType(Alert.AlertType.PERFORMANCE_ALERT);
        assertEquals(Alert.AlertType.PERFORMANCE_ALERT, alert.getAlertType());
        
        alert.setAlertType(Alert.AlertType.SYSTEM_ALERT);
        assertEquals(Alert.AlertType.SYSTEM_ALERT, alert.getAlertType());
        
        alert.setAlertType(Alert.AlertType.COMPLIANCE_ALERT);
        assertEquals(Alert.AlertType.COMPLIANCE_ALERT, alert.getAlertType());
        
        alert.setAlertType(Alert.AlertType.SECURITY_ALERT);
        assertEquals(Alert.AlertType.SECURITY_ALERT, alert.getAlertType());
    }
}