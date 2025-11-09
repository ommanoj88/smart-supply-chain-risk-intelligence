# GitHub Copilot Universal Prompt - EV Fleet Management Migration

## Auto-Understanding PR Context Without Numbering

**Paste this ONCE into Copilot. Then just describe what you're working on - Copilot will know the rest.**

---

## Overview

You are a GitHub Copilot assistant helping migrate an EV Fleet Management System from EV-only to General + EV Excellence (supporting ICE, EV, and Hybrid vehicles).

## Strategic Context

**Customer Feedback**: "Pay more for general which includes EV"

This drives a 3-tier pricing model:
- **BASIC** (₹299/vehicle/month): All vehicles - General fleet management (ICE focus)
- **EV PREMIUM** (₹699/vehicle/month): All features + EV-specific optimization
- **ENTERPRISE** (₹999/vehicle/month): Multi-depot, custom integrations, dedicated support

## Technology Stack

### Backend
- Java 17 + Spring Boot 3.x
- Microservices: 11 independent services
- Spring Cloud (Eureka, API Gateway)
- PostgreSQL 15 + Flyway migrations
- RabbitMQ for events
- Redis for caching

### Frontend
- React 18 + TypeScript
- Redux Toolkit for state management
- Material-UI for components
- Mapbox GL for maps
- Socket.io for real-time updates

## Migration Structure - 18 PRs Total

### Phase 1: Database & Data Model (4 PRs)

#### PR 1: Add Vehicle Fuel Type Support
**Trigger Keywords**: "Add vehicle fuel type support" | "Create FuelType enum" | "Add fuel tank capacity"

**Files to Create/Modify**:
- `Vehicle.java` - Add fuelType field
- `FuelType.java` - Enum (ICE/EV/HYBRID)
- `FuelConsumption.java` - New entity
- `V2__add_fuel_type_support.sql` - Flyway migration
- `V3__add_fuel_consumption_table.sql` - Fuel consumption table

**Changes**:
- Add `fuel_type` enum column (default 'EV' for backward compatibility)
- Add `fuelTankCapacity` (nullable, for ICE/HYBRID)
- Add `fuelLevel` (nullable, current fuel level)
- Create `fuel_consumption` table for ICE vehicle tracking

**Acceptance Criteria**:
- ✅ All existing vehicles default to EV fuel type
- ✅ New columns are nullable (backward compatible)
- ✅ Migration includes rollback script
- ✅ Unit tests for Vehicle entity
- ✅ Repository tests for fuel type filtering

---

#### PR 2: Create Feature Flag System
**Trigger Keywords**: "Create feature flags" | "Enable/disable features by type" | "@RequireFeature"

**Files to Create/Modify**:
- `FeatureToggle.java` - Entity for feature flags
- `FeatureToggleService.java` - Service layer
- `@RequireFeature` - Custom annotation
- `V4__create_feature_toggles.sql` - Migration

**Features Pre-populated**:
- `BATTERY_TRACKING` (EV, HYBRID)
- `FUEL_CONSUMPTION` (ICE, HYBRID)
- `CHARGING_STATIONS` (EV, HYBRID)
- `ENGINE_DIAGNOSTICS` (ICE, HYBRID)
- `REGENERATIVE_BRAKING` (EV, HYBRID)
- `OIL_CHANGE_TRACKING` (ICE, HYBRID)

**Acceptance Criteria**:
- ✅ Feature flags stored in database
- ✅ @RequireFeature annotation validates at runtime
- ✅ Service checks vehicle fuel type before allowing features
- ✅ Exception thrown for invalid feature usage

---

#### PR 3: Extend Telemetry for Multi-Fuel
**Trigger Keywords**: "Extend telemetry for fuel" | "Add engine metrics" | "Telemetry multi-fuel"

**Files to Create/Modify**:
- `TelemetryType.java` - Enum with new types
- `TelemetryData.java` - Extended with ICE fields
- `TelemetryRepository.java` - New query methods
- `V5__extend_telemetry_data.sql` - Migration

**New Telemetry Fields**:
- `fuelLevel` (0-100%)
- `engineRpm` (revolutions per minute)
- `engineTemperature` (°C)
- `engineLoad` (0-100%)
- `engineHours` (total running hours)

**Acceptance Criteria**:
- ✅ Telemetry supports both EV and ICE metrics
- ✅ Validation ensures correct metrics per fuel type
- ✅ Historical telemetry preserved
- ✅ Query performance optimized (< 500ms)

---

#### PR 4: Update Vehicle Queries
**Trigger Keywords**: "Update vehicle queries" | "Fleet composition" | "Filter by fuel type"

**Files to Create/Modify**:
- `VehicleRepository.java` - Add custom queries
- `VehicleService.java` - Business logic methods
- `VehicleController.java` - New endpoints

**New Methods**:
```java
List<Vehicle> findByFuelType(FuelType fuelType);
Map<FuelType, Long> getFleetComposition();
List<Vehicle> getLowBatteryVehicles(double threshold);
List<Vehicle> getLowFuelVehicles(double threshold);
```

**Acceptance Criteria**:
- ✅ Queries use proper indexes
- ✅ Fleet composition returns counts by fuel type
- ✅ Low battery/fuel alerts work independently
- ✅ Performance < 500ms for 10,000 vehicles

---

### Phase 2: API Enhancements (4 PRs)

#### PR 5: Update Vehicle CRUD APIs
**Trigger Keywords**: "Update vehicle CRUD" | "Fuel type in API" | "Vehicle request DTO"

**Files to Create/Modify**:
- `VehicleCreateRequestDTO.java` - Add fuel type
- `VehicleResponseDTO.java` - Include available features
- `FuelTypeValidator.java` - Custom validator
- `VehicleController.java` - Update endpoints

**Validation Rules**:
- EV/HYBRID require `batteryCapacity`
- ICE/HYBRID require `fuelTankCapacity`
- Fuel type must be valid enum value

**Response Enhancement**:
```json
{
  "id": 1,
  "vehicleNumber": "KA01AB1234",
  "fuelType": "HYBRID",
  "batteryCapacity": 50.0,
  "fuelTankCapacity": 45.0,
  "availableFeatures": [
    "BATTERY_TRACKING",
    "FUEL_CONSUMPTION",
    "CHARGING_STATIONS",
    "ENGINE_DIAGNOSTICS"
  ]
}
```

**Acceptance Criteria**:
- ✅ Swagger docs updated
- ✅ Validation error messages clear
- ✅ Backward compatible with existing clients
- ✅ Unit tests for all validators

---

#### PR 6: Extend Telemetry APIs
**Trigger Keywords**: "Extend telemetry APIs" | "Multi-fuel telemetry ingestion" | "Telemetry processor"

**Files to Create/Modify**:
- `TelemetryRequestDTO.java` - Support all metric types
- `TelemetryValidator.java` - Validate by fuel type
- `TelemetryProcessingService.java` - Route to correct handler

**Processing Logic**:
```java
if (vehicle.getFuelType() == FuelType.EV) {
    processBatteryMetrics(telemetry);
} else if (vehicle.getFuelType() == FuelType.ICE) {
    processEngineMetrics(telemetry);
} else {
    processBatteryMetrics(telemetry);
    processEngineMetrics(telemetry);
}
```

**Acceptance Criteria**:
- ✅ EV metrics routed to battery handler
- ✅ ICE metrics routed to engine handler
- ✅ Hybrid vehicles process both
- ✅ Invalid metrics rejected with clear errors

---

#### PR 7: Multi-Fuel Trip Analytics
**Trigger Keywords**: "Trip analytics" | "Multi-fuel cost calculation" | "EV vs ICE cost"

**Files to Create/Modify**:
- `EVCostCalculator.java` - Energy cost calculations
- `ICECostCalculator.java` - Fuel cost calculations
- `MultiFleetAnalyticsService.java` - Aggregate analytics
- `TripAnalyticsController.java` - New endpoints

**Cost Calculations**:
- **EV**: Energy consumed (kWh) × Electricity rate
- **ICE**: Fuel consumed (L) × Fuel price
- **Carbon Footprint**: Different formulas for EV vs ICE

**Acceptance Criteria**:
- ✅ Cost calculations accurate per fuel type
- ✅ Carbon footprint calculated correctly
- ✅ Analytics aggregatable by fleet
- ✅ Historical comparison available

---

#### PR 8: Feature Availability in APIs
**Trigger Keywords**: "Feature availability" | "Conditional features" | "Available features API"

**Files to Create/Modify**:
- `AvailableFeaturesDTO.java` - Feature list response
- `TripService.java` - Check features before trip operations
- `FeatureController.java` - New endpoint

**New Endpoint**:
```
GET /api/vehicles/{id}/features
```

**Response**:
```json
{
  "vehicleId": 1,
  "fuelType": "HYBRID",
  "availableFeatures": [
    {
      "name": "BATTERY_TRACKING",
      "enabled": true,
      "description": "Real-time battery monitoring"
    },
    {
      "name": "CHARGING_STATIONS",
      "enabled": true,
      "description": "Find nearby charging stations"
    }
  ]
}
```

**Acceptance Criteria**:
- ✅ Frontend can query available features
- ✅ Features dynamically shown/hidden in UI
- ✅ Services validate feature availability
- ✅ Clear error when unavailable feature used

---

### Phase 3: Charging Service (2 PRs)

#### PR 9: Charging Validation
**Trigger Keywords**: "Charging validation" | "EV-only charging" | "Vehicle type validator"

**Files to Create/Modify**:
- `VehicleTypeValidator.java` - Validate fuel type
- `NotAnEVVehicleException.java` - Custom exception
- `ChargingSessionController.java` - Add validation

**Validation Logic**:
```java
public void validateChargingEligibility(Vehicle vehicle) {
    if (vehicle.getFuelType() != FuelType.EV && 
        vehicle.getFuelType() != FuelType.HYBRID) {
        throw new NotAnEVVehicleException(
            "Vehicle " + vehicle.getVehicleNumber() + 
            " does not support charging"
        );
    }
}
```

**Acceptance Criteria**:
- ✅ ICE vehicles cannot start charging sessions
- ✅ Returns 400 Bad Request with clear message
- ✅ EV and Hybrid vehicles can charge
- ✅ Unit tests for all scenarios

---

#### PR 10: Charging Analytics
**Trigger Keywords**: "Charging analytics" | "Station utilization" | "Charging metrics"

**Files to Create/Modify**:
- `ChargingAnalyticsService.java` - Analytics calculations
- `ChargingAnalyticsController.java` - API endpoints

**Metrics Provided**:
- Station utilization rate (%)
- Average session cost
- Total energy charged (kWh)
- Peak usage times
- Cost per kWh by station

**Acceptance Criteria**:
- ✅ Accurate utilization calculations
- ✅ Historical analytics available
- ✅ Export to CSV/PDF supported
- ✅ Real-time dashboard updates

---

### Phase 4: Maintenance Service (2 PRs)

#### PR 11: ICE Maintenance Services
**Trigger Keywords**: "ICE maintenance" | "Oil change schedules" | "Maintenance type enum"

**Files to Create/Modify**:
- `MaintenanceType.java` - Enum with ICE types
- `MaintenanceScheduleBuilder.java` - Generate schedules
- `V6__add_ice_maintenance_types.sql` - Migration

**Maintenance Types**:
- `OIL_CHANGE` (ICE, HYBRID)
- `AIR_FILTER_REPLACEMENT` (ICE, HYBRID)
- `FUEL_FILTER_REPLACEMENT` (ICE)
- `TRANSMISSION_FLUID` (ICE, HYBRID)
- `COOLANT_FLUSH` (ICE, HYBRID)
- `SPARK_PLUG_REPLACEMENT` (ICE)
- `BATTERY_CHECK` (EV, HYBRID)
- `BRAKE_FLUID_REPLACEMENT` (ALL)

**Schedule Builder Logic**:
```java
public List<MaintenanceSchedule> buildSchedule(Vehicle vehicle) {
    List<MaintenanceSchedule> schedules = new ArrayList<>();
    
    if (vehicle.getFuelType() == FuelType.ICE || 
        vehicle.getFuelType() == FuelType.HYBRID) {
        schedules.add(createOilChangeSchedule(vehicle));
        schedules.add(createFilterReplacementSchedule(vehicle));
    }
    
    if (vehicle.getFuelType() == FuelType.EV || 
        vehicle.getFuelType() == FuelType.HYBRID) {
        schedules.add(createBatteryCheckSchedule(vehicle));
    }
    
    return schedules;
}
```

**Acceptance Criteria**:
- ✅ Schedules appropriate for fuel type
- ✅ Interval calculations correct
- ✅ Notifications sent before due date
- ✅ Historical maintenance tracked

---

#### PR 12: Maintenance Cost Tracking
**Trigger Keywords**: "Maintenance costs" | "EV vs ICE maintenance" | "Cost tracking"

**Files to Create/Modify**:
- `MaintenanceCostAnalyticsService.java` - Cost analytics

**Analytics Provided**:
- Total maintenance cost by vehicle
- Cost breakdown by maintenance type
- EV vs ICE maintenance cost comparison
- Total Cost of Ownership (TCO) calculations

**Acceptance Criteria**:
- ✅ Accurate cost tracking
- ✅ TCO includes fuel/energy costs
- ✅ Comparison reports available
- ✅ Export functionality

---

### Phase 5: Frontend Updates (4 PRs)

#### PR 13: Vehicle Forms
**Trigger Keywords**: "Vehicle forms" | "Fuel type selector" | "Conditional fields form"

**Files to Create/Modify**:
- `FuelTypeSelector.tsx` - Radio/dropdown component
- `AddVehicle.tsx` - Update form
- `constants/fuelTypes.ts` - Constants

**Form Behavior**:
```typescript
{fuelType === 'EV' || fuelType === 'HYBRID' ? (
  <TextField
    name="batteryCapacity"
    label="Battery Capacity (kWh)"
    required
  />
) : null}

{fuelType === 'ICE' || fuelType === 'HYBRID' ? (
  <TextField
    name="fuelTankCapacity"
    label="Fuel Tank Capacity (L)"
    required
  />
) : null}
```

**Acceptance Criteria**:
- ✅ Fuel type selector with icons
- ✅ Conditional fields show/hide correctly
- ✅ Validation matches backend
- ✅ Form submission works for all types

---

#### PR 14: Vehicle List & Details
**Trigger Keywords**: "Vehicle list" | "Vehicle details" | "FuelStatusPanel" | "Fuel% display"

**Files to Create/Modify**:
- `VehicleList.tsx` - Update with fuel type icons
- `VehicleDetails.tsx` - Tabs for features
- `FuelStatusPanel.tsx` - New component

**Visual Indicators**:
- EV: Green lightning bolt icon
- ICE: Orange fuel pump icon
- HYBRID: Blue dual icon

**Status Display**:
- Battery % (for EV/HYBRID)
- Fuel % (for ICE/HYBRID)
- Color coding: Green (>50%), Yellow (20-50%), Red (<20%)

**Acceptance Criteria**:
- ✅ Icons display correctly
- ✅ Tabs show based on available features
- ✅ Status updates in real-time
- ✅ Responsive design

---

#### PR 15: Station Discovery
**Trigger Keywords**: "Station discovery" | "Charging stations" | "Fuel stations" | "StationMap"

**Files to Create/Modify**:
- `StationDiscovery.tsx` - Main component
- `StationMap.tsx` - Map view
- `StationCard.tsx` - Station details
- `fuelService.ts` - API integration

**Behavior**:
- EV vehicles see charging stations
- ICE vehicles see fuel stations
- HYBRID vehicles see toggle to switch views

**Map Integration**:
```typescript
const getStationsForVehicle = (vehicle: Vehicle) => {
  if (vehicle.fuelType === 'EV') {
    return getChargingStations();
  } else if (vehicle.fuelType === 'ICE') {
    return getFuelStations();
  } else {
    return [...getChargingStations(), ...getFuelStations()];
  }
};
```

**Acceptance Criteria**:
- ✅ Map shows correct station types
- ✅ Real-time availability updates
- ✅ Route planning to station
- ✅ Station details on click

---

#### PR 16: Dashboard Overview
**Trigger Keywords**: "Dashboard" | "Fleet composition chart" | "Cost breakdown"

**Files to Create/Modify**:
- `FleetCompositionCard.tsx` - Pie/donut chart
- `CostBreakdownCard.tsx` - Bar chart
- `MaintenanceAlertsCard.tsx` - Alert list

**Charts**:
1. **Fleet Composition**: EV %, ICE %, HYBRID %
2. **Cost Breakdown**: Energy cost vs Fuel cost
3. **Maintenance Alerts**: Grouped by vehicle type

**Acceptance Criteria**:
- ✅ Charts render correctly
- ✅ Real-time data updates
- ✅ Drill-down functionality
- ✅ Export to PDF/CSV

---

### Phase 6: Billing & Monetization (2 PRs)

#### PR 17: Pricing Tiers
**Trigger Keywords**: "Pricing tiers" | "Three-tier pricing" | "PricingTier entity"

**Files to Create/Modify**:
- `PricingTier.java` - Entity
- `PricingService.java` - Tier assignment logic
- `PricingController.java` - API endpoints
- `PricingPage.tsx` - Frontend pricing page

**Pricing Logic**:
```java
public PricingTier assignTier(Organization org) {
    long evCount = vehicleRepository.countByOrganizationAndFuelType(
        org, FuelType.EV
    );
    long totalCount = vehicleRepository.countByOrganization(org);
    
    if (evCount > 0) {
        return PricingTier.EV_PREMIUM; // ₹699/vehicle/month
    } else {
        return PricingTier.BASIC; // ₹299/vehicle/month
    }
}
```

**Acceptance Criteria**:
- ✅ Tier assignment based on fleet composition
- ✅ Upgrade/downgrade functionality
- ✅ Pricing page with feature comparison
- ✅ Payment gateway integration

---

#### PR 18: Invoice Generation
**Trigger Keywords**: "Invoice generation" | "Monthly billing" | "Payment processing"

**Files to Create/Modify**:
- `InvoiceGenerationService.java` - Generate invoices
- `PaymentProcessingService.java` - Process payments
- `BillingPage.tsx` - Invoice list/details

**Invoice Generation**:
```java
public Invoice generateMonthlyInvoice(Organization org) {
    Map<PricingTier, Long> vehiclesByTier = 
        getVehicleCountByTier(org);
    
    double total = 0;
    for (Map.Entry<PricingTier, Long> entry : vehiclesByTier.entrySet()) {
        total += entry.getKey().getMonthlyRate() * entry.getValue();
    }
    
    return new Invoice(org, total, LocalDate.now());
}
```

**Acceptance Criteria**:
- ✅ Automated monthly invoice generation
- ✅ Email notifications sent
- ✅ Payment processing integrated
- ✅ Invoice PDF generation

---

## Architecture Patterns

### Vehicle Entity Pattern

```java
@Entity
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    // EV fields (optional)
    private Double batteryCapacity;
    private Double currentBatterySoc;
    private String defaultChargerType;

    // ICE fields (optional)
    private Double fuelTankCapacity;
    private Double fuelLevel;

    // Common fields
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    private String make;
    private String model;
    private Integer year;

    // ... getters, setters, constructors
}
```

### Service Method Pattern

When fuel type matters, create separate methods:

```java
@Service
public class TripService {
    
    public TripCost calculateTripCost(Long vehicleId, Trip trip) {
        Vehicle vehicle = vehicleService.getVehicle(vehicleId);
        
        return switch (vehicle.getFuelType()) {
            case EV -> calculateEVTripCost(vehicle, trip);
            case ICE -> calculateICETripCost(vehicle, trip);
            case HYBRID -> calculateHybridTripCost(vehicle, trip);
        };
    }
    
    private TripCost calculateEVTripCost(Vehicle vehicle, Trip trip) {
        double energyConsumed = trip.getDistance() * EV_CONSUMPTION_RATE;
        double cost = energyConsumed * ELECTRICITY_RATE;
        return new TripCost(cost, energyConsumed, "kWh");
    }
    
    private TripCost calculateICETripCost(Vehicle vehicle, Trip trip) {
        double fuelConsumed = trip.getDistance() * ICE_CONSUMPTION_RATE;
        double cost = fuelConsumed * FUEL_PRICE;
        return new TripCost(cost, fuelConsumed, "L");
    }
    
    private TripCost calculateHybridTripCost(Vehicle vehicle, Trip trip) {
        // Combine both calculations
        TripCost evCost = calculateEVTripCost(vehicle, trip);
        TripCost iceCost = calculateICETripCost(vehicle, trip);
        return combineCosts(evCost, iceCost);
    }
}
```

### Frontend Component Pattern

```typescript
export const VehicleStatusPanel: React.FC<{ vehicle: Vehicle }> = ({ vehicle }) => {
    return (
        <Card>
            <CardHeader>
                <Typography variant="h6">Vehicle Status</Typography>
            </CardHeader>
            <CardContent>
                {/* Always shown */}
                <Box>
                    <Typography>Vehicle: {vehicle.vehicleNumber}</Typography>
                    <Typography>Type: {vehicle.fuelType}</Typography>
                </Box>

                {/* EV/Hybrid specific */}
                {(vehicle.fuelType === 'EV' || vehicle.fuelType === 'HYBRID') && (
                    <Box mt={2}>
                        <Typography>Battery Level:</Typography>
                        <LinearProgress 
                            variant="determinate" 
                            value={vehicle.currentBatterySoc} 
                        />
                        <Typography>{vehicle.currentBatterySoc}%</Typography>
                    </Box>
                )}

                {/* ICE/Hybrid specific */}
                {(vehicle.fuelType === 'ICE' || vehicle.fuelType === 'HYBRID') && (
                    <Box mt={2}>
                        <Typography>Fuel Level:</Typography>
                        <LinearProgress 
                            variant="determinate" 
                            value={vehicle.fuelLevel} 
                        />
                        <Typography>{vehicle.fuelLevel}%</Typography>
                    </Box>
                )}
            </CardContent>
        </Card>
    );
};
```

### Validation Pattern

```java
@Component
public class VehicleValidator {
    
    public void validateVehicleRequest(VehicleCreateRequestDTO dto) {
        validateFuelType(dto);
        validateRequiredFieldsByFuelType(dto);
    }
    
    private void validateFuelType(VehicleCreateRequestDTO dto) {
        if (dto.getFuelType() == null) {
            throw new ValidationException("Fuel type is required");
        }
        
        try {
            FuelType.valueOf(dto.getFuelType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                "Invalid fuel type. Must be one of: ICE, EV, HYBRID"
            );
        }
    }
    
    private void validateRequiredFieldsByFuelType(VehicleCreateRequestDTO dto) {
        FuelType fuelType = FuelType.valueOf(dto.getFuelType().toUpperCase());
        
        // EV and Hybrid need battery capacity
        if (fuelType == FuelType.EV || fuelType == FuelType.HYBRID) {
            if (dto.getBatteryCapacity() == null || dto.getBatteryCapacity() <= 0) {
                throw new ValidationException(
                    "Battery capacity is required for " + fuelType + " vehicles"
                );
            }
        }
        
        // ICE and Hybrid need fuel tank capacity
        if (fuelType == FuelType.ICE || fuelType == FuelType.HYBRID) {
            if (dto.getFuelTankCapacity() == null || dto.getFuelTankCapacity() <= 0) {
                throw new ValidationException(
                    "Fuel tank capacity is required for " + fuelType + " vehicles"
                );
            }
        }
    }
}
```

## Acceptance Criteria (Every PR Must Have)

### Code Quality
- ✅ Code follows existing patterns and style
- ✅ All classes have proper JavaDoc/comments
- ✅ No code duplication
- ✅ Proper exception handling with meaningful messages
- ✅ Security review passed (no vulnerabilities)

### Testing
- ✅ Unit tests written (> 85% coverage)
- ✅ Integration tests for APIs/database
- ✅ All existing tests still pass
- ✅ Edge cases tested
- ✅ Performance tests for queries

### Documentation
- ✅ Swagger/OpenAPI documentation updated
- ✅ README updated if needed
- ✅ API examples provided
- ✅ Architecture diagrams updated

### Compatibility
- ✅ No breaking changes to existing APIs
- ✅ Backward compatible (existing data still works)
- ✅ Database migrations tested independently
- ✅ Rollback scripts included for migrations

### Performance
- ✅ Queries < 500ms for reasonable data size
- ✅ API responses < 500ms p99
- ✅ New indexes added where needed
- ✅ Bulk operations optimized

## Critical Do's & Don'ts

### DO:
- ✅ Make new fields NULLABLE in database (backward compatibility)
- ✅ Default `fuel_type` to 'EV' for existing vehicles
- ✅ Keep all existing EV fields in entities
- ✅ Test with mixed fleet (EV + ICE + HYBRID together)
- ✅ Use `@Query` annotations for complex JPA queries
- ✅ Add indexes to filtered columns
- ✅ Write tests for edge cases
- ✅ Document non-obvious logic in comments
- ✅ Include rollback scripts in migrations
- ✅ Use feature flags for conditional logic

### DON'T:
- ❌ Change `fuel_type` for existing vehicles (only during initial migration)
- ❌ Add new required fields without migration
- ❌ Break existing API contracts
- ❌ Assume all vehicles have batteries
- ❌ Assume all vehicles have fuel tanks
- ❌ Hardcode fuel type logic in controllers
- ❌ Skip Swagger documentation updates
- ❌ Skip performance testing
- ❌ Ignore error handling for invalid fuel types
- ❌ Create large migrations without transactions

## File Structure Reference

### Backend Structure

```
backend/
├── fleet-service/
│   └── src/main/java/com/evfleet/fleet/
│       ├── model/              # Entities (Vehicle, FuelType, etc.)
│       ├── repository/         # JPA Repositories
│       ├── service/            # Business logic
│       ├── controller/         # REST Controllers
│       ├── dto/                # Request/Response DTOs
│       ├── validation/         # Validators
│       ├── annotation/         # Custom annotations (@RequireFeature)
│       └── exception/          # Custom exceptions
├── charging-service/
│   └── src/main/java/com/evfleet/charging/
│       ├── model/              # ChargingSession, ChargingStation
│       ├── service/            # Charging business logic
│       └── analytics/          # Charging analytics
├── maintenance-service/
│   └── src/main/java/com/evfleet/maintenance/
│       ├── model/              # MaintenanceRecord, MaintenanceType
│       └── service/            # Maintenance scheduling
└── src/main/resources/
    ├── db/migration/           # Flyway migrations
    │   ├── V1__initial_schema.sql
    │   ├── V2__add_fuel_type_support.sql
    │   ├── V3__add_fuel_consumption_table.sql
    │   ├── V4__create_feature_toggles.sql
    │   ├── V5__extend_telemetry_data.sql
    │   └── V6__add_ice_maintenance_types.sql
    └── application.yml
```

### Frontend Structure

```
frontend/src/
├── components/
│   ├── fleet/
│   │   ├── VehicleList.tsx
│   │   ├── VehicleDetails.tsx
│   │   ├── AddVehicle.tsx
│   │   ├── FuelTypeSelector.tsx
│   │   └── FuelStatusPanel.tsx
│   ├── charging/
│   │   ├── StationDiscovery.tsx
│   │   ├── StationMap.tsx
│   │   ├── StationCard.tsx
│   │   └── ChargingSession.tsx
│   ├── maintenance/
│   │   ├── MaintenanceSchedule.tsx
│   │   ├── MaintenanceHistory.tsx
│   │   └── MaintenanceCost.tsx
│   ├── dashboard/
│   │   ├── FleetCompositionCard.tsx
│   │   ├── CostBreakdownCard.tsx
│   │   └── MaintenanceAlertsCard.tsx
│   └── common/
│       ├── FeatureBadge.tsx
│       ├── StatusIndicator.tsx
│       └── LoadingSpinner.tsx
├── services/
│   ├── vehicleService.ts
│   ├── chargingService.ts
│   ├── fuelService.ts
│   └── analyticsService.ts
├── pages/
│   ├── FleetManagement.tsx
│   ├── StationDiscovery.tsx
│   ├── MaintenancePage.tsx
│   ├── PricingPage.tsx
│   └── BillingPage.tsx
├── redux/
│   ├── slices/
│   │   ├── vehicleSlice.ts
│   │   ├── chargingSlice.ts
│   │   └── analyticsSlice.ts
│   └── store.ts
└── constants/
    ├── fuelTypes.ts
    ├── maintenanceTypes.ts
    └── pricingTiers.ts
```

## How to Recognize Which PR You're Working On

When you mention any of these keywords, Copilot will know EXACTLY which PR:

| PR | Keywords |
|---|---|
| **PR 1** | "Add vehicle fuel type support" \| "Create FuelType enum" \| "Add fuel tank capacity" |
| **PR 2** | "Create feature flags" \| "Enable/disable features by type" \| "@RequireFeature" |
| **PR 3** | "Extend telemetry for fuel" \| "Add engine metrics" \| "Telemetry multi-fuel" |
| **PR 4** | "Update vehicle queries" \| "Fleet composition" \| "Filter by fuel type" |
| **PR 5** | "Update vehicle CRUD" \| "Fuel type in API" \| "Vehicle request DTO" |
| **PR 6** | "Extend telemetry APIs" \| "Multi-fuel telemetry ingestion" \| "Telemetry processor" |
| **PR 7** | "Trip analytics" \| "Multi-fuel cost calculation" \| "EV vs ICE cost" |
| **PR 8** | "Feature availability" \| "Conditional features" \| "Available features API" |
| **PR 9** | "Charging validation" \| "EV-only charging" \| "Vehicle type validator" |
| **PR 10** | "Charging analytics" \| "Station utilization" \| "Charging metrics" |
| **PR 11** | "ICE maintenance" \| "Oil change schedules" \| "Maintenance type enum" |
| **PR 12** | "Maintenance costs" \| "EV vs ICE maintenance" \| "Cost tracking" |
| **PR 13** | "Vehicle forms" \| "Fuel type selector" \| "Conditional fields form" |
| **PR 14** | "Vehicle list" \| "Vehicle details" \| "FuelStatusPanel" \| "Fuel% display" |
| **PR 15** | "Station discovery" \| "Charging stations" \| "Fuel stations" \| "StationMap" |
| **PR 16** | "Dashboard" \| "Fleet composition chart" \| "Cost breakdown" |
| **PR 17** | "Pricing tiers" \| "Three-tier pricing" \| "PricingTier entity" |
| **PR 18** | "Invoice generation" \| "Monthly billing" \| "Payment processing" |

## Multi-Fuel Aware Logic

Copilot understands:

- **EV** = requires battery, charging, energy cost, carbon via kWh formula
- **ICE** = requires fuel tank, fuel stations, fuel cost, carbon via liter formula
- **HYBRID** = requires BOTH sets of fields and logic
- Each service method must check `vehicle.getFuelType()` to route correctly
- Each API endpoint must validate fuel-type-specific required fields
- Each frontend component must conditionally render fuel-type-specific UI

## What You Don't Need to Say

Instead of saying PR numbers, just describe what you want:

| ❌ Don't Say | ✅ Say This |
|---|---|
| "PR #5" | "Update vehicle CRUD APIs" |
| "PR #13" | "Work on vehicle forms" |
| "Which PR is this?" | Copilot will ask clarifying questions if ambiguous |
| "What files?" | Copilot will suggest the exact files |
| "What tests?" | Copilot will suggest test structure |
| "Database migration?" | Copilot will generate if needed |

## When Working on a PR

| You Say | Copilot Will |
|---|---|
| "Work on [PR description]" | Identify PR + start generating code |
| "Show me the tests" | Generate comprehensive test suite |
| "Add validation" | Suggest validation logic per fuel type |
| "Update Swagger" | Generate OpenAPI annotations |
| "What about error handling?" | Suggest custom exceptions + handlers |
| "Add database migration" | Generate Flyway SQL script |
| "Frontend component" | Generate React component with patterns |
| "API endpoint" | Generate Controller + DTO + tests |
| "Explain the dependency" | Reference what other PRs must complete first |

## Ready State

Copilot is ready to:
- ✅ Generate backend code (Java/Spring Boot)
- ✅ Generate frontend code (React/TypeScript)
- ✅ Generate database migrations (Flyway SQL)
- ✅ Generate tests (JUnit 5, Mockito, React Testing Library)
- ✅ Suggest error handling
- ✅ Update API documentation (Swagger)
- ✅ Suggest performance optimizations
- ✅ Review security
- ✅ Suggest validation logic
- ✅ Help debug issues

---

## Usage Instructions

### One-Time Setup:
1. Copy this entire document
2. Paste into GitHub Copilot / Claude Code
3. Wait for acknowledgment

### For Each PR:
1. Just describe what you want to work on
2. Examples:
   - "Add vehicle fuel type support"
   - "Update vehicle CRUD APIs"
   - "Work on vehicle forms"
   - "Create feature flags"
   - "Extend telemetry for multi-fuel"

3. Copilot will automatically:
   - Know which PR you mean
   - Know all the files to create/modify
   - Know the acceptance criteria
   - Know what tests to write
   - Generate appropriate code
   - Suggest database migrations
   - Update documentation

**That's it!** No need to mention PR numbers or repeat context.

---

## Why This Works

✅ Self-contained prompt (doesn't reference external docs)
✅ Comprehensive enough to handle any PR description
✅ Maps descriptions → PR specifications automatically
✅ Includes all architecture patterns
✅ Includes all file locations
✅ Includes acceptance criteria
✅ Knows what tests are needed
✅ Knows validation requirements
✅ Knows multi-fuel logic patterns
✅ Can auto-identify which PR by description

---

**Created**: 2025-11-09  
**Version**: 1.0  
**Status**: Ready to use  
**Next Step**: Copy this prompt → Paste into Copilot → Start describing PRs

