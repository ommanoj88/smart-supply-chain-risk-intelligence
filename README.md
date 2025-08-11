# Smart Supply Chain Risk Intelligence

A comprehensive Firebase Authentication system for the Smart Supply Chain Risk Intelligence platform with Spring Boot backend and React frontend.

## Features

- **Firebase Authentication**: Secure user authentication with Google Sign-In
- **Role-Based Access Control**: Admin, Supply Manager, and Viewer roles
- **Spring Boot Backend**: RESTful API with JWT token verification
- **React Frontend**: Modern React 18 application with Material-UI
- **PostgreSQL Database**: User management with automatic role assignment
- **Protected Routes**: Route guards for authenticated users
- **Real-time Authentication State**: Persistent authentication across page refreshes

## Project Structure

```
smart-supply-chain-risk-intelligence/
├── smart-supply-chain-backend/     # Spring Boot backend
├── smart-supply-chain-frontend/    # React frontend
├── database/                       # Database schemas
└── README.md
```

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher
- Firebase Project with Authentication enabled

## Setup Instructions

### 1. Database Setup

1. Install and start PostgreSQL
2. Create a database named `smart_supply_chain`
3. Run the database schema:

```bash
psql -U postgres -d smart_supply_chain -f database/schema.sql
```

### 2. Firebase Configuration

1. Create a Firebase project at [https://console.firebase.google.com/](https://console.firebase.google.com/)
2. Enable Authentication with Google provider
3. Generate a service account key:
   - Go to Project Settings > Service Accounts
   - Click "Generate new private key"
   - Save the JSON file as `firebase-service-account.json`
4. Copy the Firebase config for your web app

### 3. Backend Setup

1. Navigate to the backend directory:
```bash
cd smart-supply-chain-backend
```

2. Replace the placeholder Firebase service account file:
```bash
# Replace src/main/resources/firebase-service-account.json with your actual Firebase service account key
```

3. Update database configuration in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smart_supply_chain
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
```

4. Build and run the backend:
```bash
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Frontend Setup

1. Navigate to the frontend directory:
```bash
cd smart-supply-chain-frontend
```

2. Update Firebase configuration in `src/services/firebase.js`:
```javascript
const firebaseConfig = {
  apiKey: "your-api-key",
  authDomain: "your-project-id.firebaseapp.com",
  projectId: "your-project-id",
  storageBucket: "your-project-id.appspot.com",
  messagingSenderId: "your-sender-id",
  appId: "your-app-id"
};
```

3. Install dependencies and start the development server:
```bash
npm install
npm start
```

The frontend will start on `http://localhost:3000`

## User Roles

- **VIEWER**: Can view supply chain data and reports
- **SUPPLY_MANAGER**: Can manage supply chain operations and create reports
- **ADMIN**: Full access including user management

## API Endpoints

### Authentication Endpoints

- `POST /auth/verify` - Verify Firebase token and get/create user
- `GET /auth/user` - Get current authenticated user information
- `PUT /auth/user/role` - Update user role (Admin only)

### Protected Routes

- `/` - Dashboard (requires authentication)
- `/profile` - User profile (requires authentication)
- `/admin` - Admin panel (requires ADMIN role)
- `/supply-manager` - Supply manager dashboard (requires SUPPLY_MANAGER or ADMIN role)

## Development

### Backend Development

The backend uses:
- Spring Boot 3.1.5
- Spring Security with custom Firebase authentication filter
- Spring Data JPA with PostgreSQL
- Firebase Admin SDK for token verification

### Frontend Development

The frontend uses:
- React 18
- Firebase JavaScript SDK
- Material-UI for components
- React Router for navigation
- Axios for API calls

### Building for Production

**Backend:**
```bash
cd smart-supply-chain-backend
mvn clean package
java -jar target/smart-supply-chain-backend-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd smart-supply-chain-frontend
npm run build
# Serve the build folder with your preferred static file server
```

## Security Features

- Firebase token verification on every API call
- Role-based access control with Spring Security
- Secure token storage in frontend
- CORS configuration for frontend communication
- Protected routes with authentication guards

## Testing

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

## Environment Variables

### Backend (.env or application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/smart_supply_chain
spring.datasource.username=postgres
spring.datasource.password=password
firebase.config.path=classpath:firebase-service-account.json
cors.allowed.origins=http://localhost:3000
```

### Frontend (.env)
```
REACT_APP_API_URL=http://localhost:8080
```

## Troubleshooting

### Common Issues

1. **Firebase Authentication Error**: Ensure your Firebase service account key is correctly placed and the project ID matches
2. **Database Connection Error**: Check PostgreSQL is running and credentials are correct
3. **CORS Error**: Verify the frontend URL is added to CORS allowed origins in the backend
4. **Token Verification Failed**: Ensure the Firebase project configuration matches between frontend and backend

### Logs

Backend logs are available in the console with DEBUG level enabled for authentication components.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.