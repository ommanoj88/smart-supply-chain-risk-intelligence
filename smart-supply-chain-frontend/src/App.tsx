import React, { useEffect, useMemo } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ThemeProvider, CssBaseline, GlobalStyles, Box, Typography } from '@mui/material';
import { useMediaQuery } from '@mui/material';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

import { store } from './store';
import { lightTheme, darkTheme } from './theme';
import { AuthProvider } from './context/AuthContext';
import Header from './components/Layout/Header';
import ProtectedRoute from './components/Layout/ProtectedRoute';
import DualAuthLogin from './components/Auth/DualAuthLogin';
import Profile from './components/Auth/Profile';
import ResetPasswordForm from './components/Auth/ResetPasswordForm';
import ExecutiveDashboard from './components/dashboard/ExecutiveDashboard';
import AnalyticsDashboard from './components/dashboard/AnalyticsDashboard';
import EnhancedSupplierDashboard from './components/suppliers/EnhancedSupplierDashboard';
import EnhancedSupplierManagement from './components/suppliers/EnhancedSupplierManagement';
import EnhancedShipmentDashboard from './components/shipments/EnhancedShipmentDashboard';
import ShipmentTrackingWrapper from './components/shipments/ShipmentTrackingWrapper';
import NotificationCenter from './components/notifications/NotificationCenter';
import UserManagement from './components/admin/UserManagement';
import TestingEnvironment from './components/admin/TestingEnvironment';

// Enhanced Query Client with performance optimizations
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 2,
      refetchOnWindowFocus: false,
      staleTime: 5 * 60 * 1000, // 5 minutes
      gcTime: 10 * 60 * 1000, // 10 minutes (renamed from cacheTime in v5)
      refetchOnReconnect: true,
    },
    mutations: {
      retry: 1,
    },
  },
});

// Global styles for enhanced performance
const globalStyles = (
  <GlobalStyles
    styles={(theme) => ({
      '*': {
        boxSizing: 'border-box',
      },
      html: {
        WebkitFontSmoothing: 'antialiased',
        MozOsxFontSmoothing: 'grayscale',
        height: '100%',
        width: '100%',
      },
      body: {
        height: '100%',
        width: '100%',
        margin: 0,
        padding: 0,
        fontFamily: theme.typography.fontFamily,
        backgroundColor: theme.palette.background.default,
        color: theme.palette.text.primary,
        lineHeight: 1.6,
      },
      '#root': {
        height: '100%',
        width: '100%',
        display: 'flex',
        flexDirection: 'column',
      },
      // Custom scrollbar
      '::-webkit-scrollbar': {
        width: '8px',
      },
      '::-webkit-scrollbar-track': {
        backgroundColor: theme.palette.background.paper,
      },
      '::-webkit-scrollbar-thumb': {
        backgroundColor: theme.palette.divider,
        borderRadius: '4px',
        '&:hover': {
          backgroundColor: theme.palette.text.secondary,
        },
      },
      // Focus indicators
      '*:focus-visible': {
        outline: `2px solid ${theme.palette.primary.main}`,
        outlineOffset: '2px',
      },
      // Image optimization
      'img': {
        maxWidth: '100%',
        height: 'auto',
      },
      // Link styling
      'a': {
        color: theme.palette.primary.main,
        textDecoration: 'none',
        '&:hover': {
          textDecoration: 'underline',
        },
      },
    })}
  />
);

/**
 * Theme Provider Component with system preference detection
 */
const ThemeProviderWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
  // In a real app, this would come from Redux store
  const userThemePreference = 'system'; // 'light' | 'dark' | 'system'
  
  const theme = useMemo(() => {
    if (userThemePreference === 'system') {
      return prefersDarkMode ? darkTheme : lightTheme;
    }
    return userThemePreference === 'dark' ? darkTheme : lightTheme;
  }, [prefersDarkMode, userThemePreference]);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {globalStyles}
      {children}
    </ThemeProvider>
  );
};

/**
 * Enhanced App Component with Enterprise Features
 */
function App() {
  useEffect(() => {
    // Initialize performance monitoring
    if ('performance' in window && 'mark' in window.performance) {
      window.performance.mark('app-start');
    }

    // Register service worker for PWA capabilities
    if ('serviceWorker' in navigator && process.env.NODE_ENV === 'production') {
      navigator.serviceWorker.register('/sw.js')
        .then((registration) => {
          console.log('Service Worker registered successfully:', registration);
        })
        .catch((error) => {
          console.log('Service Worker registration failed:', error);
        });
    }

    return () => {
      if ('performance' in window && 'mark' in window.performance) {
        window.performance.mark('app-end');
        window.performance.measure('app-lifecycle', 'app-start', 'app-end');
      }
    };
  }, []);

  return (
    <Provider store={store}>
      <QueryClientProvider client={queryClient}>
        <ThemeProviderWrapper>
          <AuthProvider>
            <Router>
              <div className="App" style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                <Header />
                <main style={{ flex: 1, overflow: 'auto' }}>
                  <Routes>
                    {/* Authentication Routes */}
                    <Route path="/login" element={<DualAuthLogin />} />
                    <Route path="/reset-password" element={<ResetPasswordForm />} />
                    
                    {/* Protected Routes */}
                    <Route path="/" element={
                      <ProtectedRoute>
                        <Navigate to="/dashboard/executive" replace />
                      </ProtectedRoute>
                    } />
                    
                    {/* Dashboard Routes */}
                    <Route path="/dashboard/executive" element={
                      <ProtectedRoute>
                        <ExecutiveDashboard />
                      </ProtectedRoute>
                    } />
                    
                    {/* Demo Routes (for showcasing) */}
                    <Route path="/demo/executive" element={<ExecutiveDashboard />} />
                    <Route path="/demo/shipments" element={<EnhancedShipmentDashboard />} />
                    <Route path="/demo/suppliers" element={<EnhancedSupplierDashboard />} />
                    
                    <Route path="/dashboard/analytics" element={
                      <ProtectedRoute>
                        <AnalyticsDashboard />
                      </ProtectedRoute>
                    } />
                    
                    {/* Supplier Routes */}
                    <Route path="/suppliers" element={
                      <ProtectedRoute>
                        <EnhancedSupplierDashboard />
                      </ProtectedRoute>
                    } />
                    
                    <Route path="/suppliers/manage" element={
                      <ProtectedRoute>
                        <EnhancedSupplierManagement />
                      </ProtectedRoute>
                    } />
                    
                    {/* Shipment Routes */}
                    <Route path="/shipments" element={
                      <ProtectedRoute>
                        <EnhancedShipmentDashboard />
                      </ProtectedRoute>
                    } />
                    
                    <Route path="/shipments/tracking" element={
                      <ProtectedRoute>
                        <EnhancedShipmentDashboard />
                      </ProtectedRoute>
                    } />
                    
                    <Route path="/shipments/tracking/:trackingNumber" element={
                      <ProtectedRoute>
                        <ShipmentTrackingWrapper />
                      </ProtectedRoute>
                    } />
                    
                    {/* User Management Routes */}
                    <Route path="/admin/users" element={
                      <ProtectedRoute>
                        <UserManagement />
                      </ProtectedRoute>
                    } />
                    
                    {/* Admin Testing Environment Route */}
                    <Route path="/admin/testing" element={
                      <ProtectedRoute>
                        <TestingEnvironment />
                      </ProtectedRoute>
                    } />
                    
                    {/* User Routes */}
                    <Route path="/profile" element={
                      <ProtectedRoute>
                        <Profile />
                      </ProtectedRoute>
                    } />
                    
                    {/* Notifications */}
                    <Route path="/notifications" element={
                      <ProtectedRoute>
                        <NotificationCenter />
                      </ProtectedRoute>
                    } />
                    
                    {/* Fallback */}
                    <Route path="/unauthorized" element={
                      <Box sx={{ p: 4, textAlign: 'center' }}>
                        <Typography variant="h4" color="error" gutterBottom>
                          Access Denied
                        </Typography>
                        <Typography variant="body1">
                          You don't have permission to access this resource.
                        </Typography>
                      </Box>
                    } />
                    <Route path="*" element={<Navigate to="/dashboard/executive" replace />} />
                  </Routes>
                </main>
              </div>
            </Router>
          </AuthProvider>
        </ThemeProviderWrapper>
        
        {/* Development Tools */}
        {process.env.NODE_ENV === 'development' && (
          <ReactQueryDevtools initialIsOpen={false} />
        )}
      </QueryClientProvider>
    </Provider>
  );
}

export default App;