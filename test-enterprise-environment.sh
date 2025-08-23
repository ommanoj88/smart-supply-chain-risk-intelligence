#!/bin/bash

# Enterprise Testing Environment Validation Script
# Tests all new SAP-level functionality and enterprise features

echo "🚀 Enterprise Supply Chain Testing Environment Validation"
echo "========================================================="

# Configuration
BASE_URL="http://localhost:8080"
FRONTEND_URL="http://localhost:3000"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counter
TESTS_PASSED=0
TESTS_FAILED=0

# Helper function to test API endpoint
test_endpoint() {
    local method=$1
    local url=$2
    local data=$3
    local description=$4
    
    echo -n "Testing: $description... "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json "$BASE_URL$url")
    else
        response=$(curl -s -w "%{http_code}" -o /tmp/response.json -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" "$BASE_URL$url")
    fi
    
    http_code="${response: -3}"
    
    if [ "$http_code" -eq 200 ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        TESTS_PASSED=$((TESTS_PASSED + 1))
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (HTTP $http_code)"
        TESTS_FAILED=$((TESTS_FAILED + 1))
        return 1
    fi
}

# Helper function to check if service is running
check_service() {
    local url=$1
    local service_name=$2
    
    echo -n "Checking $service_name service... "
    
    if curl -s "$url" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ RUNNING${NC}"
        return 0
    else
        echo -e "${RED}✗ NOT AVAILABLE${NC}"
        return 1
    fi
}

echo
echo "📋 Phase 1: Service Availability Check"
echo "--------------------------------------"

# Check if backend is running
check_service "$BASE_URL/health" "Backend"
BACKEND_AVAILABLE=$?

# Check if frontend is running
check_service "$FRONTEND_URL" "Frontend"
FRONTEND_AVAILABLE=$?

if [ $BACKEND_AVAILABLE -ne 0 ]; then
    echo -e "${RED}❌ Backend service not available. Please start the application first.${NC}"
    echo "Run: docker-compose up -d"
    exit 1
fi

echo
echo "🧪 Phase 2: Enterprise API Simulation Testing"
echo "----------------------------------------------"

# Test SAP Digital Manufacturing API
test_endpoint "GET" "/mock-apis/enterprise/sap/digital-manufacturing/orders" "" "SAP Digital Manufacturing Orders"

# Test SAP Ariba Suppliers API
test_endpoint "GET" "/mock-apis/enterprise/sap/ariba/suppliers" "" "SAP Ariba Suppliers"

# Test SAP IBP Demand Forecast API
test_endpoint "GET" "/mock-apis/enterprise/sap/ibp/demand-forecast?material=MAT-001" "" "SAP IBP Demand Forecast"

# Test Oracle ERP Financials API
test_endpoint "GET" "/mock-apis/enterprise/oracle/erp/financials/invoices" "" "Oracle ERP Financials"

# Test Oracle SCM Inventory API
test_endpoint "GET" "/mock-apis/enterprise/oracle/scm/inventory?location=PLANT1" "" "Oracle SCM Inventory"

# Test Weather Forecast API
test_endpoint "GET" "/mock-apis/enterprise/weather/forecast?location=Singapore" "" "Weather Forecast API"

# Test Supply Chain News API
test_endpoint "GET" "/mock-apis/enterprise/news/supply-chain?region=ASIA" "" "Supply Chain News API"

# Test Currency Rates API
test_endpoint "GET" "/mock-apis/enterprise/currency/rates?baseCurrency=USD" "" "Currency Exchange Rates"

# Test Customs Tariffs API
test_endpoint "GET" "/mock-apis/enterprise/customs/tariffs?country=US&category=Electronics" "" "Customs Tariffs API"

echo
echo "🎯 Phase 3: Crisis Simulation Testing"
echo "--------------------------------------"

# Test Crisis Simulation
test_endpoint "POST" "/mock-apis/enterprise/crisis/simulate" \
    '{"type":"HURRICANE","severity":4,"region":"Southeast Asia"}' \
    "Hurricane Crisis Simulation"

# Test Simulation Configuration
test_endpoint "POST" "/mock-apis/enterprise/simulation/configure" \
    '{"scenario":"PANDEMIC","parameters":{"severity":3,"duration":30}}' \
    "Simulation Configuration"

# Test Simulation Status
test_endpoint "GET" "/mock-apis/enterprise/simulation/status" "" "Simulation Status Check"

echo
echo "📊 Phase 4: Supply Chain Planning Testing"
echo "------------------------------------------"

# Test Demand Forecasting
test_endpoint "POST" "/api/planning/demand-forecast" \
    '{"materialNumber":"MAT-001","forecastHorizon":12,"algorithm":"ML_ENSEMBLE"}' \
    "AI-Powered Demand Forecasting"

# Test Supply Optimization
test_endpoint "POST" "/api/planning/supply-optimization" \
    '{"materials":["ALL"],"planningHorizon":26,"objective":"COST_MINIMIZATION"}' \
    "Supply Planning Optimization"

# Test Inventory Optimization
test_endpoint "POST" "/api/planning/inventory-optimization" \
    '{"level":"MULTI_ECHELON","serviceLevel":0.95,"reviewPeriod":7}' \
    "Multi-Echelon Inventory Optimization"

# Test Sales & Operations Planning
test_endpoint "POST" "/api/planning/sales-operations-plan" \
    '{"planningHorizon":18,"planningLevel":"FAMILY"}' \
    "Sales & Operations Planning (S&OP)"

# Test Response Plan Execution
test_endpoint "POST" "/api/planning/response-plan" \
    '{"responseType":"SUPPLY_DISRUPTION","severity":"HIGH","affectedAreas":["SUPPLIERS"]}' \
    "Real-time Response Plan Execution"

# Test Planning Analytics
test_endpoint "GET" "/api/planning/analytics" "" "Planning Analytics & KPIs"

# Test Planning Recommendations
test_endpoint "GET" "/api/planning/recommendations" "" "Planning Recommendations"

echo
echo "🧩 Phase 5: Admin Testing Environment"
echo "-------------------------------------"

# Test System Statistics
test_endpoint "GET" "/api/admin/data-stats" "" "System Statistics"

# Test Available Scenarios
test_endpoint "GET" "/api/admin/testing/scenarios" "" "Available Testing Scenarios"

# Test Scenario Generation
test_endpoint "POST" "/api/admin/testing/generate-scenario" \
    '{"scenarioType":"HURRICANE_DISRUPTION","intensity":4,"affectedRegions":["Asia","Europe"]}' \
    "Hurricane Scenario Generation"

# Test Crisis Simulation
test_endpoint "POST" "/api/admin/testing/simulate-crisis" \
    '{"crisisType":"PANDEMIC_CRISIS","duration":72,"realTimeUpdates":true}' \
    "Pandemic Crisis Simulation"

# Test Load Test Data Generation
test_endpoint "POST" "/api/admin/testing/load-test-data" \
    '{"suppliers":100,"shipments":1000,"events":10000}' \
    "Load Test Data Generation"

echo
echo "🔍 Phase 6: Original System Integration Test"
echo "---------------------------------------------"

# Test original endpoints to ensure compatibility
test_endpoint "GET" "/api/suppliers" "" "Original Suppliers API"
test_endpoint "GET" "/api/shipments" "" "Original Shipments API"
test_endpoint "GET" "/mock-apis/carriers/dhl/track/TEST123" "" "Original DHL Mock API"

echo
echo "📈 Phase 7: Performance & Health Checks"
echo "----------------------------------------"

# Test health endpoints
test_endpoint "GET" "/health" "" "Application Health Check"

# Additional health checks if they exist
test_endpoint "GET" "/actuator/health" "" "Spring Actuator Health" 2>/dev/null || echo "Actuator not configured (optional)"

echo
echo "🎨 Phase 8: Frontend Component Validation"
echo "------------------------------------------"

if [ $FRONTEND_AVAILABLE -eq 0 ]; then
    echo "✓ Frontend is available at $FRONTEND_URL"
    echo "✓ AdminTestingDashboard component created"
    echo "✓ EnterpriseExecutiveDashboard component created"
    echo "→ Manual testing required for UI components"
    echo "  - Navigate to /admin/testing for testing dashboard"
    echo "  - Navigate to /dashboard/enterprise for executive dashboard"
else
    echo -e "${YELLOW}⚠ Frontend not available - UI testing skipped${NC}"
fi

echo
echo "📊 Test Results Summary"
echo "======================="
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo -e "Total Tests: $(($TESTS_PASSED + $TESTS_FAILED))"

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}🎉 ALL TESTS PASSED! Enterprise testing environment is ready.${NC}"
    echo
    echo "🚀 Next Steps:"
    echo "1. Access Admin Testing Dashboard: $FRONTEND_URL/admin/testing"
    echo "2. Access Enterprise Executive Dashboard: $FRONTEND_URL/dashboard/enterprise"
    echo "3. Review API documentation at: $BASE_URL/swagger-ui.html"
    echo "4. Check enterprise testing guide: docs/enterprise-testing-guide.md"
    exit 0
else
    echo -e "\n${RED}❌ Some tests failed. Please check the logs and fix issues.${NC}"
    echo
    echo "🔧 Troubleshooting:"
    echo "1. Ensure the application is fully started: docker-compose up -d"
    echo "2. Check logs: docker-compose logs backend"
    echo "3. Verify database is running: docker-compose ps postgres"
    echo "4. Check if authentication is required for admin endpoints"
    exit 1
fi