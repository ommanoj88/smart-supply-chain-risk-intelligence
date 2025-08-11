import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { CircularProgress, Box } from '@mui/material';

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const { isAuthenticated, user, loading } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <Box 
        display="flex" 
        justifyContent="center" 
        alignItems="center" 
        minHeight="100vh"
      >
        <CircularProgress />
      </Box>
    );
  }

  if (!isAuthenticated) {
    // Redirect to login page with return url
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Check role-based access
  if (requiredRole) {
    const hasRequiredRole = () => {
      switch (requiredRole) {
        case 'ADMIN':
          return user?.role === 'ADMIN';
        case 'SUPPLY_MANAGER':
          return user?.role === 'ADMIN' || user?.role === 'SUPPLY_MANAGER';
        case 'VIEWER':
          return user?.role === 'ADMIN' || user?.role === 'SUPPLY_MANAGER' || user?.role === 'VIEWER';
        default:
          return true;
      }
    };

    if (!hasRequiredRole()) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  return children;
};

export default ProtectedRoute;