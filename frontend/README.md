# Frontend - Smart Supply Chain Risk Intelligence

React frontend application with Firebase authentication and role-based dashboard.

## Features

- Firebase Google Authentication
- Protected Routes
- Role-Based Dashboard
- Responsive Design
- API Integration with Backend

## Project Structure

```
src/
├── components/
│   ├── Auth/
│   │   ├── Login.jsx          # Google Sign-In page
│   │   └── ProtectedRoute.jsx # Route protection
│   ├── Dashboard/
│   │   ├── Dashboard.jsx      # Main dashboard
│   │   └── UserProfile.jsx    # User profile display
│   └── Layout/
│       ├── Header.jsx         # App header with user info
│       └── Navigation.jsx     # Role-based navigation
├── services/
│   ├── firebase.js           # Firebase configuration
│   └── api.js               # API service with Axios
├── context/
│   └── AuthContext.jsx      # Authentication context
├── App.jsx                  # Main app component
├── App.css                  # Global styles
└── index.js                # App entry point
```

## Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Environment Configuration

Update `.env` file with your Firebase configuration:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_FIREBASE_API_KEY=your_api_key
REACT_APP_FIREBASE_AUTH_DOMAIN=your_domain
REACT_APP_FIREBASE_PROJECT_ID=your_project_id
```

### 3. Start Development Server

```bash
npm start
```

The app will open at http://localhost:3000

## Authentication Flow

1. User visits app and is redirected to login if not authenticated
2. Login page displays Google Sign-In button
3. Firebase handles Google OAuth flow
4. On successful auth, user is logged into backend
5. User is redirected to dashboard with role-based content

## Role-Based Features

### Viewer
- Basic dashboard overview
- User profile access
- Read-only permissions

### Supply Manager
- Supply chain management sections
- Risk assessment tools
- Analytics access

### Admin
- All Supply Manager features
- User management
- System settings

## API Integration

The app uses Axios with automatic Firebase token attachment:

```javascript
// API calls automatically include Firebase token
const response = await authAPI.getProfile();
```

## Components

### AuthContext
Manages authentication state and provides:
- `user` - Firebase user object
- `userProfile` - Backend user profile
- `login()` - Google sign-in function
- `logout()` - Sign out function
- `isAuthenticated` - Boolean auth status
- Role helpers: `isAdmin`, `isSupplyManager`, `isViewer`

### Protected Routes
Automatically redirects to login if user is not authenticated.

### Dashboard
Role-based dashboard showing different content based on user permissions.

## Styling

Modern CSS with:
- Responsive grid layouts
- Flexbox for component alignment
- CSS variables for theming
- Mobile-first responsive design

## Development

### Available Scripts

- `npm start` - Development server
- `npm build` - Production build
- `npm test` - Run tests
- `npm run eject` - Eject from Create React App

### Code Organization

- Components are organized by feature
- Shared services in `/services`
- Global state in `/context`
- Utility functions in `/utils`

## Troubleshooting

**Authentication Issues:**
- Check Firebase configuration
- Verify backend is running
- Check browser console for errors

**API Issues:**
- Ensure backend is running on port 8080
- Check network tab for CORS errors
- Verify Firebase token is valid

**Styling Issues:**
- Check responsive breakpoints
- Verify CSS imports
- Test on different screen sizes