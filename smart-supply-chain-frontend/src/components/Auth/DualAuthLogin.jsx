import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Card from '../ui/Card';
import Button from '../ui/Button';
import EnhancedLogin from './EnhancedLogin';
import ForgotPasswordForm from './ForgotPasswordForm';

// Icons
const GoogleIcon = () => (
  <svg className="w-5 h-5" viewBox="0 0 24 24">
    <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
    <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
    <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
    <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
  </svg>
);

const DualAuthLogin = () => {
  const { signInWithGoogle, loading: authLoading, error: authError } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [currentView, setCurrentView] = useState('login'); // 'login', 'reset'
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const from = location.state?.from?.pathname || '/';

  const handleGoogleSignIn = async () => {
    setLoading(true);
    setError('');
    try {
      await signInWithGoogle();
      navigate(from, { replace: true });
    } catch (error) {
      console.error('Google sign in error:', error);
      setError('Google sign-in failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleJWTLogin = async (identifier, password) => {
    setLoading(true);
    setError('');
    
    try {
      const response = await fetch('/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ identifier, password }),
      });

      if (response.ok) {
        const data = await response.json();
        // Store JWT token
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        
        // Redirect to protected route
        navigate(from, { replace: true });
        
        // Reload to update auth context
        window.location.reload();
      } else {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Login failed');
      }
    } catch (error) {
      console.error('JWT login error:', error);
      throw error;
    } finally {
      setLoading(false);
    }
  };

  if (currentView === 'reset') {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center p-6">
        <ForgotPasswordForm onBackToLogin={() => setCurrentView('login')} />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-6">
      <div className="w-full max-w-md space-y-6">
        {/* Enhanced JWT Login Form */}
        <EnhancedLogin 
          onLogin={handleJWTLogin}
          onSwitchToReset={() => setCurrentView('reset')}
        />

        {/* Divider */}
        <div className="relative">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-300" />
          </div>
          <div className="relative flex justify-center text-sm">
            <span className="px-2 bg-gray-50 text-gray-500">Or continue with</span>
          </div>
        </div>

        {/* Google Sign-In Card */}
        <Card variant="elevated">
          <Card.Content className="p-6">
            <div className="text-center space-y-4">
              <p className="text-sm text-gray-600">
                Sign in with your Google account for quick access
              </p>
              
              <Button
                variant="outline"
                className="w-full"
                onClick={handleGoogleSignIn}
                loading={loading || authLoading}
                disabled={loading || authLoading}
              >
                <GoogleIcon />
                Sign in with Google
              </Button>

              {(error || authError) && (
                <motion.div
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="p-3 bg-red-50 border border-red-200 rounded-lg"
                >
                  <p className="text-red-700 text-sm">{error || authError}</p>
                </motion.div>
              )}
            </div>
          </Card.Content>
        </Card>

        {/* User Roles Information */}
        <Card variant="outlined">
          <Card.Content className="p-4">
            <h3 className="text-sm font-medium text-gray-900 mb-3">User Roles:</h3>
            <div className="space-y-2 text-xs text-gray-600">
              <div>
                <span className="font-medium">Viewer:</span> View supply chain data and reports
              </div>
              <div>
                <span className="font-medium">Supply Manager:</span> Manage operations and create reports
              </div>
              <div>
                <span className="font-medium">Admin:</span> Full access including user management
              </div>
            </div>
          </Card.Content>
        </Card>

        {/* Demo Credentials */}
        <Card variant="outlined">
          <Card.Content className="p-4">
            <h3 className="text-sm font-medium text-gray-900 mb-3">Demo Credentials:</h3>
            <div className="space-y-2 text-xs text-gray-600 font-mono">
              <div>
                <span className="font-medium">Admin:</span> admin / password123
              </div>
              <div>
                <span className="font-medium">Manager:</span> manager / password123
              </div>
              <div>
                <span className="font-medium">Viewer:</span> viewer / password123
              </div>
            </div>
          </Card.Content>
        </Card>
      </div>
    </div>
  );
};

export default DualAuthLogin;