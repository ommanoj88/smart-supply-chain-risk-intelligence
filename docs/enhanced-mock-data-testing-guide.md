# Enhanced Mock Data Testing Guide

## Quick Start Testing

This guide provides step-by-step instructions for testing the enhanced mock data generation capabilities.

## Prerequisites

1. Application running on `http://localhost:8080`
2. Valid JWT token with ADMIN role
3. Database seeded with initial data (150+ suppliers, 2000+ shipments)

## Step 1: Get Authentication Token

```bash
# Login to get JWT token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'

# Save the token from response
export JWT_TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Step 2: Test Enhanced Mock Data Capabilities

### Check Available Scenario Capabilities

```bash
curl -X GET http://localhost:8080/api/admin/mock-data/scenario-capabilities \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json"
```

**Expected Response**:
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
  }
}
```

## Step 3: Test Crisis Scenarios

### Hurricane Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/crisis-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioType": "HURRICANE",
    "affectedRegions": ["Gulf Coast", "East Coast"],
    "severity": "CATEGORY_3",
    "duration": "P14D",
    "parameters": {
      "includeRecoveryPlan": true,
      "calculateEconomicImpact": true
    }
  }'
```

### Trade War Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/crisis-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioType": "TRADE_WAR",
    "affectedRegions": ["Asia", "North America"],
    "severity": "HIGH",
    "duration": "P180D",
    "parameters": {
      "tariffIncrease": 25,
      "affectedIndustries": ["Electronics", "Automotive"]
    }
  }'
```

### Supplier Bankruptcy Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/crisis-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioType": "SUPPLIER_BANKRUPTCY",
    "affectedRegions": ["Global"],
    "severity": "MEDIUM",
    "duration": "P60D",
    "parameters": {
      "failedSupplierCount": 3,
      "cascadingEffects": true
    }
  }'
```

### Pandemic Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/crisis-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioType": "PANDEMIC",
    "affectedRegions": ["Asia", "Europe", "North America"],
    "severity": "HIGH",
    "duration": "P365D",
    "parameters": {
      "lockdownSeverity": "STRICT",
      "workforceReduction": 40
    }
  }'
```

## Step 4: Test Market Data Scenarios

### Currency Fluctuation Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/market-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dataType": "CURRENCY",
    "region": "Europe",
    "timeRange": "P90D",
    "volatilityLevel": 0.25,
    "parameters": {
      "currencyPairs": ["USD-EUR", "GBP-EUR"],
      "includeHedgingScenarios": true
    }
  }'
```

### Commodity Price Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/market-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dataType": "COMMODITY",
    "region": "Global",
    "timeRange": "P180D",
    "volatilityLevel": 0.30,
    "parameters": {
      "commodities": ["OIL", "STEEL", "SEMICONDUCTORS"],
      "includeShortageScenarios": true
    }
  }'
```

### Weather Impact Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/market-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dataType": "WEATHER",
    "region": "North America",
    "timeRange": "P120D",
    "volatilityLevel": 0.20,
    "parameters": {
      "weatherEvents": ["HURRICANE_SEASON", "WINTER_STORMS"],
      "affectedRoutes": ["Gulf_Coast", "Great_Lakes"]
    }
  }'
```

### Economic Indicator Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/market-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dataType": "ECONOMIC",
    "region": "Global",
    "timeRange": "P365D",
    "volatilityLevel": 0.15,
    "parameters": {
      "indicators": ["GDP_GROWTH", "INFLATION", "MARKET_CONFIDENCE"],
      "scenario": "RECESSION"
    }
  }'
```

## Step 5: Test Supplier Performance Scenarios

First, get a list of suppliers to test with:

```bash
curl -X GET http://localhost:8080/api/admin/data-stats \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### Performance Degradation Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/supplier-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": 1,
    "scenarioType": "PERFORMANCE_DEGRADATION",
    "severity": "MEDIUM",
    "timeframe": "P180D",
    "parameters": {
      "degradationRate": 0.02,
      "includeWarningIndicators": true,
      "generateRecoveryProgram": true
    }
  }'
```

### Capacity Constraint Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/supplier-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": 2,
    "scenarioType": "CAPACITY_CONSTRAINT",
    "severity": "HIGH",
    "timeframe": "P90D",
    "parameters": {
      "demandSurge": 3.0,
      "capacityUtilization": 0.95,
      "includeInvestmentOptions": true
    }
  }'
```

### Quality Issue Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/supplier-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": 3,
    "scenarioType": "QUALITY_ISSUE",
    "severity": "HIGH",
    "timeframe": "P60D",
    "parameters": {
      "defectType": "MATERIAL_COMPOSITION",
      "affectedBatches": 5,
      "includeRecallScenario": true
    }
  }'
```

### Compliance Issue Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/supplier-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "supplierId": 4,
    "scenarioType": "COMPLIANCE_ISSUE",
    "severity": "MEDIUM",
    "timeframe": "P120D",
    "parameters": {
      "certificationType": "ISO_9001",
      "expiryDate": "2024-06-30",
      "auditFailure": true
    }
  }'
```

## Step 6: Test Complex Shipment Scenarios

### Multi-Modal Transport Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/complex-shipment-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioType": "MULTI_MODAL_TRANSPORT",
    "parameters": {
      "transportModes": ["OCEAN", "RAIL", "TRUCK"],
      "optimizeFor": "COST",
      "route": "ASIA_TO_US",
      "intermodalDelays": true
    }
  }'
```

### Customs Delays Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/complex-shipment-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioType": "CUSTOMS_DELAYS",
    "parameters": {
      "borderCrossings": ["US_MEXICO", "US_CANADA"],
      "delayReasons": ["DOCUMENTATION", "INSPECTION", "TARIFF_CHANGES"],
      "severityLevel": "HIGH"
    }
  }'
```

### Port Congestion Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/complex-shipment-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioType": "PORT_CONGESTION",
    "parameters": {
      "affectedPorts": ["LOS_ANGELES", "LONG_BEACH", "ROTTERDAM"],
      "congestionLevel": "SEVERE",
      "containerShortage": true,
      "vesselsWaitingTime": "5-8 days"
    }
  }'
```

### Route Optimization Scenario

```bash
curl -X POST http://localhost:8080/api/admin/mock-data/complex-shipment-scenario \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "scenarioType": "ROUTE_OPTIMIZATION",
    "parameters": {
      "optimizationGoal": "COST_MINIMIZATION",
      "constraints": ["AVOID_HIGH_RISK_AREAS", "MINIMIZE_EMISSIONS"],
      "alternativeRoutes": true,
      "emergencyRerouting": false
    }
  }'
```

## Step 7: Verify Data Generation

### Check System Statistics After Scenario Generation

```bash
curl -X GET http://localhost:8080/api/admin/data-stats \
  -H "Authorization: Bearer $JWT_TOKEN"
```

### Validate Crisis Scenario Impact

```bash
# Check for high-risk shipments
curl -X GET "http://localhost:8080/api/shipments?status=EXCEPTION&page=0&size=10" \
  -H "Authorization: Bearer $JWT_TOKEN"

# Check affected suppliers
curl -X GET "http://localhost:8080/api/suppliers?riskScore=HIGH&page=0&size=10" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

## Step 8: Test Scenario Integration

### Verify Dashboard Impact

1. **Open Dashboard**: Navigate to `http://localhost:3000/dashboard`
2. **Check Risk Alerts**: Verify new risk alerts are generated
3. **Supplier Performance**: Check degraded supplier performance metrics
4. **Shipment Tracking**: Verify delayed shipments appear in tracking
5. **Market Data**: Check currency and commodity data updates

### Real-time Notifications Test

```bash
# Connect to WebSocket endpoint for real-time updates
# This would typically be done through frontend JavaScript
# ws://localhost:8080/ws/shipment-tracking
```

## Expected Outcomes

After running these tests, you should observe:

1. **Crisis Scenarios**:
   - Increased risk scores for affected suppliers
   - New shipment delays and exceptions
   - Generated recovery plans and economic impact calculations

2. **Market Data Scenarios**:
   - Currency rate fluctuations affecting costs
   - Commodity price changes impacting supplier costs
   - Weather-related shipping delays

3. **Supplier Scenarios**:
   - Degraded performance metrics for targeted suppliers
   - Capacity constraint alerts
   - Quality issue tracking and compliance alerts

4. **Shipment Scenarios**:
   - Multi-modal routing with realistic delays
   - Customs processing delays
   - Port congestion affecting delivery times
   - Optimized routing recommendations

## Troubleshooting

### Common Issues

1. **Authentication Errors**:
   ```bash
   # Verify token is valid
   curl -X GET http://localhost:8080/api/auth/profile \
     -H "Authorization: Bearer $JWT_TOKEN"
   ```

2. **Permission Denied**:
   ```bash
   # Verify admin role
   # Only users with ADMIN role can generate mock data
   ```

3. **Supplier Not Found**:
   ```bash
   # Get list of valid supplier IDs
   curl -X GET http://localhost:8080/api/suppliers?page=0&size=10 \
     -H "Authorization: Bearer $JWT_TOKEN"
   ```

4. **Database Connection Issues**:
   ```bash
   # Check application health
   curl -X GET http://localhost:8080/actuator/health
   ```

### Performance Testing

For load testing, use multiple concurrent requests:

```bash
# Test with 10 concurrent hurricane scenarios
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/admin/mock-data/crisis-scenario \
    -H "Authorization: Bearer $JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
      "scenarioType": "HURRICANE",
      "affectedRegions": ["Gulf Coast"],
      "severity": "CATEGORY_2",
      "duration": "P7D"
    }' &
done
wait
```

## Automated Testing Script

Create a comprehensive test script:

```bash
#!/bin/bash
# enhanced-mock-data-test.sh

# Set base URL and get token
BASE_URL="http://localhost:8080"
TOKEN=$(get_jwt_token)  # Implement this function

# Test all scenario types
scenarios=(
  "HURRICANE:CATEGORY_3:Gulf_Coast"
  "TRADE_WAR:HIGH:Asia,North_America"
  "SUPPLIER_BANKRUPTCY:MEDIUM:Global"
  "PANDEMIC:HIGH:Global"
)

for scenario in "${scenarios[@]}"; do
  IFS=':' read -r type severity regions <<< "$scenario"
  echo "Testing $type scenario..."
  
  response=$(curl -s -X POST "$BASE_URL/api/admin/mock-data/crisis-scenario" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"scenarioType\": \"$type\",
      \"severity\": \"$severity\",
      \"affectedRegions\": [\"$(echo $regions | tr ',' '","')\"],
      \"duration\": \"P14D\"
    }")
  
  if echo "$response" | grep -q "success.*true"; then
    echo "✅ $type scenario generated successfully"
  else
    echo "❌ $type scenario failed"
    echo "$response"
  fi
done
```

This comprehensive testing guide ensures that all enhanced mock data generation capabilities are thoroughly validated and working as expected.