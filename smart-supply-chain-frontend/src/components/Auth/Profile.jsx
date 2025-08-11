import React from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Chip,
  Avatar,
  Divider,
  Grid,
} from '@mui/material';
import { 
  Person, 
  Security, 
  Schedule,
  Verified 
} from '@mui/icons-material';
import { useAuth } from '../../context/AuthContext';

const Profile = () => {
  const { user } = useAuth();

  const getRoleColor = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'error';
      case 'SUPPLY_MANAGER':
        return 'warning';
      case 'VIEWER':
        return 'info';
      default:
        return 'default';
    }
  };

  const getRoleDescription = (role) => {
    switch (role) {
      case 'ADMIN':
        return 'Full administrative access including user management';
      case 'SUPPLY_MANAGER':
        return 'Manage supply chain operations and create reports';
      case 'VIEWER':
        return 'View supply chain data and reports';
      default:
        return 'No specific role assigned';
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleString();
  };

  if (!user) {
    return (
      <Container>
        <Typography>Loading user profile...</Typography>
      </Container>
    );
  }

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Typography variant="h4" gutterBottom>
        User Profile
      </Typography>
      
      <Grid container spacing={3}>
        {/* Main Profile Card */}
        <Grid item xs={12} md={8}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
              <Avatar
                sx={{
                  width: 80,
                  height: 80,
                  mr: 2,
                  backgroundColor: 'primary.main',
                  fontSize: '2rem',
                }}
              >
                {user.name ? user.name.charAt(0).toUpperCase() : 'U'}
              </Avatar>
              <Box>
                <Typography variant="h5" gutterBottom>
                  {user.name}
                </Typography>
                <Typography variant="body1" color="textSecondary" gutterBottom>
                  {user.email}
                </Typography>
                <Chip
                  label={user.role}
                  color={getRoleColor(user.role)}
                  icon={<Security />}
                  variant="outlined"
                />
              </Box>
            </Box>
            
            <Divider sx={{ my: 2 }} />
            
            <Box sx={{ mt: 2 }}>
              <Typography variant="h6" gutterBottom>
                Role Permissions
              </Typography>
              <Typography variant="body2" color="textSecondary">
                {getRoleDescription(user.role)}
              </Typography>
            </Box>
          </Paper>
        </Grid>

        {/* Account Details Card */}
        <Grid item xs={12} md={4}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Account Details
            </Typography>
            
            <Box sx={{ mt: 2 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Verified sx={{ mr: 1, color: 'success.main' }} />
                <Typography variant="body2">
                  Account Verified
                </Typography>
              </Box>
              
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Person sx={{ mr: 1, color: 'text.secondary' }} />
                <Box>
                  <Typography variant="body2" fontWeight="bold">
                    User ID
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    #{user.id}
                  </Typography>
                </Box>
              </Box>
              
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Schedule sx={{ mr: 1, color: 'text.secondary' }} />
                <Box>
                  <Typography variant="body2" fontWeight="bold">
                    Member Since
                  </Typography>
                  <Typography variant="body2" color="textSecondary">
                    {formatDate(user.createdAt)}
                  </Typography>
                </Box>
              </Box>
              
              {user.updatedAt !== user.createdAt && (
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <Schedule sx={{ mr: 1, color: 'text.secondary' }} />
                  <Box>
                    <Typography variant="body2" fontWeight="bold">
                      Last Updated
                    </Typography>
                    <Typography variant="body2" color="textSecondary">
                      {formatDate(user.updatedAt)}
                    </Typography>
                  </Box>
                </Box>
              )}
            </Box>
          </Paper>
        </Grid>

        {/* Firebase Info Card */}
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Authentication Information
            </Typography>
            <Typography variant="body2" color="textSecondary" gutterBottom>
              This account is authenticated through Firebase Authentication
            </Typography>
            <Box sx={{ mt: 2, p: 2, backgroundColor: '#f5f5f5', borderRadius: 1 }}>
              <Typography variant="body2" fontWeight="bold">
                Firebase UID:
              </Typography>
              <Typography 
                variant="body2" 
                sx={{ 
                  fontFamily: 'monospace', 
                  wordBreak: 'break-all' 
                }}
              >
                {user.firebaseUid}
              </Typography>
            </Box>
          </Paper>
        </Grid>
      </Grid>
    </Container>
  );
};

export default Profile;