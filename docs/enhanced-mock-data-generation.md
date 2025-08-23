# Enhanced Mock Data Generation - Comprehensive Test Data for All Scenarios

## Overview

The Enhanced Mock Data Generation system provides comprehensive test data capabilities that support realistic testing of all system features and scenarios. Building on the existing foundation of 150+ suppliers, 2000+ shipments, and 15,000+ events, this enhancement adds sophisticated scenario generation for crisis management, market data simulation, and advanced supplier performance testing.

## Core Capabilities

### 1. Crisis Scenario Data Generation

#### Hurricane Impact Simulation
- **Affected Suppliers**: Realistic supplier impacts in hurricane-prone regions
- **Shipping Disruptions**: Delayed/rerouted shipments with realistic recovery patterns
- **Recovery Timeline**: 2-6 week recovery simulation based on category level
- **Economic Impact**: Cost calculations including insurance claims and alternative sourcing

**API Endpoint**: `POST /api/admin/mock-data/crisis-scenario`

**Example Request**:
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

**Sample Response**:
```json
{
  "success": true,
  "scenario": {
    "scenarioId": "uuid-1234-5678",
    "scenarioType": "HURRICANE",
    "severity": "CATEGORY_3",
    "affectedSuppliers": [
      {
        "supplierId": 123,
        "supplierName": "Gulf Coast Manufacturing",
        "operationalCapacityReduction": "60%",
        "expectedDowntimeDays": 8,
        "infrastructureDamage": "MODERATE",
        "recoveryProbability": "85%"
      }
    ],
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

#### Trade War Effects
- **Tariff Impact**: Realistic tariff changes affecting cost and routing
- **Border Delays**: Enhanced customs processing delays
- **Alternative Routing**: Supplier shifts to different countries
- **Supply Chain Reshuffling**: Production relocation scenarios

#### Supplier Bankruptcy Scenarios
- **Cascading Effects**: Dependent supplier impact analysis
- **Alternative Sourcing**: Emergency qualification requirements
- **Financial Impact**: Cost premiums and expedited shipping
- **Recovery Actions**: Realistic supplier replacement timelines

#### Pandemic Disruptions
- **Factory Shutdowns**: Variable duration shutdown scenarios
- **Labor Shortages**: Workforce reduction impact modeling
- **Demand Spikes**: Sudden demand increase/decrease patterns
- **Supply Chain Fragility**: Single point of failure identification

### 2. Market Data Simulation

#### Currency Fluctuations
- **Real-time Currency Data**: Realistic exchange rate variations
- **Cost Impact Calculation**: Automatic cost recalculation
- **Hedging Scenarios**: Strategy effectiveness simulation
- **Multi-currency Shipments**: Complex exposure modeling

**API Endpoint**: `POST /api/admin/mock-data/market-scenario`

**Example Request**:
```json
{
  "dataType": "CURRENCY",
  "region": "North America",
  "timeRange": "P30D",
  "volatilityLevel": 0.15,
  "parameters": {
    "includeHedgingScenarios": true,
    "calculateCostImpacts": true
  }
}
```

#### Commodity Price Volatility
- **Oil Price Impact**: Fuel cost variations affecting shipping
- **Raw Material Costs**: Steel, aluminum, semiconductor price fluctuations
- **Energy Costs**: Regional energy price differences
- **Commodity Shortage**: Scarcity scenarios and price spikes

#### Weather Data Integration
- **Seasonal Patterns**: Realistic weather affecting shipping routes
- **Extreme Weather Events**: Storms, typhoons, monsoons
- **Climate Impact**: Long-term climate change effects
- **Weather-Dependent Industries**: Agriculture, construction, energy

#### Economic Indicators
- **GDP Impact**: Economic growth/recession effects
- **Inflation Simulation**: Cost inflation scenarios
- **Market Confidence**: Investor confidence effects
- **Regional Variations**: Different economic conditions by region

### 3. Advanced Supplier Scenarios

#### Performance Degradation Simulation
- **Gradual Quality Decline**: Slowly degrading performance patterns
- **Warning Indicators**: Early warning sign generation
- **Root Cause Analysis**: Realistic causes for degradation
- **Recovery Programs**: Improvement initiative timelines

**API Endpoint**: `POST /api/admin/mock-data/supplier-scenario`

**Example Request**:
```json
{
  "supplierId": 123,
  "scenarioType": "PERFORMANCE_DEGRADATION",
  "severity": "MEDIUM",
  "timeframe": "P180D",
  "parameters": {
    "includeWarningIndicators": true,
    "generateRecoveryProgram": true
  }
}
```

#### Capacity Constraints
- **Demand Surge Scenarios**: Capacity limit hitting during peak demand
- **Capital Investment**: Required capacity expansion scenarios
- **Lead Time Extension**: Realistic lead time increases
- **Alternative Capacity**: Emergency capacity from alternative suppliers

#### Quality Issues
- **Product Defects**: Realistic quality issue scenarios
- **Recall Scenarios**: Product recall situations and responses
- **Certification Issues**: Quality certification lapses
- **Customer Complaints**: Realistic complaint scenarios

#### Compliance and Certification
- **Certification Expiry**: ISO, FDA, CE certification expiration
- **Regulatory Changes**: New regulation impact scenarios
- **Audit Failures**: Failed compliance audits and remediation
- **Documentation Issues**: Missing or incorrect compliance documentation

### 4. Complex Shipment Scenarios

#### Multi-modal Transport
- **Ocean + Rail + Truck**: Realistic transport combinations
- **Intermodal Delays**: Transfer point delays
- **Mode Optimization**: Cost vs speed comparisons
- **Time/Cost Trade-offs**: Different transport mode analysis

**API Endpoint**: `POST /api/admin/mock-data/complex-shipment-scenario`

**Example Request**:
```json
{
  "scenarioType": "MULTI_MODAL_TRANSPORT",
  "parameters": {
    "transportModes": ["OCEAN", "RAIL", "TRUCK"],
    "optimizeFor": "COST"
  }
}
```

#### Customs and Border Issues
- **Customs Delays**: Realistic clearance delay scenarios
- **Documentation Issues**: Missing or incorrect documentation
- **Duty and Tax Changes**: Sudden duty changes affecting costs
- **Border Congestion**: Port and crossing congestion scenarios

#### Port Congestion Simulation
- **Major Port Delays**: Congestion at major ports (LA, Long Beach, Rotterdam)
- **Alternative Ports**: Alternative port usage scenarios
- **Vessel Waiting Time**: Realistic waiting times and costs
- **Container Shortages**: Availability issues affecting shipping

#### Route Optimization Scenarios
- **Emergency Rerouting**: Immediate route change requirements
- **Cost Optimization**: Route changes to minimize costs
- **Time Optimization**: Route changes to minimize delivery time
- **Risk Mitigation**: Route changes to avoid high-risk areas

## API Endpoints Summary

### Core Enhanced Mock Data APIs

1. **Crisis Scenario Generation**
   - `POST /api/admin/mock-data/crisis-scenario`
   - Supports: HURRICANE, TRADE_WAR, SUPPLIER_BANKRUPTCY, PANDEMIC

2. **Market Data Simulation**
   - `POST /api/admin/mock-data/market-scenario`
   - Supports: CURRENCY, COMMODITY, WEATHER, ECONOMIC

3. **Supplier Performance Scenarios**
   - `POST /api/admin/mock-data/supplier-scenario`
   - Supports: PERFORMANCE_DEGRADATION, CAPACITY_CONSTRAINT, QUALITY_ISSUE, COMPLIANCE_ISSUE

4. **Complex Shipment Scenarios**
   - `POST /api/admin/mock-data/complex-shipment-scenario`
   - Supports: MULTI_MODAL_TRANSPORT, CUSTOMS_DELAYS, PORT_CONGESTION, ROUTE_OPTIMIZATION

5. **Scenario Capabilities**
   - `GET /api/admin/mock-data/scenario-capabilities`
   - Returns available scenario types and descriptions

### Integration with Existing APIs

The enhanced mock data integrates seamlessly with existing endpoints:
- `GET /api/admin/data-stats` - Enhanced with new scenario statistics
- `POST /api/admin/testing/generate-scenario` - Legacy compatibility maintained
- `GET /api/admin/testing/scenarios` - Includes new scenario types

## Data Quality and Realism

### Realistic Data Patterns
- **18-Month Lookback**: Complete historical data for trend analysis
- **Seasonal Patterns**: Holiday shipping spikes, summer slowdowns, harvest cycles
- **Economic Cycles**: Economic expansion/contraction effects
- **Crisis Recovery**: Realistic recovery patterns after various disruptions

### Performance Correlation
- **Supplier Interdependencies**: Realistic relationships between suppliers
- **Geographic Clustering**: Suppliers in same region sharing similar risks
- **Industry Patterns**: Industry-specific performance characteristics
- **Size-Based Patterns**: Different patterns for large vs small suppliers

### Market Integration
- **Global Market Correlation**: How global events affect different regions
- **Currency Impact**: Realistic exchange rate effects on costs
- **Commodity Dependencies**: How commodity price changes affect industries
- **Economic Sensitivity**: Different supplier sensitivity to economic changes

## Usage Examples

### Testing Hurricane Preparedness
```bash
curl -X POST http://localhost:8080/api/admin/mock-data/crisis-scenario \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "scenarioType": "HURRICANE",
    "affectedRegions": ["Gulf Coast"],
    "severity": "CATEGORY_4",
    "duration": "P21D"
  }'
```

### Simulating Currency Volatility
```bash
curl -X POST http://localhost:8080/api/admin/mock-data/market-scenario \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "dataType": "CURRENCY",
    "region": "Europe",
    "timeRange": "P90D",
    "volatilityLevel": 0.25
  }'
```

### Testing Supplier Performance Degradation
```bash
curl -X POST http://localhost:8080/api/admin/mock-data/supplier-scenario \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -d '{
    "supplierId": 123,
    "scenarioType": "PERFORMANCE_DEGRADATION",
    "severity": "HIGH",
    "timeframe": "P180D"
  }'
```

## Security and Access Control

All enhanced mock data endpoints require:
- **Authentication**: Valid JWT token
- **Authorization**: ADMIN role required for data generation
- **Rate Limiting**: Built-in rate limiting to prevent abuse
- **Audit Logging**: All scenario generation activities are logged

## Performance Considerations

- **Batch Processing**: Large data generation uses batch processing
- **Async Operations**: Long-running scenarios run asynchronously
- **Memory Management**: Efficient memory usage for large datasets
- **Database Optimization**: Optimized queries for scenario data retrieval

## Future Enhancements

- **AI-Powered Scenarios**: Machine learning for more realistic scenarios
- **Real-time Data Integration**: Integration with live market data feeds
- **Custom Scenario Builder**: UI for building custom scenarios
- **Scenario Scheduling**: Automated scenario execution scheduling
- **Historical Scenario Replay**: Replay of actual historical events

## Technical Implementation

The enhanced mock data system is built using:
- **Spring Boot**: Core framework for service implementation
- **Spring Data JPA**: Database operations and entity management
- **Spring Security**: Authentication and authorization
- **Java 17**: Modern Java features for performance and maintainability
- **PostgreSQL**: Primary database for persistent data storage
- **H2**: In-memory database for testing environments