# Comprehensive Testing Documentation
# Smart Supply Chain Risk Intelligence Platform

## Table of Contents

1. [Authentication & User Management Testing](#authentication--user-management-testing)
2. [Supplier Management Testing](#supplier-management-testing)
3. [Shipment Tracking Testing](#shipment-tracking-testing)
4. [Analytics and Dashboard Testing](#analytics-and-dashboard-testing)
5. [Role-Based Access Control Testing](#role-based-access-control-testing)
6. [Integration Testing](#integration-testing)
7. [Performance Testing](#performance-testing)
8. [Security Testing](#security-testing)
9. [Troubleshooting Guide](#troubleshooting-guide)

---

## Authentication & User Management Testing

### 1. Initial Setup and Admin Account Creation

#### Prerequisites
- Application deployed and accessible
- PostgreSQL database running
- Backend services started on port 8080
- Frontend application started on port 3000

#### Test Procedure

**Step 1: Initial Application Access**
1. Open browser and navigate to `http://localhost:3000`
2. **Expected Result**: Login page should display with dual authentication options
3. **Verification**: 
   - JWT login form visible
   - Google Sign-In button visible
   - "Forgot Password" link present
   - Professional UI with enterprise branding

**Step 2: Create Initial Super Admin Account**
1. Click "Register" link on login page
2. Fill in registration form:
   ```
   Username: admin
   Email: admin@company.com
   Password: SecurePassword123!
   First Name: System
   Last Name: Administrator
   ```
3. Submit registration form
4. **Expected Result**: Registration successful, email verification prompt displayed
5. **Verification**: Check database for new user record with SUPER_ADMIN role

**Step 3: Email Verification Process**
1. Check email inbox for verification email
2. Click verification link in email
3. **Expected Result**: Account activated, redirect to login page
4. **Verification**: User status changed to 'active' in database

**Step 4: Initial Admin Login**
1. Enter admin credentials on login page
2. Click "Sign In"
3. **Expected Result**: 
   - Successful login
   - Redirect to Executive Dashboard
   - User profile shows SUPER_ADMIN role
   - Navigation menu includes admin sections

**Step 5: Access Admin Dashboard**
1. Navigate to `/admin/users` or click "User Management" in navigation
2. **Expected Result**: 
   - User Management interface loads
   - Shows current admin user in table
   - All admin features accessible
   - Role matrix displays correctly

---

### 2. User Creation and Role Assignment

#### Test Procedure

**Step 1: Navigate to User Management**
1. From main navigation, click "User Management"
2. **Expected Result**: User Management dashboard displays
3. **Verification**: 
   - Current users table visible
   - "Add User" button present
   - Role filter options available
   - Stats cards show correct counts

**Step 2: Create Supply Chain Manager**
1. Click "Add User" button
2. Fill in user creation form:
   ```
   Username: jsmith
   Email: j.smith@company.com
   First Name: John
   Last Name: Smith
   Role: Supply Chain Manager
   Department: Supply Chain
   Phone: +1-555-0124
   Location: Chicago, IL
   ```
3. Click "Create User"
4. **Expected Result**: 
   - User created successfully
   - Appears in users table with "Pending" status
   - Role chip displays correctly with appropriate color

**Step 3: Create Additional Users for Each Role**

Repeat Step 2 for each role type:

**Analyst User:**
```
Username: mwilson
Email: m.wilson@company.com
First Name: Maria
Last Name: Wilson
Role: Supply Chain Analyst
Department: Analytics
Phone: +1-555-0125
Location: New York, NY
```

**Viewer User:**
```
Username: viewer01
Email: viewer@company.com
First Name: Alex
Last Name: Johnson
Role: Viewer
Department: Operations
Location: Austin, TX
```

**Supplier Portal User:**
```
Username: supplier01
Email: supplier@vendor.com
First Name: Sarah
Last Name: Chen
Role: Supplier Portal User
Department: External
Location: Los Angeles, CA
```

**Step 4: Verify Role Assignment**
1. Check users table for all created users
2. Click on "Role Matrix" tab
3. **Expected Result**: 
   - All role types display with correct permissions
   - Permission chips show appropriate access levels
   - Role hierarchy levels displayed correctly

**Step 5: Activate Users**
1. For each pending user, click edit icon
2. Change status from "Pending" to "Active"
3. Save changes
4. **Expected Result**: 
   - User status updates to active
   - Status chip color changes to green
   - User can now log in

---

### 3. Authentication Flow Testing

#### Test Procedure

**Step 1: Valid Credentials Login**
1. Logout from admin account
2. Try logging in with valid credentials:
   ```
   Username: jsmith
   Password: (generated password from email)
   ```
3. **Expected Result**: 
   - Successful login
   - Redirect to appropriate dashboard based on role
   - Navigation menu reflects user's permissions

**Step 2: Invalid Credentials Handling**
1. Attempt login with invalid credentials:
   ```
   Username: jsmith
   Password: wrongpassword
   ```
2. **Expected Result**: 
   - Login fails with clear error message
   - Account lockout after 5 failed attempts
   - Error message: "Invalid credentials. X attempts remaining."

**Step 3: Password Reset Functionality**
1. Click "Forgot Password" link
2. Enter email address: `j.smith@company.com`
3. Submit password reset request
4. **Expected Result**: 
   - Success message displayed
   - Password reset email sent
   - Email contains secure reset link

**Step 4: Password Reset Process**
1. Check email for password reset link
2. Click reset link
3. Enter new password (must meet complexity requirements)
4. Confirm new password
5. Submit form
6. **Expected Result**: 
   - Password updated successfully
   - Redirect to login page
   - Can login with new password

**Step 5: Multi-Factor Authentication Setup**
1. Login to user account
2. Navigate to Profile settings
3. Enable 2FA authentication
4. Scan QR code with authenticator app
5. Enter verification code
6. **Expected Result**: 
   - 2FA enabled successfully
   - Backup codes generated
   - Next login requires 2FA code

---

## Supplier Management Testing

### 1. Supplier CRUD Operations

#### Prerequisites
- User logged in with SUPPLY_MANAGER or SUPER_ADMIN role
- Supplier management interface accessible

#### Test Procedure

**Step 1: Navigate to Supplier Management**
1. Click "Suppliers" in main navigation
2. Select "Manage Suppliers"
3. **Expected Result**: 
   - Supplier management interface loads
   - Existing suppliers displayed in grid/table
   - "Add Supplier" button visible
   - Search and filter options available

**Step 2: Create New Supplier**
1. Click "Add Supplier" button
2. Fill in supplier creation form:
   ```
   Company Name: Acme Manufacturing Inc.
   Contact Person: David Wilson
   Email: david.wilson@acme.com
   Phone: +1-555-0200
   Address: 123 Industrial Blvd, Detroit, MI 48201
   Country: United States
   Industry: Automotive Parts
   Certification: ISO 9001:2015
   Risk Category: Medium
   Payment Terms: Net 30
   ```
3. Upload supplier documents (certifications, contracts)
4. Click "Save Supplier"
5. **Expected Result**: 
   - Supplier created successfully
   - Appears in supplier list
   - Automatic risk score calculated
   - All entered data displayed correctly

**Step 3: Edit Supplier Details**
1. Find created supplier in list
2. Click edit icon or "Edit" button
3. Modify supplier information:
   ```
   Contact Person: David Wilson Jr.
   Phone: +1-555-0201
   Risk Category: Low
   ```
4. Update certification expiry date
5. Save changes
6. **Expected Result**: 
   - Changes saved successfully
   - Updated information displayed
   - Risk score recalculated if applicable
   - Audit trail logged

**Step 4: Upload Supplier Documents**
1. From supplier edit screen, navigate to "Documents" tab
2. Upload the following document types:
   - ISO Certification (PDF)
   - Insurance Certificate (PDF)
   - Financial Statement (PDF)
   - Contract Agreement (PDF)
3. **Expected Result**: 
   - All documents uploaded successfully
   - Document thumbnails/links displayed
   - Document metadata (size, upload date) shown
   - Download functionality works

**Step 5: Delete Supplier**
1. Select a test supplier from list
2. Click delete icon or "Delete" button
3. Confirm deletion in confirmation dialog
4. **Expected Result**: 
   - Supplier removed from list
   - Confirmation message displayed
   - Related data properly handled (shipments, etc.)
   - Audit trail logged

**Step 6: Search and Filter Testing**
1. Use search box to find suppliers by:
   - Company name
   - Contact person
   - Location
   - Industry
2. Test filters:
   - Risk category
   - Certification status
   - Country/Region
   - Active/Inactive status
3. **Expected Result**: 
   - Search returns relevant results
   - Filters work individually and in combination
   - Results update in real-time
   - Clear filters option works

---

### 2. Risk Scoring and Assessment

#### Test Procedure

**Step 1: Automatic Risk Score Calculation**
1. Create new supplier with high-risk characteristics:
   ```
   Country: High-risk region
   Industry: Volatile industry
   Financial Rating: Poor
   Compliance History: Issues noted
   ```
2. **Expected Result**: 
   - Risk score automatically calculated
   - High risk color coding applied (red)
   - Risk factors clearly identified
   - Score explanation provided

**Step 2: Manual Risk Score Adjustment**
1. Select supplier with calculated risk score
2. Navigate to "Risk Assessment" section
3. Add manual risk factors:
   - Recent quality issues
   - Delivery delays
   - Communication problems
4. Adjust overall risk score manually
5. Add justification comments
6. **Expected Result**: 
   - Manual adjustments applied
   - Combined score calculated
   - Justification logged
   - Risk history maintained

**Step 3: Risk Score History Tracking**
1. View supplier risk score over time
2. Check risk score history graph
3. **Expected Result**: 
   - Historical risk scores displayed
   - Timeline shows score changes
   - Reasons for changes documented
   - Trend analysis available

**Step 4: Risk Threshold Alerts**
1. Set risk threshold alerts for high-risk suppliers
2. Configure notification settings
3. Simulate risk score increase above threshold
4. **Expected Result**: 
   - Alert notifications generated
   - Email notifications sent
   - Dashboard alerts displayed
   - Action items created

---

## Shipment Tracking Testing

### 1. Shipment Creation and Management

#### Prerequisites
- User logged in with appropriate permissions
- Suppliers configured in system
- Shipment tracking interface accessible

#### Test Procedure

**Step 1: Create New Shipment**
1. Navigate to "Shipments" section
2. Click "Create Shipment" button
3. Fill in shipment details:
   ```
   Tracking Number: 1Z999AA1234567890
   Supplier: Acme Manufacturing Inc.
   Carrier: UPS
   Origin: Detroit, MI, USA
   Destination: Chicago, IL, USA
   Expected Delivery: [Date + 3 days]
   Value: $15,000
   Weight: 500 lbs
   Description: Automotive parts shipment
   Priority: High
   ```
4. Add shipment items/contents
5. Click "Create Shipment"
6. **Expected Result**: 
   - Shipment created successfully
   - Tracking number generated if not provided
   - Initial status set to "Created"
   - All details saved correctly

**Step 2: Update Shipment Status**
1. Find created shipment in shipments list
2. Click "Update Status" or edit shipment
3. Change status to "In Transit"
4. Add location update: "Departed Origin Facility"
5. Update estimated delivery date if needed
6. **Expected Result**: 
   - Status updated successfully
   - Real-time notifications sent to stakeholders
   - Status history logged
   - Dashboard metrics updated

**Step 3: Add Tracking Events**
1. From shipment detail view, add tracking events:
   ```
   Event 1:
   - Timestamp: [Current time - 2 hours]
   - Location: Detroit, MI Facility
   - Status: Package Picked Up
   - Description: Package picked up from shipper
   
   Event 2:
   - Timestamp: [Current time - 1 hour]
   - Location: Detroit, MI Hub
   - Status: Departed Facility
   - Description: Package departed Detroit hub
   ```
2. **Expected Result**: 
   - Events added to tracking timeline
   - Map updated with current location
   - ETA recalculated if applicable
   - Notifications sent for major milestones

**Step 4: Shipment Completion Workflow**
1. Add final tracking events:
   ```
   Event: Out for Delivery
   Event: Delivered
   ```
2. Upload proof of delivery (POD) document
3. Confirm delivery details:
   - Delivery date/time
   - Recipient name
   - Any delivery notes
4. Mark shipment as "Delivered"
5. **Expected Result**: 
   - Shipment marked complete
   - Delivery confirmation sent
   - Performance metrics updated
   - POD document stored

---

### 2. Real-Time Tracking Integration

#### Test Procedure

**Step 1: Carrier API Integration Testing**
1. Create shipments with tracking numbers from different carriers:
   - UPS: 1Z999AA1234567890
   - FedEx: 9612019000000000000
   - DHL: 1234567890
2. Enable real-time tracking for each shipment
3. **Expected Result**: 
   - API connections established successfully
   - Real-time tracking data retrieved
   - Tracking events synchronized
   - Location updates displayed on map

**Step 2: Webhook and Real-Time Updates**
1. Configure webhook endpoints for tracking updates
2. Simulate tracking updates from carrier systems
3. Monitor real-time dashboard for updates
4. **Expected Result**: 
   - Webhooks received and processed
   - Dashboard updates in real-time
   - WebSocket connections stable
   - No data loss or delays

**Step 3: Exception Handling**
1. Test scenarios with tracking issues:
   - Invalid tracking numbers
   - Carrier API downtime
   - Delayed shipments
   - Lost packages
2. **Expected Result**: 
   - Graceful error handling
   - Alternative tracking methods used
   - Users notified of issues
   - Manual tracking options available

---

## Analytics and Dashboard Testing

### 1. Dashboard Functionality

#### Prerequisites
- Sample data loaded in system
- User with analytics permissions
- All dashboard components operational

#### Test Procedure

**Step 1: Executive Dashboard Access**
1. Login as user with executive permissions
2. Navigate to Executive Dashboard
3. **Expected Result**: 
   - Dashboard loads within 3 seconds
   - All KPI cards display current data
   - Charts and graphs render correctly
   - No loading errors or timeouts

**Step 2: KPI Calculations Verification**
1. Verify key metrics display:
   - Supply Chain Health Score (0-100)
   - Total Cost Impact (in millions)
   - On-Time Performance percentage
   - Active Shipments count
   - Risk Exposure Index
2. Cross-reference with manual calculations
3. **Expected Result**: 
   - All KPIs display accurate values
   - Calculations match expected results
   - Trend indicators show correct direction
   - Color coding appropriate for values

**Step 3: Interactive Chart Testing**
1. Test chart interactions:
   - Hover over data points for details
   - Click chart elements for drill-down
   - Use chart zoom and pan features
   - Filter data by time ranges
2. **Expected Result**: 
   - All interactions work smoothly
   - Tooltips display correct information
   - Drill-down navigation functions
   - Filtering updates charts properly

**Step 4: Real-Time Data Updates**
1. Monitor dashboard for automatic updates
2. Make changes to underlying data (create shipment, update supplier)
3. Observe dashboard refresh behavior
4. **Expected Result**: 
   - Dashboard updates within 30 seconds
   - New data reflected in metrics
   - Charts update smoothly
   - No page refresh required

**Step 5: Responsive Design Testing**
1. Test dashboard on different screen sizes:
   - Desktop (1920x1080)
   - Tablet (768x1024)
   - Mobile (375x667)
2. Test different browsers:
   - Chrome
   - Firefox
   - Safari
   - Edge
3. **Expected Result**: 
   - Layout adapts to all screen sizes
   - All features accessible on mobile
   - Charts remain readable
   - No horizontal scrolling required

---

### 2. Report Generation and Export

#### Test Procedure

**Step 1: Standard Report Generation**
1. Navigate to Analytics section
2. Generate standard reports:
   - Supplier Performance Report
   - Shipment Summary Report
   - Risk Assessment Report
   - Financial Impact Report
3. **Expected Result**: 
   - Reports generate within 60 seconds
   - All requested data included
   - Professional formatting applied
   - Charts and graphs included

**Step 2: Custom Report Builder**
1. Access custom report builder
2. Create custom report with:
   - Selected date range
   - Specific suppliers
   - Custom metrics
   - Filtered data
3. Save report template
4. **Expected Result**: 
   - Custom report builds successfully
   - All filters applied correctly
   - Template saved for reuse
   - Export options available

**Step 3: Data Export Testing**
1. Export reports in different formats:
   - PDF (for executive presentation)
   - Excel (for data analysis)
   - CSV (for data import)
   - PowerPoint (for presentations)
2. **Expected Result**: 
   - All exports complete successfully
   - Formatting preserved in exports
   - File sizes reasonable
   - No data corruption

**Step 4: Scheduled Report Automation**
1. Set up scheduled reports:
   - Daily operational reports
   - Weekly summary reports
   - Monthly executive reports
2. Configure email distribution lists
3. **Expected Result**: 
   - Reports generate on schedule
   - Email delivery successful
   - Report quality consistent
   - Error handling for failures

---

## Role-Based Access Control Testing

### 1. Role Permission Verification

#### Test Procedure

**Step 1: Super Admin Role Testing**
1. Login as Super Admin user
2. Verify access to all features:
   - User Management
   - System Configuration
   - All dashboard sections
   - Data export capabilities
   - Audit logs
3. **Expected Result**: 
   - Complete access to all features
   - No permission denied errors
   - All admin tools functional
   - System settings accessible

**Step 2: Supply Chain Manager Role Testing**
1. Login as Supply Chain Manager
2. Test access to:
   - Supplier management (full CRUD)
   - Shipment tracking (full access)
   - Analytics dashboard (read/write)
   - Report generation
   - Risk assessment tools
3. Test restricted access:
   - User management (should be denied)
   - System configuration (should be denied)
4. **Expected Result**: 
   - Appropriate access granted
   - Restricted features properly blocked
   - Clear error messages for denied access
   - Role-appropriate navigation menu

**Step 3: Analyst Role Testing**
1. Login as Analyst user
2. Test access to:
   - Analytics dashboard (read-only)
   - Report creation and export
   - Data visualization tools
   - Trend analysis features
3. Test restricted access:
   - Supplier modification (should be denied)
   - Shipment creation (should be denied)
   - User management (should be denied)
4. **Expected Result**: 
   - Analytics features fully functional
   - Data modification properly restricted
   - Export capabilities available
   - Read-only access respected

**Step 4: Viewer Role Testing**
1. Login as Viewer user
2. Test access to:
   - Dashboard viewing
   - Basic reports
   - Alert notifications
   - Profile management
3. Test all restricted features:
   - Data modification (should be denied)
   - Report creation (should be denied)
   - System configuration (should be denied)
4. **Expected Result**: 
   - Read-only access properly enforced
   - No modification options visible
   - Basic features functional
   - Clear limitations communicated

**Step 5: Supplier Portal User Testing**
1. Login as Supplier Portal user
2. Test supplier-specific features:
   - Supplier dashboard
   - Document upload
   - Performance metrics viewing
   - Communication tools
   - Order tracking
3. Test access restrictions:
   - Other suppliers' data (should be denied)
   - Internal company data (should be denied)
   - System administration (should be denied)
4. **Expected Result**: 
   - Supplier-specific data accessible
   - Data isolation properly enforced
   - Upload functionality works
   - Communication features functional

---

### 2. Permission Boundary Testing

#### Test Procedure

**Step 1: Cross-Role Data Access Testing**
1. Create test data with different users
2. Attempt to access data across role boundaries
3. Test API endpoints directly with different role tokens
4. **Expected Result**: 
   - Data access properly restricted
   - API endpoints respect permissions
   - No data leakage between roles
   - Audit trails capture access attempts

**Step 2: Role Escalation Prevention**
1. Attempt to modify user roles without permission
2. Test URL manipulation to access restricted areas
3. Test API calls with modified permissions
4. **Expected Result**: 
   - Role modifications properly blocked
   - URL access restrictions enforced
   - API security prevents escalation
   - Security events logged

---

## Integration Testing

### 1. API Integration Testing

#### Test Procedure

**Step 1: Authentication API Testing**
```bash
# Test user login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"identifier":"admin","password":"password123"}'

# Expected: JWT token returned
# Verify: Token contains correct user information and permissions
```

**Step 2: Supplier API Testing**
```bash
# Get all suppliers (with authentication)
curl -X GET http://localhost:8080/api/suppliers \
  -H "Authorization: Bearer [JWT_TOKEN]"

# Create new supplier
curl -X POST http://localhost:8080/api/suppliers \
  -H "Authorization: Bearer [JWT_TOKEN]" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Supplier","email":"test@supplier.com"}'

# Expected: Supplier created, returns supplier object with ID
```

**Step 3: Shipment API Testing**
```bash
# Get shipment tracking
curl -X GET http://localhost:8080/api/shipments/tracking/1Z12345E0205271688 \
  -H "Authorization: Bearer [JWT_TOKEN]"

# Update shipment status
curl -X PUT http://localhost:8080/api/shipments/1/status?status=OUT_FOR_DELIVERY \
  -H "Authorization: Bearer [JWT_TOKEN]"

# Expected: Status updated, WebSocket notification sent
```

**Step 4: Mock Carrier API Testing**
```bash
# Test DHL tracking
curl -X GET http://localhost:8080/mock-apis/carriers/dhl/track/1234567890

# Test FedEx tracking
curl -X GET http://localhost:8080/mock-apis/carriers/fedex/track/1234567890

# Test UPS tracking
curl -X GET http://localhost:8080/mock-apis/carriers/ups/track/1Z12345E0205271688

# Expected: Realistic tracking responses for each carrier
```

---

### 2. WebSocket Real-Time Testing

#### Test Procedure

**Step 1: WebSocket Connection Testing**
```javascript
// Connect to WebSocket endpoint
const socket = new SockJS('http://localhost:8080/ws/shipment-tracking');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to shipment updates
    stompClient.subscribe('/topic/shipments', function(message) {
        const update = JSON.parse(message.body);
        console.log('Shipment update:', update);
    });
    
    // Subscribe to risk alerts
    stompClient.subscribe('/topic/risk-alerts', function(message) {
        const alert = JSON.parse(message.body);
        console.log('Risk alert:', alert);
    });
});
```

**Step 2: Real-Time Update Testing**
1. Open multiple browser tabs with dashboard
2. Create/update shipments in one tab
3. Verify updates appear in other tabs immediately
4. **Expected Result**: 
   - Updates propagate within 2 seconds
   - All connected clients receive updates
   - No duplicate messages
   - Connection remains stable

---

## Performance Testing

### 1. Load Testing

#### Test Procedure

**Step 1: Database Performance**
1. Load test data:
   - 1000+ suppliers
   - 10,000+ shipments
   - 100,000+ tracking events
2. Measure dashboard load times
3. **Expected Result**: 
   - Dashboard loads in <3 seconds
   - API responses <500ms
   - Database queries optimized
   - No memory leaks

**Step 2: Concurrent User Testing**
1. Simulate 50 concurrent users
2. Execute mixed workload:
   - Dashboard viewing
   - Report generation
   - Data entry
   - Search operations
3. **Expected Result**: 
   - Response times remain stable
   - No errors under load
   - Resource utilization reasonable
   - Session management stable

---

## Security Testing

### 1. Authentication Security

#### Test Procedure

**Step 1: Password Security**
1. Test password complexity requirements
2. Attempt common password attacks:
   - Dictionary attacks
   - Brute force attempts
   - Password spraying
3. **Expected Result**: 
   - Strong passwords enforced
   - Account lockout after failed attempts
   - Rate limiting implemented
   - Security events logged

**Step 2: Session Security**
1. Test session timeout behavior
2. Attempt session hijacking
3. Test concurrent session limits
4. **Expected Result**: 
   - Sessions expire appropriately
   - Session tokens secure
   - Concurrent sessions limited
   - Session invalidation works

---

## Troubleshooting Guide

### Common Issues and Solutions

#### 1. Database Connection Issues

**Symptoms:**
- Application fails to start
- "Database connection refused" errors
- Data not loading in interface

**Troubleshooting Steps:**
```bash
# Check PostgreSQL status
docker-compose ps postgres

# View database logs
docker-compose logs postgres

# Test database connection
psql -h localhost -p 5432 -U supply_user -d smart_supply_chain

# Restart database service
docker-compose restart postgres
```

**Solution:**
- Verify database credentials in application.properties
- Ensure PostgreSQL is running and accessible
- Check firewall settings
- Verify database schema is properly initialized

#### 2. Authentication Problems

**Symptoms:**
- Login fails with valid credentials
- JWT token errors
- User sessions not persisting

**Troubleshooting Steps:**
```bash
# Verify JWT secret configuration
echo $JWT_SECRET

# Check user creation in database
psql -d smart_supply_chain -c "SELECT * FROM users WHERE username='admin';"

# Test authentication endpoint
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"test123","firstName":"Test","lastName":"User"}'
```

**Solution:**
- Verify JWT secret is configured correctly
- Check password encryption settings
- Ensure user accounts are properly activated
- Clear browser cache and cookies

#### 3. WebSocket Connection Issues

**Symptoms:**
- Real-time updates not working
- Dashboard not refreshing automatically
- WebSocket connection errors

**Troubleshooting Steps:**
```bash
# Test WebSocket endpoint directly
curl -i -N \
  -H "Connection: Upgrade" \
  -H "Upgrade: websocket" \
  -H "Sec-WebSocket-Version: 13" \
  -H "Sec-WebSocket-Key: test" \
  http://localhost:8080/ws/shipment-tracking
```

**Solution:**
- Check WebSocket configuration in backend
- Verify firewall allows WebSocket connections
- Test with different browsers
- Check proxy/load balancer WebSocket support

#### 4. Performance Issues

**Symptoms:**
- Slow dashboard loading
- API timeouts
- Memory usage high

**Troubleshooting Steps:**
```bash
# Check application logs
docker-compose logs backend

# Monitor resource usage
docker stats

# Check database performance
psql -d smart_supply_chain -c "SELECT * FROM pg_stat_activity;"
```

**Solution:**
- Optimize database queries
- Increase memory allocation
- Review caching configuration
- Consider horizontal scaling

---

### Getting Help

#### Support Channels
- **Documentation**: [GitHub Wiki](https://github.com/ommanoj88/smart-supply-chain-risk-intelligence/wiki)
- **Issues**: [GitHub Issues](https://github.com/ommanoj88/smart-supply-chain-risk-intelligence/issues)
- **Email**: support@supplychainrisk.com

#### Bug Reporting Template
When reporting issues, please include:
1. **Environment Details**: OS, browser, application version
2. **Steps to Reproduce**: Detailed steps leading to the issue
3. **Expected Result**: What should have happened
4. **Actual Result**: What actually happened
5. **Screenshots/Logs**: Visual evidence and error logs
6. **Browser Console**: Any JavaScript errors
7. **Network Requests**: Failed API calls or network issues

---

*This testing documentation should be reviewed and updated regularly as new features are added to the platform.*