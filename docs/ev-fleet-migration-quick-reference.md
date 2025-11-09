# EV Fleet Migration - Quick Reference Guide

## 🎯 Quick PR Lookup

| Phase | PR | Keywords | Key Files |
|-------|----|----|-----------|
| **Phase 1: Database** ||||
| | 1 | "vehicle fuel type" | Vehicle.java, FuelType.java, V2 migration |
| | 2 | "feature flags" | FeatureToggle.java, @RequireFeature, V4 migration |
| | 3 | "telemetry multi-fuel" | TelemetryData.java, V5 migration |
| | 4 | "vehicle queries" | VehicleRepository.java, VehicleService.java |
| **Phase 2: APIs** ||||
| | 5 | "vehicle CRUD API" | VehicleCreateRequestDTO.java, FuelTypeValidator.java |
| | 6 | "telemetry APIs" | TelemetryRequestDTO.java, TelemetryValidator.java |
| | 7 | "trip analytics" | EVCostCalculator.java, ICECostCalculator.java |
| | 8 | "feature availability" | AvailableFeaturesDTO.java, FeatureController.java |
| **Phase 3: Charging** ||||
| | 9 | "charging validation" | VehicleTypeValidator.java, NotAnEVVehicleException.java |
| | 10 | "charging analytics" | ChargingAnalyticsService.java |
| **Phase 4: Maintenance** ||||
| | 11 | "ICE maintenance" | MaintenanceType.java, MaintenanceScheduleBuilder.java |
| | 12 | "maintenance costs" | MaintenanceCostAnalyticsService.java |
| **Phase 5: Frontend** ||||
| | 13 | "vehicle forms" | FuelTypeSelector.tsx, AddVehicle.tsx |
| | 14 | "vehicle list" | VehicleList.tsx, FuelStatusPanel.tsx |
| | 15 | "station discovery" | StationDiscovery.tsx, StationMap.tsx |
| | 16 | "dashboard" | FleetCompositionCard.tsx, CostBreakdownCard.tsx |
| **Phase 6: Billing** ||||
| | 17 | "pricing tiers" | PricingTier.java, PricingService.java |
| | 18 | "invoice generation" | InvoiceGenerationService.java |

## 🔑 Fuel Type Decision Matrix

| Feature | EV | ICE | HYBRID |
|---------|----|----|--------|
| Battery Capacity | ✅ Required | ❌ | ✅ Required |
| Fuel Tank | ❌ | ✅ Required | ✅ Required |
| Charging Sessions | ✅ | ❌ | ✅ |
| Fuel Consumption | ❌ | ✅ | ✅ |
| Battery Telemetry | ✅ | ❌ | ✅ |
| Engine Telemetry | ❌ | ✅ | ✅ |
| Oil Changes | ❌ | ✅ | ✅ |
| Battery Checks | ✅ | ❌ | ✅ |

## 💡 Common Code Patterns

### Backend: Fuel Type Switch
```java
switch (vehicle.getFuelType()) {
    case EV -> handleEV(vehicle);
    case ICE -> handleICE(vehicle);
    case HYBRID -> handleHybrid(vehicle);
}
```

### Backend: Validation
```java
if (fuelType == FuelType.EV || fuelType == FuelType.HYBRID) {
    requireNotNull(batteryCapacity, "Battery required for " + fuelType);
}
```

### Frontend: Conditional Rendering
```typescript
{(fuelType === 'EV' || fuelType === 'HYBRID') && (
    <BatteryFields />
)}

{(fuelType === 'ICE' || fuelType === 'HYBRID') && (
    <FuelFields />
)}
```

## 📊 Database Migration Checklist

- [ ] Add columns as NULLABLE
- [ ] Set default values for existing data
- [ ] Create indexes on filtered columns
- [ ] Write rollback script
- [ ] Test with production-sized data
- [ ] Verify no breaking changes
- [ ] Check query performance

## ✅ Testing Checklist (Every PR)

### Unit Tests
- [ ] Entity validation
- [ ] Service business logic
- [ ] DTO validation
- [ ] Edge cases
- [ ] Error handling

### Integration Tests
- [ ] API endpoints
- [ ] Database queries
- [ ] Multi-fuel scenarios
- [ ] Feature flags
- [ ] Exception cases

### Performance Tests
- [ ] Query < 500ms
- [ ] API response < 500ms
- [ ] Bulk operations
- [ ] Index effectiveness

## 🚨 Critical Validations

### Vehicle Creation
```
EV: batteryCapacity > 0
ICE: fuelTankCapacity > 0
HYBRID: batteryCapacity > 0 AND fuelTankCapacity > 0
```

### Charging Session
```
ONLY: fuelType IN (EV, HYBRID)
ERROR: NotAnEVVehicleException for ICE
```

### Telemetry
```
EV: batteryLevel, chargingStatus, batteryTemperature
ICE: fuelLevel, engineRpm, engineTemperature
HYBRID: ALL OF ABOVE
```

## 💰 Pricing Tiers

| Tier | Price/Vehicle/Month | Features |
|------|---------------------|----------|
| **BASIC** | ₹299 | ICE focus, basic tracking |
| **EV PREMIUM** | ₹699 | All features + EV optimization |
| **ENTERPRISE** | ₹999 | Multi-depot, custom integrations |

### Assignment Logic
- Has EV/HYBRID vehicles → **EV PREMIUM**
- Only ICE vehicles → **BASIC**
- >50 vehicles → Consider **ENTERPRISE**

## 🎨 UI Component Patterns

### Icons
- EV: ⚡ (Green)
- ICE: ⛽ (Orange)
- HYBRID: ⚡⛽ (Blue)

### Status Colors
- >50%: Green
- 20-50%: Yellow
- <20%: Red

### Conditional Tabs
```typescript
tabs = [
  { label: 'Overview', visible: true },
  { label: 'Battery', visible: isEVOrHybrid },
  { label: 'Fuel', visible: isICEOrHybrid },
  { label: 'Charging', visible: isEVOrHybrid },
  { label: 'Maintenance', visible: true },
]
```

## 🔧 Troubleshooting

### "Battery capacity required"
- Check: Vehicle is EV or HYBRID
- Fix: Add batteryCapacity field

### "Vehicle does not support charging"
- Check: Vehicle fuel type
- Fix: Only EV/HYBRID can charge

### "Invalid fuel type"
- Check: Enum value (ICE/EV/HYBRID)
- Fix: Use correct enum value

### Query Performance
- Check: Indexes on fuel_type, status
- Fix: Add composite indexes

## 📝 Acceptance Criteria Template

For every PR, ensure:
- [ ] Code follows existing patterns
- [ ] JavaDoc/comments added
- [ ] No code duplication
- [ ] Exception handling with clear messages
- [ ] Security review passed
- [ ] Unit tests (>85% coverage)
- [ ] Integration tests
- [ ] All existing tests pass
- [ ] Swagger docs updated
- [ ] No breaking changes
- [ ] Backward compatible
- [ ] Migration tested
- [ ] Performance validated
- [ ] Rollback script included

## 🚀 Development Workflow

1. **Start**: Copy universal prompt to Copilot
2. **Describe**: "Work on [feature description]"
3. **Generate**: Let Copilot create code
4. **Test**: Run unit + integration tests
5. **Validate**: Check acceptance criteria
6. **Document**: Update Swagger/README
7. **Review**: Security + performance
8. **Commit**: Push PR

## 📚 Dependencies Between PRs

```
PR 1 (Fuel Type)
  ↓
PR 2 (Feature Flags) → PR 5 (CRUD API) → PR 8 (Feature API)
  ↓
PR 3 (Telemetry) → PR 6 (Telemetry API) → PR 7 (Analytics)
  ↓
PR 4 (Queries) → PR 13 (Forms) → PR 14 (List/Details) → PR 16 (Dashboard)
  ↓
PR 9 (Validation) → PR 10 (Analytics) → PR 15 (Stations)
  ↓
PR 11 (Maintenance) → PR 12 (Cost Tracking)
  ↓
PR 17 (Pricing) → PR 18 (Invoicing)
```

## 🎯 Priority Order (Recommended)

**Week 1-2**: PRs 1, 2, 3, 4 (Foundation)
**Week 3-4**: PRs 5, 6, 7, 8 (APIs)
**Week 5**: PRs 9, 10 (Charging)
**Week 6**: PRs 11, 12 (Maintenance)
**Week 7-8**: PRs 13, 14, 15, 16 (Frontend)
**Week 9**: PRs 17, 18 (Billing)

---

**Print this page and keep it handy while working on the migration!**

Last Updated: 2025-11-09
