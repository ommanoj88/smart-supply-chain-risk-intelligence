# Quick Start Testing Guide
# Smart Supply Chain Risk Intelligence Platform

## 🚀 Quick Setup and Testing (30 minutes)

This guide will help you quickly test all major functionalities of the Smart Supply Chain Risk Intelligence Platform.

---

## Prerequisites Checklist

Before starting, ensure you have:
- [ ] Java 17+ installed
- [ ] Node.js 18+ installed  
- [ ] PostgreSQL 15+ running
- [ ] Docker & Docker Compose (optional)
- [ ] Git repository cloned

---

## 1. Environment Setup (5 minutes)

### Option A: Docker Setup (Recommended)
```bash
# Clone and start services
git clone https://github.com/ommanoj88/smart-supply-chain-risk-intelligence.git
cd smart-supply-chain-risk-intelligence

# Copy environment file
cp .env.example .env

# Start all services
docker-compose up -d

# Wait for services to start (check with)
docker-compose ps
```

### Option B: Manual Setup
```bash
# Start PostgreSQL database
createdb smart_supply_chain
psql -d smart_supply_chain -f database/schema.sql

# Start Backend
cd smart-supply-chain-backend
mvn spring-boot:run

# Start Frontend (new terminal)
cd smart-supply-chain-frontend  
npm install
npm start
```

### Verify Setup
- ✅ Backend: http://localhost:8080/health
- ✅ Frontend: http://localhost:3000
- ✅ Database: `psql -d smart_supply_chain -c "SELECT 1;"`

---

## 2. Authentication Testing (5 minutes)

### Create Super Admin Account
1. **Navigate to**: http://localhost:3000
2. **Click**: "Register" (if first time) or use existing admin account
3. **Create admin user**:
   ```
   Username: admin
   Email: admin@company.com
   Password: AdminPassword123!
   First Name: System
   Last Name: Administrator
   ```
4. **Verify**: Login successful, dashboard loads

### Test Login/Logout
1. **Logout** and **login** again with admin credentials
2. **Expected**: Smooth authentication flow
3. **Check**: User profile shows SUPER_ADMIN role

✅ **Result**: Authentication system working

---

## 3. User Management Testing (5 minutes)

### Access User Management
1. **Navigate to**: `/admin/users` or click "User Management"
2. **Verify**: User management interface loads with current admin user

### Create Test Users
**Quick user creation** (use the "Add User" button):

```
Supply Manager:
- Username: manager01
- Email: manager@company.com  
- Name: John Manager
- Role: Supply Chain Manager

Analyst:
- Username: analyst01
- Email: analyst@company.com
- Name: Maria Analyst  
- Role: Supply Chain Analyst

Viewer:
- Username: viewer01
- Email: viewer@company.com
- Name: Alex Viewer
- Role: Viewer
```

### Test Role Matrix
1. **Click**: "Role Matrix" tab
2. **Verify**: All 6 role types display with permissions
3. **Check**: Permission chips show correctly

✅ **Result**: User management functional, RBAC system working

---

## 4. Dashboard & Analytics Testing (5 minutes)

### Executive Dashboard
1. **Navigate to**: Executive Dashboard (should be default)
2. **Verify loading**:
   - [ ] Supply Chain Health Score displays (0-100)
   - [ ] KPI cards show metrics
   - [ ] Charts render without errors
   - [ ] Real-time status indicator shows "Live"

### Test Interactivity  
1. **Click**: Refresh button (↻) 
2. **Hover**: Over chart elements
3. **Check**: Data updates and tooltips work

### Analytics Dashboard
1. **Navigate to**: Analytics Dashboard
2. **Verify**: Advanced charts and visualizations load
3. **Test**: Any interactive features (zoom, filters)

✅ **Result**: Dashboard system working, enterprise-grade visualizations functional

---

## 5. Supplier Management Testing (5 minutes)

### Create Test Supplier
1. **Navigate to**: Suppliers → Manage Suppliers
2. **Click**: "Add Supplier" 
3. **Fill in**:
   ```
   Company: Acme Manufacturing
   Contact: David Wilson
   Email: david@acme.com
   Phone: +1-555-0200
   Country: United States
   Industry: Automotive
   Risk Level: Medium
   ```
4. **Save** and **verify** supplier appears in list

### Test Supplier Features
1. **Edit** the created supplier
2. **Upload** a test document (any PDF)
3. **Check** risk scoring updates
4. **Search** for supplier by name

✅ **Result**: Supplier management functional

---

## 6. Shipment Tracking Testing (5 minutes)

### Create Test Shipment
1. **Navigate to**: Shipments
2. **Click**: "Create Shipment"
3. **Fill in**:
   ```
   Tracking Number: TEST123456789
   Supplier: Acme Manufacturing  
   Carrier: UPS
   Origin: Detroit, MI
   Destination: Chicago, IL
   Value: $15,000
   Priority: High
   ```
4. **Save** shipment

### Test Tracking Features
1. **View** shipment details
2. **Update** status to "In Transit"
3. **Add** tracking event: "Package picked up"
4. **Check** status timeline updates

✅ **Result**: Shipment tracking functional

---

## 7. Role-Based Access Testing (3 minutes)

### Test Different User Roles
1. **Logout** from admin account
2. **Login** as manager01 (Supply Manager)
   - **Check**: Can access suppliers and shipments
   - **Verify**: Cannot access user management
3. **Login** as viewer01 (Viewer)
   - **Check**: Read-only dashboard access
   - **Verify**: No create/edit options visible

✅ **Result**: Role-based access control working

---

## 8. Real-Time Features Testing (2 minutes)

### Test WebSocket Connectivity
1. **Open** dashboard in two browser tabs
2. **In tab 1**: Create or update a shipment
3. **In tab 2**: Watch for real-time updates
4. **Expected**: Updates appear automatically

### Check Live Status
1. **Verify**: "Live" indicator in dashboard header
2. **Check**: Last updated timestamp refreshes

✅ **Result**: Real-time features working

---

## ✅ Quick Test Results Summary

| Feature | Status | Notes |
|---------|---------|-------|
| Authentication | ✅ Pass | |
| User Management | ✅ Pass | |
| Role-Based Access | ✅ Pass | |
| Executive Dashboard | ✅ Pass | |
| Analytics Dashboard | ✅ Pass | |
| Supplier Management | ✅ Pass | |
| Shipment Tracking | ✅ Pass | |
| Real-Time Updates | ✅ Pass | |

---

## 🐛 Common Quick Fixes

### If Dashboard Won't Load
```bash
# Check backend is running
curl http://localhost:8080/health

# Check frontend is running  
curl http://localhost:3000

# Restart services
docker-compose restart
```

### If Login Fails
```bash
# Check user in database
docker-compose exec postgres psql -U supply_user smart_supply_chain -c "SELECT username, role FROM users;"

# Reset admin password (if needed)
# Use password reset feature or create new admin user
```

### If Charts Don't Render
1. **Check browser console** for JavaScript errors
2. **Clear browser cache** and reload
3. **Try different browser** (Chrome recommended)

---

## 🎯 Next Steps

After quick testing passes:

1. **Load Sample Data**: Use `/api/admin/seed-data` endpoint to generate realistic test data
2. **Full Testing**: Follow the [Comprehensive Testing Guide](./comprehensive-testing-guide.md)
3. **Performance Testing**: Test with larger datasets
4. **Integration Testing**: Test external API integrations
5. **Security Testing**: Test authentication and authorization edge cases

---

## 📞 Quick Support

**Immediate Issues:**
- Check logs: `docker-compose logs backend` / `docker-compose logs frontend`
- Browser console errors for frontend issues
- Database connection: `docker-compose ps postgres`

**Still Need Help?**
- [GitHub Issues](https://github.com/ommanoj88/smart-supply-chain-risk-intelligence/issues)
- [Comprehensive Testing Guide](./comprehensive-testing-guide.md)
- Email: support@supplychainrisk.com

---

*Total Time: ~30 minutes for complete quick testing of all major features*