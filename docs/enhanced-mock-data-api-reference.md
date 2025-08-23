# Enhanced Mock Data API Reference

## Overview

This document provides a comprehensive reference for all Enhanced Mock Data Generation APIs, building on the existing infrastructure of 150+ suppliers, 2000+ shipments, and 15,000+ events.

## Base URL
```
http://localhost:8080/api/admin/mock-data
```

## Authentication
All endpoints require:
- **Bearer Token**: JWT token with ADMIN role
- **Content-Type**: application/json

## API Endpoints

### 1. Crisis Scenario Generation

#### Generate Crisis Scenario
```http
POST /crisis-scenario
```

**Supported Scenario Types**:
- `HURRICANE` - Hurricane impact simulation with category levels 1-5
- `TRADE_WAR` - Tariff and trade restriction scenarios
- `SUPPLIER_BANKRUPTCY` - Supplier failure with cascading effects
- `PANDEMIC` - Global pandemic disruption scenarios

**Request Body**:
```json
{
  "scenarioType": "HURRICANE",
  "affectedRegions": ["Gulf Coast", "East Coast"],
  "severity": "CATEGORY_3",
  "duration": "P14D",
  "parameters": {
    "includeRecoveryPlan": true,
    "calculateEconomicImpact": true
  }
}
```

**Response**:
```json
{
  "success": true,
  "scenario": {
    "scenarioId": "uuid-1234",
    "scenarioType": "HURRICANE",
    "severity": "CATEGORY_3",
    "affectedSuppliers": [...],
    "shippingDisruptions": [...],
    "recoveryPlan": [...],
    "economicImpact": {
      "totalEconomicImpact": 2500000,
      "directCosts": 1500000,
      "shippingCostIncrease": 300000,
      "expectedInsuranceClaims": 1050000
    }
  }
}
```

### 2. Market Data Simulation

#### Generate Market Data Scenario
```http
POST /market-scenario
```

**Supported Data Types**:
- `CURRENCY` - Currency fluctuation scenarios
- `COMMODITY` - Commodity price volatility
- `WEATHER` - Weather impact on supply chains
- `ECONOMIC` - Economic indicator scenarios

**Request Body**:
```json
{
  "dataType": "CURRENCY",
  "region": "Europe",
  "timeRange": "P90D",
  "volatilityLevel": 0.25,
  "parameters": {
    "currencyPairs": ["USD-EUR", "GBP-EUR"],
    "includeHedgingScenarios": true
  }
}
```

**Response**:
```json
{
  "success": true,
  "marketData": {
    "dataType": "CURRENCY",
    "region": "Europe",
    "rateChanges": {
      "USD-EUR": [
        {
          "date": "2024-01-01T00:00:00",
          "rate": 0.8456,
          "change": "+1.2%"
        }
      ]
    },
    "costImpacts": {...},
    "hedgingScenarios": [...]
  }
}
```

### 3. Supplier Performance Scenarios

#### Generate Supplier Scenario
```http
POST /supplier-scenario
```

**Supported Scenario Types**:
- `PERFORMANCE_DEGRADATION` - Gradual quality decline
- `CAPACITY_CONSTRAINT` - Demand surge scenarios
- `QUALITY_ISSUE` - Product defects and recalls
- `COMPLIANCE_ISSUE` - Certification and regulatory issues

**Request Body**:
```json
{
  "supplierId": 123,
  "scenarioType": "PERFORMANCE_DEGRADATION",
  "severity": "MEDIUM",
  "timeframe": "P180D",
  "parameters": {
    "degradationRate": 0.02,
    "includeWarningIndicators": true,
    "generateRecoveryProgram": true
  }
}
```

**Response**:
```json
{
  "success": true,
  "supplierScenario": {
    "scenarioType": "PERFORMANCE_DEGRADATION",
    "supplierId": 123,
    "supplierName": "Acme Manufacturing",
    "qualityDegradationTrend": [...],
    "warningIndicators": [...],
    "rootCauses": {...},
    "recoveryProgram": [...]
  }
}
```

### 4. Complex Shipment Scenarios

#### Generate Complex Shipment Scenario
```http
POST /complex-shipment-scenario
```

**Supported Scenario Types**:
- `MULTI_MODAL_TRANSPORT` - Ocean + rail + truck combinations
- `CUSTOMS_DELAYS` - Border crossing delays
- `PORT_CONGESTION` - Major port congestion scenarios
- `ROUTE_OPTIMIZATION` - Emergency rerouting scenarios

**Request Body**:
```json
{
  "scenarioType": "MULTI_MODAL_TRANSPORT",
  "parameters": {
    "transportModes": ["OCEAN", "RAIL", "TRUCK"],
    "optimizeFor": "COST",
    "route": "ASIA_TO_US",
    "intermodalDelays": true
  }
}
```

**Response**:
```json
{
  "success": true,
  "shipmentScenario": {
    "scenarioType": "MULTI_MODAL_TRANSPORT",
    "transportModes": ["OCEAN", "RAIL", "TRUCK"],
    "intermodalDelays": "2-4 hours at each transfer point",
    "costOptimization": "15% savings vs air freight",
    "timeTradeoff": "+5 days vs direct air transport",
    "transferPoints": [...]
  }
}
```

### 5. Scenario Capabilities

#### Get Available Scenario Types
```http
GET /scenario-capabilities
```

**Response**:
```json
{
  "crisisScenarios": {
    "available": ["HURRICANE", "TRADE_WAR", "SUPPLIER_BANKRUPTCY", "PANDEMIC"],
    "description": "Comprehensive crisis scenarios with cascading effects"
  },
  "marketDataScenarios": {
    "available": ["CURRENCY", "COMMODITY", "WEATHER", "ECONOMIC"],
    "description": "Market data simulation affecting costs and routing"
  },
  "supplierScenarios": {
    "available": ["PERFORMANCE_DEGRADATION", "CAPACITY_CONSTRAINT", "QUALITY_ISSUE", "COMPLIANCE_ISSUE"],
    "description": "Advanced supplier performance scenarios"
  },
  "shipmentScenarios": {
    "available": ["MULTI_MODAL_TRANSPORT", "CUSTOMS_DELAYS", "PORT_CONGESTION", "ROUTE_OPTIMIZATION"],
    "description": "Complex shipment scenarios with realistic delays"
  },
  "features": [
    "Realistic crisis scenarios with 18-month historical data",
    "Market data integration with currency and commodity impacts",
    "Advanced supplier degradation patterns",
    "Multi-modal transport simulation",
    "Cascading effect modeling",
    "Recovery timeline simulation"
  ]
}
```

## Legacy Compatibility Endpoints

### Traditional Testing Scenarios (Enhanced)

#### Get System Statistics
```http
GET /api/admin/data-stats
```

**Enhanced Response** (includes new scenario data):
```json
{
  "totalSuppliers": 156,
  "totalShipments": 2045,
  "totalTrackingEvents": 15678,
  "onTimeDeliveryRate": 94.2,
  "averageRiskScore": 23.4,
  "highRiskSuppliers": 12,
  "scenarioStats": {
    "activeScenarios": 3,
    "scenariosGenerated": 25,
    "lastScenarioType": "HURRICANE",
    "lastScenarioDate": "2024-01-15T10:30:00Z"
  }
}
```

#### Generate Testing Scenario (Legacy)
```http
POST /api/admin/testing/generate-scenario
```

**Enhanced Support** (now includes new scenario types):
```json
{
  "scenarioType": "HURRICANE_DISRUPTION",
  "intensity": 3,
  "affectedRegions": ["Asia", "Europe"],
  "realTimeUpdates": true
}
```

## HTTP Status Codes

- `200 OK` - Successful scenario generation
- `400 Bad Request` - Invalid request parameters
- `401 Unauthorized` - Missing or invalid JWT token
- `403 Forbidden` - Insufficient permissions (requires ADMIN role)
- `404 Not Found` - Supplier not found (for supplier scenarios)
- `500 Internal Server Error` - Server error during scenario generation

## Error Response Format

```json
{
  "success": false,
  "error": "Crisis scenario generation failed: Invalid scenario type",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/admin/mock-data/crisis-scenario"
}
```

## Rate Limiting

- **Rate Limit**: 10 requests per minute per user
- **Burst Limit**: 3 concurrent requests
- **Headers**: 
  - `X-RateLimit-Remaining`: Requests remaining in current window
  - `X-RateLimit-Reset`: Time when rate limit resets

## Data Integration Points

### Generated Data Affects:

1. **Supplier Risk Scores** - Updated based on scenario impacts
2. **Shipment Status** - New delays and exceptions created
3. **Risk Alerts** - Automatic alert generation for high-impact scenarios
4. **Performance Metrics** - KPI updates reflecting scenario effects
5. **Cost Calculations** - Currency and commodity impacts on costs
6. **Route Planning** - Alternative routing recommendations

### Real-time Updates:

- **WebSocket Notifications** - Real-time scenario updates
- **Dashboard Refresh** - Automatic dashboard data refresh
- **Alert System** - Immediate risk alert generation
- **Audit Logging** - Complete scenario generation audit trail

## Example Workflow

1. **Authentication**: Obtain JWT token with ADMIN role
2. **Check Capabilities**: GET `/scenario-capabilities` to see available scenarios
3. **Generate Hurricane**: POST `/crisis-scenario` with HURRICANE type
4. **Monitor Impact**: GET `/api/admin/data-stats` to see updated statistics
5. **Generate Market Data**: POST `/market-scenario` with CURRENCY type
6. **Test Supplier Impact**: POST `/supplier-scenario` with specific supplier
7. **Verify Integration**: Check dashboard for updated visualizations

## Advanced Features

### Scenario Chaining
Generate multiple related scenarios:
```bash
# 1. Generate hurricane
POST /crisis-scenario (HURRICANE)
# 2. Generate currency impact
POST /market-scenario (CURRENCY with high volatility)
# 3. Generate supplier degradation
POST /supplier-scenario (PERFORMANCE_DEGRADATION)
```

### Batch Operations
Generate multiple scenarios simultaneously:
```bash
# Multiple parallel requests
curl -X POST /crisis-scenario & \
curl -X POST /market-scenario & \
curl -X POST /supplier-scenario &
```

### Historical Analysis
Generate scenarios with historical context:
```json
{
  "scenarioType": "PANDEMIC",
  "historicalBaseline": "2020-COVID-19",
  "applyLessonsLearned": true,
  "timeRange": "P365D"
}
```

This API reference provides complete coverage of the enhanced mock data generation capabilities, ensuring comprehensive testing of all supply chain risk scenarios.