# Smart Supply Chain Risk Intelligence Backend

A Spring Boot backend application with Firebase authentication integration for the Smart Supply Chain Risk Intelligence platform.

## Features

- **Firebase Authentication**: Complete integration with Firebase Auth for secure user authentication
- **Role-based Access Control**: Three user roles (ADMIN, SUPPLY_MANAGER, VIEWER) with different permissions
- **RESTful API**: Well-documented REST endpoints for user management and authentication
- **Database Integration**: JPA with Hibernate for data persistence (H2 for development, PostgreSQL/MySQL for production)
- **Error Handling**: Comprehensive global exception handling with standardized API responses
- **Security**: Spring Security configuration with JWT token validation and CORS support
- **Documentation**: Extensive JavaDoc comments and API documentation

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security**
- **Spring Data JPA**
- **Firebase Admin SDK 9.2.0**
- **H2 Database** (development)
- **PostgreSQL/MySQL** (production)
- **Maven** (dependency management)

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Firebase project with service account key

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd smart-supply-chain-risk-intelligence/backend
```

2. Configure Firebase:
   - Place your Firebase service account key file in `src/main/resources/service-account-key.json`
   - Update the `firebase.project-id` in `application.yml`

3. Build the application:
```bash
mvn clean compile
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

### Environment Variables

For production deployment, set the following environment variables:

```bash
# Database Configuration
DATABASE_URL=jdbc:postgresql://localhost:5432/supply_chain_risk
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password

# Firebase Configuration
FIREBASE_PROJECT_ID=your-firebase-project-id

# Server Configuration
SERVER_PORT=8080
```

## API Endpoints

### Authentication Endpoints

- `POST /api/auth/login` - Authenticate user with Firebase token
- `GET /api/auth/verify` - Verify current authentication status
- `POST /api/auth/logout` - Logout current user
- `POST /api/auth/refresh` - Refresh authentication token
- `GET /api/auth/profile` - Get current user profile
- `PUT /api/auth/profile` - Update current user profile

### User Management Endpoints (Admin/Supply Manager)

- `GET /api/users` - Get all users (paginated)
- `POST /api/users` - Create new user (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)
- `PUT /api/users/{id}/role` - Update user role (Admin only)
- `PUT /api/users/{id}/status` - Update user status (Admin only)
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/search/name?name=` - Search users by name
- `GET /api/users/search/email?email=` - Search users by email
- `GET /api/users/statistics` - Get user statistics
- `GET /api/users/recent?days=` - Get recently created users
- `GET /api/users/count/active` - Get active user count

### Public Endpoints

- `GET /api/health` - Health check endpoint
- `GET /h2-console` - H2 database console (development only)

## User Roles

### ADMIN
- Full system access
- User management (create, update, delete, change roles)
- Supply chain data management
- Risk assessment management
- Analytics and reporting
- System configuration

### SUPPLY_MANAGER
- Supply chain data management
- Risk assessment management
- Analytics and reporting
- Limited user management (view, update profiles)

### VIEWER
- Read-only access to approved data
- Basic analytics viewing
- Profile management (own profile only)

## Configuration Profiles

### Development (default)
- Uses H2 in-memory database
- Detailed logging enabled
- H2 console accessible at `/h2-console`
- CORS enabled for local development

### Production
- Uses PostgreSQL/MySQL database
- Optimized logging levels
- Security hardened
- Environment-based configuration

## Security Features

- Firebase JWT token validation
- Role-based endpoint protection
- CORS configuration for frontend integration
- Comprehensive input validation
- Secure error responses (no sensitive data exposure)
- Session management (stateless)

## Database Schema

### Users Table
- `id` (Primary Key)
- `firebase_uid` (Unique, Firebase UID)
- `email` (Unique, User email)
- `name` (User full name)
- `role` (User role enum)
- `active` (Account status)
- `created_at` (Creation timestamp)
- `updated_at` (Last update timestamp)

## Error Handling

The application uses standardized API responses with consistent error formatting:

```json
{
  "status": 400,
  "message": "Validation failed",
  "error": {
    "errorCode": "VALIDATION_FAILED",
    "fieldErrors": {
      "email": "Email is required"
    }
  },
  "timestamp": "2024-01-01 12:00:00"
}
```

## Logging

Comprehensive logging is configured with different levels:
- `DEBUG`: Development debugging information
- `INFO`: General application flow
- `WARN`: Warning conditions
- `ERROR`: Error conditions

Logs include request/response details, authentication events, and business logic operations.

## Testing

Run tests with:
```bash
mvn test
```

## Build and Deploy

### Local Build
```bash
mvn clean package
```

### Docker Build (if Dockerfile is added)
```bash
docker build -t supply-chain-backend .
docker run -p 8080:8080 supply-chain-backend
```

## Contributing

1. Follow Java coding standards
2. Add comprehensive JavaDoc comments
3. Include unit tests for new features
4. Update API documentation
5. Test with different user roles

## License

This project is licensed under the MIT License - see the LICENSE file for details.