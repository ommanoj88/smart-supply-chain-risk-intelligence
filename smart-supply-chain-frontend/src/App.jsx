import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';

import { AuthProvider } from './context/AuthContext';
import Header from './components/Layout/Header';
import ProtectedRoute from './components/Layout/ProtectedRoute';
import DualAuthLogin from './components/Auth/DualAuthLogin';
import Profile from './components/Auth/Profile';
import ResetPasswordForm from './components/Auth/ResetPasswordForm';
import AnalyticsDashboard from './components/dashboard/AnalyticsDashboard';
import SupplierDashboard from './components/suppliers/SupplierDashboard';
import ShipmentDashboard from './components/shipments/ShipmentDashboard';
import ShipmentTracking from './components/shipments/ShipmentTracking';

// Create a client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

// Use the new AnalyticsDashboard component instead of the old Dashboard
const Dashboard = () => {
  return <AnalyticsDashboard />;
};

// Unauthorized component
const Unauthorized = () => {
  return (
    <div className="page-container">
      <div className="content-container">
        <div className="max-w-md mx-auto text-center">
          <div className="card p-8">
            <h1 className="text-3xl font-bold text-red-600 mb-4">Access Denied</h1>
            <p className="text-gray-600 mb-6">
              You don't have permission to access this resource.
            </p>
            <button 
              onClick={() => window.history.back()} 
              className="btn-primary"
            >
              Go Back
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

// Admin Panel Component
const AdminPanel = () => {
  return (
    <div className="page-container">
      <div className="content-container">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Admin Panel</h1>
        <div className="card p-8">
          <h2 className="text-xl font-semibold text-gray-800 mb-4">
            Administrative Controls
          </h2>
          <p className="text-gray-600">
            Admin-only content goes here. Only users with ADMIN role can access this section.
            This includes user management, system configuration, and advanced analytics.
          </p>
        </div>
      </div>
    </div>
  );
};

// Supply Manager Dashboard Component
const SupplyManagerDashboard = () => {
  return (
    <div className="page-container">
      <div className="content-container">
        <h1 className="text-3xl font-bold text-gray-900 mb-6">Supply Manager Dashboard</h1>
        <div className="card p-8">
          <h2 className="text-xl font-semibold text-gray-800 mb-4">
            Supply Chain Operations
          </h2>
          <p className="text-gray-600">
            Supply Manager content goes here. Users with SUPPLY_MANAGER or ADMIN role can access this.
            Manage suppliers, monitor performance, and assess risks.
          </p>
        </div>
      </div>
    </div>
  );
};

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router>
          <div className="min-h-screen bg-gray-50">
            <Header />
            <main>
              <Routes>
                <Route path="/login" element={<DualAuthLogin />} />
                <Route path="/reset-password" element={<ResetPasswordForm />} />
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
                  path="/suppliers"
                  element={
                    <ProtectedRoute>
                      <SupplierDashboard />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/shipments"
                  element={
                    <ProtectedRoute>
                      <ShipmentDashboard />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/shipments/track/:trackingNumber"
                  element={
                    <ProtectedRoute>
                      <ShipmentTracking />
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
                      <AdminPanel />
                    </ProtectedRoute>
                  }
                />
                <Route
                  path="/supply-manager"
                  element={
                    <ProtectedRoute requiredRole="SUPPLY_MANAGER">
                      <SupplyManagerDashboard />
                    </ProtectedRoute>
                  }
                />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </main>
          </div>
        </Router>
      </AuthProvider>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
}

export default App;