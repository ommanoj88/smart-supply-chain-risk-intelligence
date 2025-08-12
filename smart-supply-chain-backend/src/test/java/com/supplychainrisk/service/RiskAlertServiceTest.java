package com.supplychainrisk.service;

import com.supplychainrisk.entity.RiskAlert;
import com.supplychainrisk.entity.User;
import com.supplychainrisk.repository.RiskAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RiskAlertServiceTest {

    @Mock
    private RiskAlertRepository riskAlertRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RiskAlertService riskAlertService;

    private RiskAlert testAlert;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testAlert = new RiskAlert("SUPPLIER_RISK", RiskAlert.Severity.HIGH, "Test Alert", "Test Description");
        testAlert.setId(1L);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
    }

    @Test
    public void testCreateRiskAlert() {
        // Given
        when(riskAlertRepository.save(any(RiskAlert.class))).thenReturn(testAlert);

        // When
        RiskAlert result = riskAlertService.createRiskAlert(
                "SUPPLIER_RISK", 
                RiskAlert.Severity.HIGH, 
                "Test Alert", 
                "Test Description", 
                "SUPPLIER", 
                1L
        );

        // Then
        assertNotNull(result);
        assertEquals("Test Alert", result.getTitle());
        assertEquals(RiskAlert.Severity.HIGH, result.getSeverity());
        verify(riskAlertRepository).save(any(RiskAlert.class));
        verify(notificationService).sendRiskAlertNotification(any(RiskAlert.class));
    }

    @Test
    public void testGetRiskAlert() {
        // Given
        when(riskAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));

        // When
        Optional<RiskAlert> result = riskAlertService.getRiskAlert(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testAlert.getTitle(), result.get().getTitle());
        verify(riskAlertRepository).findById(1L);
    }

    @Test
    public void testAcknowledgeAlert() {
        // Given
        when(riskAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));
        when(riskAlertRepository.save(any(RiskAlert.class))).thenReturn(testAlert);

        // When
        RiskAlert result = riskAlertService.acknowledgeAlert(1L, testUser);

        // Then
        assertNotNull(result);
        assertEquals(RiskAlert.Status.ACKNOWLEDGED, testAlert.getStatus());
        assertEquals(testUser, testAlert.getAcknowledgedBy());
        assertNotNull(testAlert.getAcknowledgedAt());
        verify(riskAlertRepository).findById(1L);
        verify(riskAlertRepository).save(testAlert);
    }

    @Test
    public void testResolveAlert() {
        // Given
        when(riskAlertRepository.findById(1L)).thenReturn(Optional.of(testAlert));
        when(riskAlertRepository.save(any(RiskAlert.class))).thenReturn(testAlert);
        String resolutionNotes = "Test resolution notes";

        // When
        RiskAlert result = riskAlertService.resolveAlert(1L, testUser, resolutionNotes);

        // Then
        assertNotNull(result);
        assertEquals(RiskAlert.Status.RESOLVED, testAlert.getStatus());
        assertEquals(testUser, testAlert.getResolvedBy());
        assertEquals(resolutionNotes, testAlert.getResolutionNotes());
        assertNotNull(testAlert.getResolvedAt());
        verify(riskAlertRepository).findById(1L);
        verify(riskAlertRepository).save(testAlert);
    }

    @Test
    public void testAcknowledgeAlertNotFound() {
        // Given
        when(riskAlertRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            riskAlertService.acknowledgeAlert(1L, testUser);
        });
        assertEquals("Risk alert not found with id: 1", exception.getMessage());
        verify(riskAlertRepository).findById(1L);
        verify(riskAlertRepository, never()).save(any(RiskAlert.class));
    }

    @Test
    public void testEvaluateSupplierRisk() {
        // Given
        Long supplierId = 1L;
        when(riskAlertRepository.save(any(RiskAlert.class))).thenReturn(testAlert);

        // When - risk score exceeds threshold
        riskAlertService.evaluateSupplierRisk(supplierId, 80, 50);

        // Then - expect save to be called twice (once in createRiskAlert, once in createSupplierRiskAlert)
        verify(riskAlertRepository, times(2)).save(any(RiskAlert.class));
        verify(notificationService).sendRiskAlertNotification(any(RiskAlert.class));
    }
}