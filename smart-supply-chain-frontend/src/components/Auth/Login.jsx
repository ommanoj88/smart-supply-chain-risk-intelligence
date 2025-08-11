import React, { useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Button,
  Box,
  Alert,
  CircularProgress,
  Divider,
} from '@mui/material';
import { Google } from '@mui/icons-material';
import { useAuth } from '../../context/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';

const Login = () => {
  const { signInWithGoogle, loading, error } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [isSigningIn, setIsSigningIn] = useState(false);

  const from = location.state?.from?.pathname || '/';

  const handleGoogleSignIn = async () => {
    setIsSigningIn(true);
    try {
      await signInWithGoogle();
      navigate(from, { replace: true });
    } catch (error) {
      console.error('Sign in error:', error);
    } finally {
      setIsSigningIn(false);
    }
  };

  return (
    <Container component="main" maxWidth="sm">
      <Box
        sx={{
          marginTop: 8,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Paper elevation={3} sx={{ padding: 4, width: '100%' }}>
          <Box sx={{ textAlign: 'center', mb: 3 }}>
            <Typography component="h1" variant="h4" gutterBottom>
              Smart Supply Chain
            </Typography>
            <Typography variant="h6" color="textSecondary" gutterBottom>
              Risk Intelligence Platform
            </Typography>
            <Divider sx={{ my: 2 }} />
            <Typography variant="body1" color="textSecondary">
              Sign in to access the platform
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box sx={{ mt: 3 }}>
            <Button
              fullWidth
              variant="contained"
              size="large"
              startIcon={
                isSigningIn || loading ? (
                  <CircularProgress size={20} color="inherit" />
                ) : (
                  <Google />
                )
              }
              onClick={handleGoogleSignIn}
              disabled={isSigningIn || loading}
              sx={{
                backgroundColor: '#4285f4',
                '&:hover': {
                  backgroundColor: '#357ae8',
                },
                py: 1.5,
              }}
            >
              {isSigningIn || loading ? 'Signing in...' : 'Sign in with Google'}
            </Button>
          </Box>

          <Box sx={{ mt: 3, textAlign: 'center' }}>
            <Typography variant="body2" color="textSecondary">
              By signing in, you agree to use this platform in accordance with our policies.
            </Typography>
          </Box>

          <Box sx={{ mt: 3, p: 2, backgroundColor: '#f5f5f5', borderRadius: 1 }}>
            <Typography variant="h6" gutterBottom>
              User Roles:
            </Typography>
            <Typography variant="body2" component="div">
              <strong>Viewer:</strong> View supply chain data and reports
              <br />
              <strong>Supply Manager:</strong> Manage supply chain operations and create reports
              <br />
              <strong>Admin:</strong> Full access including user management
            </Typography>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default Login;