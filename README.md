# Smart Supply Chain Risk Intelligence Platform

A modern web application built with Spring Boot backend and React frontend, featuring Firebase authentication and role-based access control.

## Features

- 🔐 **Firebase Authentication** with Google Sign-In
- 👥 **Role-Based Access Control** (Admin, Supply Manager, Viewer)
- 🌐 **Modern React Frontend** with responsive design
- 🚀 **Spring Boot Backend** with REST APIs
- 📊 **H2 Database** for quick development setup
- 🛡️ **CORS Configuration** for local development

## Architecture

```
├── Backend (Spring Boot)
│   ├── Firebase Authentication Integration
│   ├── User Management with JPA/Hibernate
│   ├── Role-Based Access Control
│   └── RESTful APIs
└── Frontend (React)
    ├── Firebase Web SDK
    ├── Protected Routes
    ├── Role-Based UI Components
    └── Responsive Design
```

## User Roles

- **Admin**: Full system access, user management, system configuration
- **Supply Manager**: Supply chain management, risk assessment, analytics
- **Viewer**: Read-only access to dashboard and basic reports

## Prerequisites

- Java 17+
- Node.js 16+
- Firebase Project Account
- Maven 3.6+

## Backend Setup

### 1. Firebase Service Account Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: `rentvat`
3. Go to Project Settings → Service Accounts
4. Click "Generate new private key"
5. Save the file as `service-account-key.json` in the project root

### 2. Environment Configuration

Create a `.env` file in the root directory (copy from `.env.backend`):

```env
FIREBASE_PROJECT_ID=rentvat
FIREBASE_CREDENTIALS_PATH=./service-account-key.json
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://127.0.0.1:3000
```

### 3. Run Backend

```bash
# Compile and run
mvn clean spring-boot:run

# The backend will start on http://localhost:8080
```

### 4. Database Access

- H2 Console: http://localhost:8080/api/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Frontend Setup

### 1. Install Dependencies

```bash
cd frontend
npm install
```

### 2. Environment Configuration

The frontend environment is already configured in `frontend/.env`:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_FIREBASE_API_KEY=AIzaSyCZAd5WmJE7SrUYj4yNZDG5sltaEnrUQgk
REACT_APP_FIREBASE_AUTH_DOMAIN=rentvat.firebaseapp.com
REACT_APP_FIREBASE_PROJECT_ID=rentvat
```

### 3. Run Frontend

```bash
cd frontend
npm start

# The frontend will start on http://localhost:3000
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - Authenticate user with Firebase token
- `GET /api/auth/profile` - Get current user profile
- `GET /api/auth/users` - Get all users (Admin only)
- `PUT /api/auth/users/{uid}/role` - Update user role (Admin only)

### Health Check
- `GET /api/health` - System health status

## Development Workflow

1. **Start Backend**: `mvn spring-boot:run` (Port 8080)
2. **Start Frontend**: `cd frontend && npm start` (Port 3000)
3. **Access Application**: http://localhost:3000
4. **Database Console**: http://localhost:8080/api/h2-console

## Authentication Flow

1. User clicks "Continue with Google" on login page
2. Firebase handles Google OAuth authentication
3. Frontend receives Firebase ID token
4. Frontend sends token to backend `/api/auth/login`
5. Backend verifies token with Firebase Admin SDK
6. Backend creates/updates user in database
7. User is redirected to dashboard with role-based content

## Troubleshooting

### Backend Issues

**Firebase initialization failed:**
- Ensure `service-account-key.json` exists in project root
- Verify Firebase project ID is correct
- Check service account has proper permissions

**Database connection issues:**
- H2 is in-memory, data resets on restart
- Access H2 console at `/h2-console` for debugging

### Frontend Issues

**Authentication not working:**
- Check Firebase config in `services/firebase.js`
- Verify CORS settings allow frontend domain
- Check browser console for Firebase errors

**API calls failing:**
- Ensure backend is running on port 8080
- Check network tab for CORS errors
- Verify Firebase token is being sent

## Security Notes

- Firebase service account key should never be committed to version control
- CORS is configured for local development only
- All API endpoints except auth and health require authentication
- Role-based access is enforced both in frontend and backend

## Next Steps

1. Configure production Firebase settings
2. Set up production database (PostgreSQL/MySQL)
3. Implement supply chain specific features
4. Add comprehensive testing
5. Set up CI/CD pipeline
6. Configure production CORS and security settings

## Contributing

1. Create feature branch from main
2. Implement changes with tests
3. Ensure both frontend and backend work
4. Create pull request with detailed description