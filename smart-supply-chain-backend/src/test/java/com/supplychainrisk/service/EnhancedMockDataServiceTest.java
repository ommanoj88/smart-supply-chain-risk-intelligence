package com.supplychainrisk.service;

import com.supplychainrisk.dto.CrisisScenarioRequest;
import com.supplychainrisk.dto.MarketDataRequest;
import com.supplychainrisk.dto.SupplierScenarioRequest;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Enhanced Mock Data Service DTOs
 */
public class EnhancedMockDataServiceTest {

    @Test
    void testCrisisScenarioRequestCreation() {
        // Test DTO creation and basic validation
        CrisisScenarioRequest request = new CrisisScenarioRequest();
        request.setScenarioType("HURRICANE");
        request.setAffectedRegions(Arrays.asList("Gulf Coast", "East Coast"));
        request.setSeverity("CATEGORY_3");
        request.setDuration(Duration.ofDays(14));
        
        assertNotNull(request);
        assertEquals("HURRICANE", request.getScenarioType());
        assertEquals(2, request.getAffectedRegions().size());
        assertEquals("CATEGORY_3", request.getSeverity());
        assertEquals(14, request.getDuration().toDays());
    }

    @Test
    void testMarketDataRequestCreation() {
        // Test market data request DTO
        MarketDataRequest request = new MarketDataRequest();
        request.setDataType("CURRENCY");
        request.setRegion("North America");
        request.setTimeRange(Duration.ofDays(30));
        request.setVolatilityLevel(0.15);
        
        assertNotNull(request);
        assertEquals("CURRENCY", request.getDataType());
        assertEquals("North America", request.getRegion());
        assertEquals(30, request.getTimeRange().toDays());
        assertEquals(0.15, request.getVolatilityLevel());
    }

    @Test
    void testSupplierScenarioRequestCreation() {
        // Test supplier scenario request DTO
        SupplierScenarioRequest request = new SupplierScenarioRequest();
        request.setSupplierId(1L);
        request.setScenarioType("PERFORMANCE_DEGRADATION");
        request.setSeverity("MEDIUM");
        request.setTimeframe(Duration.ofDays(180));
        
        assertNotNull(request);
        assertEquals(1L, request.getSupplierId());
        assertEquals("PERFORMANCE_DEGRADATION", request.getScenarioType());
        assertEquals("MEDIUM", request.getSeverity());
        assertEquals(180, request.getTimeframe().toDays());
    }
    
    @Test
    void testCrisisScenarioTypes() {
        // Test various crisis scenario types
        String[] scenarioTypes = {"HURRICANE", "TRADE_WAR", "SUPPLIER_BANKRUPTCY", "PANDEMIC"};
        
        for (String type : scenarioTypes) {
            CrisisScenarioRequest request = new CrisisScenarioRequest();
            request.setScenarioType(type);
            assertEquals(type, request.getScenarioType());
        }
    }
    
    @Test
    void testMarketDataTypes() {
        // Test various market data types
        String[] dataTypes = {"CURRENCY", "COMMODITY", "WEATHER", "ECONOMIC"};
        
        for (String type : dataTypes) {
            MarketDataRequest request = new MarketDataRequest();
            request.setDataType(type);
            assertEquals(type, request.getDataType());
        }
    }
    
    @Test
    void testSupplierScenarioTypes() {
        // Test various supplier scenario types
        String[] scenarioTypes = {"PERFORMANCE_DEGRADATION", "CAPACITY_CONSTRAINT", "QUALITY_ISSUE", "COMPLIANCE_ISSUE"};
        
        for (String type : scenarioTypes) {
            SupplierScenarioRequest request = new SupplierScenarioRequest();
            request.setScenarioType(type);
            assertEquals(type, request.getScenarioType());
        }
    }
}