package com.supplychainrisk.service;

import com.supplychainrisk.dto.*;
import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnhancedSupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierLocationRepository supplierLocationRepository;

    @Mock
    private RiskFactorRepository riskFactorRepository;

    @Mock
    private SupplierCertificationRepository supplierCertificationRepository;

    @Mock
    private RiskAssessmentService riskAssessmentService;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private SupplierIntelligenceService supplierIntelligenceService;

    @InjectMocks
    private EnhancedSupplierService enhancedSupplierService;

    private Supplier testSupplier;

    @BeforeEach
    public void setUp() {
        testSupplier = new Supplier();
        testSupplier.setId(1L);
        testSupplier.setName("Test Supplier");
        testSupplier.setSupplierCode("TS001");
        testSupplier.setOverallRiskScore(45);
        testSupplier.setRiskScore(45.0);
        testSupplier.setRiskLevel(Supplier.RiskLevel.MEDIUM);
        testSupplier.setOnTimeDeliveryRate(BigDecimal.valueOf(95.5));
        testSupplier.setQualityRating(BigDecimal.valueOf(8.5));
        testSupplier.setCostCompetitivenessScore(75);
        testSupplier.setResponsivenessScore(80);
        testSupplier.setStatus(Supplier.SupplierStatus.ACTIVE);
    }

    @Test
    public void testGetComprehensiveSupplierProfile() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(riskFactorRepository.findBySupplierIdAndIsActive(1L, true)).thenReturn(new ArrayList<>());
        when(riskAssessmentService.getRiskRecommendations(any(Supplier.class))).thenReturn(Arrays.asList("Monitor regularly"));
        when(supplierIntelligenceService.getMarketIntelligence(any(Supplier.class))).thenReturn(new MarketIntelligence());

        // When
        SupplierProfile result = enhancedSupplierService.getComprehensiveSupplierProfile(1L);

        // Then
        assertNotNull(result);
        assertNotNull(result.getSupplier());
        assertEquals("Test Supplier", result.getSupplier().getName());
        assertNotNull(result.getRiskAssessment());
        assertNotNull(result.getPerformanceAnalytics());
        assertNotNull(result.getMarketIntelligence());
        assertNotNull(result.getSupplierNetwork());
        
        verify(supplierRepository, atLeast(1)).findById(1L);
        verify(riskFactorRepository).findBySupplierIdAndIsActive(1L, true);
        verify(supplierIntelligenceService).getMarketIntelligence(testSupplier);
    }

    @Test
    public void testGetSupplierRiskDistribution() {
        // Given
        List<Supplier> suppliers = Arrays.asList(testSupplier);
        when(supplierRepository.findAll()).thenReturn(suppliers);

        // When
        Map<String, Object> result = enhancedSupplierService.getSupplierRiskDistribution();

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("riskLevelDistribution"));
        assertTrue(result.containsKey("totalSuppliers"));
        assertTrue(result.containsKey("averageRiskScore"));
        assertEquals(1, result.get("totalSuppliers"));
        
        verify(supplierRepository).findAll();
    }

    @Test
    public void testGetSupplierPerformanceTrends() {
        // Given
        List<Supplier> suppliers = Arrays.asList(testSupplier);
        when(supplierRepository.findAll()).thenReturn(suppliers);

        // When
        Map<String, Object> result = enhancedSupplierService.getSupplierPerformanceTrends(
            SupplierPerformanceAnalytics.TimeRange.LAST_6_MONTHS);

        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("averageOnTimeDelivery"));
        assertTrue(result.containsKey("averageQualityRating"));
        assertTrue(result.containsKey("timeRange"));
        assertTrue(result.containsKey("generatedAt"));
        
        verify(supplierRepository).findAll();
    }

    @Test
    public void testFindAlternativeSuppliers() {
        // Given
        SupplierRequirements requirements = new SupplierRequirements();
        requirements.setMaxResults(5);
        requirements.setIndustry("Technology");
        
        List<Supplier> suppliers = Arrays.asList(testSupplier);
        when(supplierRepository.findAll()).thenReturn(suppliers);

        // When
        List<SupplierRecommendation> result = enhancedSupplierService.findAlternativeSuppliers(requirements);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        
        SupplierRecommendation recommendation = result.get(0);
        assertNotNull(recommendation.getSupplier());
        assertEquals("Test Supplier", recommendation.getSupplier().getName());
        assertTrue(recommendation.getScore() > 0);
        
        verify(supplierRepository).findAll();
    }

    @Test
    public void testGetSupplierPerformanceDashboard() {
        // Given
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

        // When
        SupplierPerformanceAnalytics result = enhancedSupplierService.getSupplierPerformanceDashboard(
            1L, SupplierPerformanceAnalytics.TimeRange.LAST_12_MONTHS);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getSupplierId());
        assertEquals("Test Supplier", result.getSupplierName());
        assertEquals(SupplierPerformanceAnalytics.TimeRange.LAST_12_MONTHS, result.getTimeRange());
        assertNotNull(result.getCurrentMetrics());
        
        // Verify performance metrics
        SupplierPerformanceAnalytics.PerformanceMetrics metrics = result.getCurrentMetrics();
        assertEquals(95.5, metrics.getOnTimeDeliveryRate());
        assertEquals(8.5, metrics.getQualityScore());
        assertEquals(75.0, metrics.getCostCompetitiveness());
        assertEquals(80.0, metrics.getResponsiveness());
        
        verify(supplierRepository).findById(1L);
    }

    @Test
    public void testGetComprehensiveSupplierProfileNotFound() {
        // Given
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            enhancedSupplierService.getComprehensiveSupplierProfile(999L);
        });
        
        assertTrue(exception.getMessage().contains("Failed to retrieve supplier profile"));
        verify(supplierRepository).findById(999L);
    }
}