# Smart Supply Chain Risk Intelligence

A comprehensive platform for managing supply chain operations, tracking shipments, and assessing risks with real-time monitoring and intelligent analytics.

## 🎯 Features

### **Complete Authentication System**
- **JWT Authentication**: Secure token-based authentication with session management
- **Firebase Integration**: Support for Google Sign-In and Firebase authentication
- **Role-Based Access Control**: ADMIN, SUPPLY_MANAGER, VIEWER, and AUDITOR roles
- **Security Features**: Account locking, failed login tracking, password encryption

### **Supplier Management**
- **150+ Realistic Suppliers**: Global supplier database with comprehensive profiles
- **Risk Assessment**: Multi-dimensional risk scoring (financial, operational, compliance, geographic)
- **Performance Tracking**: Historical performance data and KPI monitoring
- **Certification Management**: ISO and compliance certification tracking

### **Advanced Shipment Tracking**
- **2000+ Sample Shipments**: Realistic shipment data with major trade routes
- **Real-time Updates**: WebSocket-based live tracking and notifications
- **15,000+ Tracking Events**: Detailed event timeline with geographic progression
- **Risk Monitoring**: Proactive delay prediction and exception handling

### **Mock Carrier APIs**
- **DHL Express**: Realistic API responses for international express shipments
- **FedEx**: Ground and express shipping with detailed scan events
- **UPS**: Comprehensive tracking with package activities
- **Maersk Line**: Ocean freight container tracking
- **MSC**: Maritime shipping with vessel information

### **Real-time Features**
- **Live Dashboard**: Real-time metrics and KPI updates
- **WebSocket Notifications**: Instant shipment status updates
- **Risk Alerts**: Proactive notifications for high-risk scenarios
- **Performance Monitoring**: Live supplier performance tracking

## 🚀 Quick Start

### Prerequisites
- **Java 17** or higher
- **Node.js 18** or higher
- **PostgreSQL 15** or higher
- **Docker & Docker Compose** (optional, for containerized deployment)

### Database Setup

1. Create PostgreSQL database:
```bash
psql -U postgres -c "CREATE DATABASE smart_supply_chain;"
psql -U postgres -d smart_supply_chain -f database/schema.sql
```

2. Create database user:
```sql
CREATE USER supply_user WITH ENCRYPTED PASSWORD 'supply_pass';
GRANT ALL PRIVILEGES ON DATABASE smart_supply_chain TO supply_user;
```

### Backend Setup

1. Navigate to backend directory:
```bash
cd smart-supply-chain-backend
```

2. Configure database connection in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smart_supply_chain
spring.datasource.username=supply_user
spring.datasource.password=supply_pass
```

3. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd smart-supply-chain-frontend
```

2. Install dependencies and start:
```bash
npm install
npm start
```

Frontend will start on `http://localhost:3000`

### Docker Deployment

1. Copy environment file:
```bash
cp .env.example .env
# Edit .env with your configuration
```

2. Start with Docker Compose:
```bash
# Development environment
docker-compose up -d

# Production environment with nginx proxy
docker-compose --profile production up -d
```

## 🔧 Configuration

### Environment Variables

Copy `.env.example` to `.env` and configure:

```bash
# Database
DB_PASSWORD=your-secure-password

# JWT
JWT_SECRET=your-super-secret-jwt-key

# External APIs (optional)
DHL_API_KEY=your-dhl-api-key
FEDEX_API_KEY=your-fedex-api-key
```

### Security Configuration

The application supports dual authentication:
- **JWT**: For username/password authentication
- **Firebase**: For Google Sign-In and OAuth flows

## 📊 Sample Data

### Automatic Data Seeding

Generate realistic sample data using the admin endpoint:

```bash
# Login as admin and call:
POST /api/admin/seed-data
```

This creates:
- **150+ Suppliers**: Global manufacturers across 6+ countries
- **2000+ Shipments**: 18 months of shipping data
- **15,000+ Events**: Detailed tracking timeline
- **Sample Users**: Admin, managers, viewers, and auditors

### Geographic Distribution

- **North America**: 45 suppliers (30%)
- **Asia**: 52 suppliers (35%) 
- **Europe**: 38 suppliers (25%)
- **Rest of World**: 15 suppliers (10%)

### Trade Routes

- **Asia → North America**: 35% of shipments
- **Europe → North America**: 25% of shipments
- **Asia → Europe**: 20% of shipments
- **Intra-regional**: 20% of shipments

## 🌐 API Documentation

### Interactive API Documentation

Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

### Key Endpoints

#### Authentication
```bash
# JWT Login
POST /auth/login
{
  "identifier": "admin",
  "password": "password123"
}

# Register new user
POST /auth/register
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Suppliers
```bash
# Get all suppliers
GET /api/suppliers?page=0&size=20

# Get supplier by ID
GET /api/suppliers/1

# Create supplier
POST /api/suppliers
```

#### Shipments
```bash
# Get all shipments
GET /api/shipments?page=0&size=20

# Track shipment
GET /api/shipments/tracking/1Z12345E0205271688

# Update shipment status (with real-time broadcast)
PUT /api/shipments/1/status?status=OUT_FOR_DELIVERY
```

#### Mock Carrier APIs
```bash
# DHL tracking
GET /mock-apis/carriers/dhl/track/1234567890

# FedEx tracking
GET /mock-apis/carriers/fedex/track/1234567890

# UPS tracking
GET /mock-apis/carriers/ups/track/1Z12345E0205271688
```

## 📱 Real-time Features

### WebSocket Connections

Connect to real-time updates:

```javascript
// Connect to WebSocket
const socket = new SockJS('http://localhost:8080/ws/shipment-tracking');
const stompClient = Stomp.over(socket);

// Subscribe to shipment updates
stompClient.subscribe('/topic/shipments', (message) => {
  const update = JSON.parse(message.body);
  console.log('Shipment update:', update);
});

// Subscribe to risk alerts
stompClient.subscribe('/topic/risk-alerts', (message) => {
  const alert = JSON.parse(message.body);
  console.log('Risk alert:', alert);
});
```

### Real-time Topics

- `/topic/shipments` - All shipment updates
- `/topic/shipments/{id}` - Specific shipment updates
- `/topic/tracking-events` - New tracking events
- `/topic/risk-alerts` - Risk notifications
- `/topic/dashboard` - Dashboard metrics
- `/topic/suppliers` - Supplier updates

## 🏥 Health Monitoring

### Health Check Endpoints

```bash
# Application health
GET /health

# Database health
GET /health/database

# Readiness probe
GET /health/ready

# Liveness probe
GET /health/live
```

### Monitoring with Docker

Health checks are built into Docker containers:

```bash
# Check container health
docker ps

# View health check logs
docker inspect supply-chain-backend
```

## 🧪 Testing

### Backend Testing

```bash
cd smart-supply-chain-backend
mvn test
```

### Frontend Testing

```bash
cd smart-supply-chain-frontend
npm test
```

### Integration Testing

```bash
# Test with real database
mvn test -Dspring.profiles.active=integration

# Test with mock carrier APIs
curl http://localhost:8080/mock-apis/carriers/dhl/track/TEST123
```

## 🔒 Security Features

### Authentication Security
- **BCrypt Password Hashing**: Secure password storage
- **JWT Token Validation**: Stateless authentication
- **Session Management**: Track and revoke user sessions
- **Account Locking**: Prevent brute force attacks

### API Security
- **CORS Configuration**: Cross-origin request handling
- **Role-based Access**: Endpoint-level authorization
- **Input Validation**: Request payload validation
- **SQL Injection Prevention**: Parameterized queries

### Container Security
- **Non-root User**: Containers run as app user
- **Minimal Base Images**: Alpine Linux for smaller attack surface
- **Security Headers**: Nginx security headers
- **Health Checks**: Container health monitoring

## 📈 Performance & Scalability

### Database Optimization
- **Indexed Queries**: Optimized database indexes
- **Connection Pooling**: HikariCP for efficient connections
- **Query Optimization**: JPA query tuning

### Caching Strategy
- **Redis Integration**: Session and data caching
- **HTTP Caching**: Static asset caching
- **Database Caching**: Query result caching

### Monitoring & Observability
- **Application Metrics**: Custom business metrics
- **Health Checks**: Kubernetes-ready health endpoints
- **Logging**: Structured JSON logging
- **Tracing**: Request correlation IDs

## 🚢 Deployment

### Development Environment

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

### Production Environment

```bash
# Start with production profile
docker-compose --profile production up -d

# Update configuration
docker-compose restart backend

# Scale services
docker-compose up -d --scale backend=2
```

### Environment-specific Configuration

- **Development**: Debug logging, SQL logging enabled
- **Production**: Optimized for performance, minimal logging
- **Testing**: In-memory database, mock services

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

### Troubleshooting

**Database Connection Issues:**
```bash
# Check PostgreSQL status
docker-compose ps postgres

# View database logs
docker-compose logs postgres
```

**Authentication Problems:**
```bash
# Verify JWT secret
echo $JWT_SECRET

# Check user creation
curl -X POST http://localhost:8080/auth/register -H "Content-Type: application/json" -d '{"username":"test","email":"test@example.com","password":"test123","firstName":"Test","lastName":"User"}'
```

**WebSocket Connection Issues:**
```bash
# Test WebSocket endpoint
curl -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Version: 13" -H "Sec-WebSocket-Key: test" http://localhost:8080/ws/shipment-tracking
```

### Getting Help

- 📧 Email: support@supplychainrisk.com
- 💬 Issues: [GitHub Issues](https://github.com/ommanoj88/smart-supply-chain-risk-intelligence/issues)
- 📖 Documentation: [Wiki](https://github.com/ommanoj88/smart-supply-chain-risk-intelligence/wiki)