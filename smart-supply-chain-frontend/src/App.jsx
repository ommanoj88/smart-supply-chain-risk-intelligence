import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { Box, Container, Typography, Paper, Alert } from '@mui/material';

import { AuthProvider } from './context/AuthContext';
import Header from './components/Layout/Header';
import ProtectedRoute from './components/Layout/ProtectedRoute';
import Login from './components/Auth/Login';
import Profile from './components/Auth/Profile';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

// Dashboard component
const Dashboard = () => {
  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        Supply Chain Risk Intelligence Dashboard
      </Typography>
      <Paper sx={{ p: 3, mt: 2 }}>
        <Typography variant="h6" gutterBottom>
          Welcome to the Smart Supply Chain Risk Intelligence Platform
        </Typography>
        <Typography variant="body1" paragraph>
          This platform provides comprehensive tools for monitoring and analyzing supply chain risks.
          Use the navigation menu to access different features based on your role.
        </Typography>
        <Alert severity="info">
          Authentication system is now active. Your session is secured with Firebase Authentication.
        </Alert>
      </Paper>
    </Container>
  );
};

// Unauthorized component
const Unauthorized = () => {
  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Paper sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h4" color="error" gutterBottom>
          Access Denied
        </Typography>
        <Typography variant="body1">
          You don't have permission to access this resource.
        </Typography>
      </Paper>
    </Container>
  );
};

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <Box sx={{ flexGrow: 1 }}>
            <Header />
            <main>
              <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/unauthorized" element={<Unauthorized />} />
                <Route
                  path="/"
                  element={
                    <ProtectedRoute>
                      <Dashboard />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/profile"
                  element={
                    <ProtectedRoute>
                      <Profile />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/admin"
                  element={
                    <ProtectedRoute requiredRole="ADMIN">
                      <Container maxWidth="lg" sx={{ mt: 4 }}>
                        <Typography variant="h4">Admin Panel</Typography>
                        <Paper sx={{ p: 3, mt: 2 }}>
                          <Typography>
                            Admin-only content goes here. Only users with ADMIN role can access this.
                          </Typography>
                        </Paper>
                      </Container>
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/supply-manager"
                  element={
                    <ProtectedRoute requiredRole="SUPPLY_MANAGER">
                      <Container maxWidth="lg" sx={{ mt: 4 }}>
                        <Typography variant="h4">Supply Manager Dashboard</Typography>
                        <Paper sx={{ p: 3, mt: 2 }}>
                          <Typography>
                            Supply Manager content goes here. Users with SUPPLY_MANAGER or ADMIN role can access this.
                          </Typography>
                        </Paper>
                      </Container>
                    </ProtectedRoute>
                  }
                />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </main>
          </Box>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;