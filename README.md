# Smart Supply Chain Risk Intelligence Platform

A comprehensive Smart Supply Chain Risk Intelligence SaaS platform built with Spring Boot backend and React frontend. This application helps manufacturing, retail, and logistics companies monitor, predict, and mitigate supply chain disruptions.

## 🚀 Features

### Backend (Spring Boot)
- **Authentication & Security**: Firebase JWT authentication with role-based access control
- **Supplier Management**: Complete CRUD operations with automated risk scoring algorithms
- **Shipment Tracking**: Real-time shipment monitoring with status updates
- **Risk Prediction**: AI-powered risk assessment and prediction system
- **Alerts & Notifications**: Email notifications and alert management system
- **Database Support**: H2 (development) and PostgreSQL (production)

### Frontend (React + TypeScript)
- **Modern React**: Built with TypeScript, Vite, and modern React patterns
- **Firebase Integration**: Google Sign-In authentication
- **Responsive Design**: Mobile-friendly interface with gradient styling
- **Real-time Dashboard**: KPI cards and interactive data visualization
- **API Integration**: Seamless communication with backend services

## 🏗️ Architecture

```
Frontend (React/TypeScript)  ←→  Backend (Spring Boot)  ←→  Database (H2/PostgreSQL)
     ↓                              ↓                        ↓
Firebase Auth                   Spring Security          JPA/Hibernate
Material Design                RESTful APIs             Entity Management
Responsive UI                   Risk Algorithms          Data Persistence
```

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Security**: Spring Security + Firebase Admin SDK
- **Database**: H2 (dev), PostgreSQL (prod)
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **Authentication**: Firebase Web SDK
- **HTTP Client**: Axios
- **Styling**: CSS3 with gradients and modern design

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.6+

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend will start on `http://localhost:8080/api`

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

Frontend will start on `http://localhost:5173`

## 🔧 Configuration

### Backend Configuration
The application uses `application.yml` with profiles:
- **dev**: H2 in-memory database
- **prod**: PostgreSQL with environment variables

### Environment Variables
```bash
# Firebase Configuration
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_PRIVATE_KEY=your-private-key
FIREBASE_CLIENT_EMAIL=your-client-email

# Database (Production)
DATABASE_URL=jdbc:postgresql://localhost:5432/riskintelligence
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-password

# Email Configuration
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### Frontend Configuration
Create `.env` file in frontend directory:
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```

## 📊 Database Schema

### Core Entities
- **Users**: Firebase UID, email, name, role (ADMIN/SUPPLY_MANAGER/VIEWER)
- **Suppliers**: Name, location, contact info, risk score (0-10)
- **Shipments**: Supplier reference, status, origin/destination, ETA
- **Risk Predictions**: ML-based risk assessment with probability scores
- **Alerts**: User notifications with severity levels

### Relationships
- Users ← Alerts (One-to-Many)
- Suppliers ← Shipments (One-to-Many)
- Shipments ← Risk Predictions (One-to-Many)

## 🔐 Security Features

- **Firebase JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: ADMIN, SUPPLY_MANAGER, VIEWER roles
- **CORS Configuration**: Secure cross-origin requests
- **Input Validation**: Comprehensive data validation
- **SQL Injection Protection**: JPA/Hibernate protection

## 📈 Risk Intelligence Features

### Risk Scoring Algorithm
The platform uses a sophisticated risk scoring system considering:
- **Supplier History**: Past performance and reliability metrics
- **Geographic Factors**: Location-based risk assessment
- **Environmental Conditions**: Weather and political stability
- **Time Factors**: Delivery schedules and seasonal variations

### Prediction Engine
Mock AI/ML service that provides:
- **Risk Level Classification**: LOW, MEDIUM, HIGH, CRITICAL
- **Probability Scores**: Quantified risk percentages
- **Factor Analysis**: Detailed breakdown of risk contributors

## 🎨 UI Features

- **Modern Design**: Gradient backgrounds and card-based layout
- **Responsive**: Mobile-first responsive design
- **Interactive Dashboard**: Real-time KPI monitoring
- **Color-Coded Risk**: Visual risk level indicators
- **Status Tracking**: Dynamic shipment status updates

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/verify` - Verify Firebase token
- `GET /api/auth/me` - Get current user info

### Suppliers
- `GET /api/suppliers` - List all suppliers
- `GET /api/suppliers/high-risk` - Get high-risk suppliers
- `POST /api/suppliers` - Create new supplier
- `PUT /api/suppliers/{id}` - Update supplier

### Shipments
- `GET /api/shipments` - List all shipments
- `GET /api/shipments/delayed` - Get delayed shipments
- `POST /api/shipments` - Create new shipment
- `PUT /api/shipments/{id}/status` - Update shipment status

### Risk Predictions
- `GET /api/risk-predictions/high-risk` - Get high-risk predictions
- `POST /api/risk-predictions/generate/{shipmentId}` - Generate prediction

### Alerts
- `GET /api/alerts/unacknowledged` - Get unread alerts
- `POST /api/alerts` - Create new alert
- `PUT /api/alerts/{id}/acknowledge` - Mark alert as read

## 🏢 Production Deployment

### Docker Support
```bash
# Build backend
docker build -t supply-chain-backend ./backend

# Build frontend
docker build -t supply-chain-frontend ./frontend
```

### Environment Setup
1. Set up PostgreSQL database
2. Configure Firebase project
3. Set environment variables
4. Deploy with proper SSL certificates

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot team for excellent framework
- Firebase team for authentication services
- React team for frontend framework
- All open-source contributors