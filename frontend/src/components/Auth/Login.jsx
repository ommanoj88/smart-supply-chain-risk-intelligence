import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Navigate } from 'react-router-dom';

const Login = () => {
  const { login, isAuthenticated, loading, error } = useAuth();
  const [isLoggingIn, setIsLoggingIn] = useState(false);

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleGoogleLogin = async () => {
    setIsLoggingIn(true);
    try {
      await login();
    } catch (err) {
      console.error('Login failed:', err);
    } finally {
      setIsLoggingIn(false);
    }
  };

  if (loading) {
    return (
      <div className="login-container">
        <div className="login-card">
          <div className="loading">Loading...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>Smart Supply Chain</h1>
          <h2>Risk Intelligence Platform</h2>
          <p>Sign in to access your dashboard</p>
        </div>
        
        {error && (
          <div className="error-message">
            <p>Error: {error}</p>
          </div>
        )}
        
        <div className="login-form">
          <button
            onClick={handleGoogleLogin}
            disabled={isLoggingIn}
            className="google-login-btn"
          >
            {isLoggingIn ? (
              <span>Signing in...</span>
            ) : (
              <>
                <svg width="18" height="18" viewBox="0 0 18 18">
                  <path fill="#4285F4" d="M16.51 8.18h-7.33v3.2h4.3c-.18 1.01-.74 1.87-1.55 2.4v2h2.51c1.47-1.35 2.32-3.34 2.32-5.68 0-.55-.05-1.08-.14-1.6z"/>
                  <path fill="#34A853" d="M9.18 16.5c2.1 0 3.86-.7 5.15-1.89l-2.51-1.95c-.7.47-1.6.75-2.64.75-2.03 0-3.75-1.37-4.37-3.22H2.24v2.01C3.53 14.27 6.17 16.5 9.18 16.5z"/>
                  <path fill="#FBBC05" d="M4.81 10.19c-.16-.47-.25-.97-.25-1.49s.09-1.02.25-1.49V5.2H2.24A8.11 8.11 0 0 0 1.5 8.7c0 1.31.3 2.54.84 3.64l2.47-1.95z"/>
                  <path fill="#EA4335" d="M9.18 4.28c1.14 0 2.17.39 2.98 1.16l2.23-2.23C12.99 1.99 11.24 1.5 9.18 1.5 6.17 1.5 3.53 3.73 2.24 6.8l2.57 2.01c.62-1.85 2.34-3.22 4.37-3.22z"/>
                </svg>
                Continue with Google
              </>
            )}
          </button>
        </div>
        
        <div className="login-footer">
          <p>Welcome to the Smart Supply Chain Risk Intelligence Platform</p>
        </div>
      </div>
    </div>
  );
};

export default Login;