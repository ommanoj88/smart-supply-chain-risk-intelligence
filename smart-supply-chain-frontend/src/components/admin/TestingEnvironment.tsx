import React, { useState } from 'react';
import {
  Box,
  Container,
  Tabs,
  Tab,
  Typography,
  Card,
  CardContent,
  useTheme,
  alpha,
} from '@mui/material';
import {
  Storage,
  Api,
  Timeline,
  Assessment,
  Warning,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { useAuth } from '../../context/AuthContext';

// Import individual testing components
import MockDataGenerator from './testing/MockDataGenerator';
import APISimulationDashboard from './testing/APISimulationDashboard';
import ScenarioBuilder from './testing/ScenarioBuilder';
import TestingResults from './testing/TestingResults';

/**
 * Main Testing Environment Component
 * Provides tabbed interface for all admin testing functionality
 */
const TestingEnvironment: React.FC = () => {
  const theme = useTheme();
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState(0);

  // Only render for admin users
  if (user?.role !== 'ADMIN') {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Card elevation={3}>
          <CardContent sx={{ textAlign: 'center', py: 6 }}>
            <Warning color="error" sx={{ fontSize: 64, mb: 2 }} />
            <Typography variant="h4" color="error" gutterBottom>
              Access Denied
            </Typography>
            <Typography variant="body1" color="text.secondary">
              This testing environment is only accessible to administrators.
            </Typography>
          </CardContent>
        </Card>
      </Container>
    );
  }

  const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
    setActiveTab(newValue);
  };

  const tabData = [
    {
      label: 'Mock Data Generator',
      icon: <Storage />,
      component: <MockDataGenerator />,
      description: 'Generate realistic test data for suppliers, shipments, and scenarios'
    },
    {
      label: 'API Simulation',
      icon: <Api />,
      component: <APISimulationDashboard />,
      description: 'Control and monitor external API simulations in real-time'
    },
    {
      label: 'Scenario Builder',
      icon: <Timeline />,
      component: <ScenarioBuilder />,
      description: 'Create and execute crisis scenarios for testing system resilience'
    },
    {
      label: 'Testing Results',
      icon: <Assessment />,
      component: <TestingResults />,
      description: 'View comprehensive testing outcomes and performance metrics'
    }
  ];

  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: 'background.default' }}>
      <Container maxWidth="xl" sx={{ py: 3 }}>
        {/* Header */}
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Box sx={{ mb: 4 }}>
            <Typography variant="h3" gutterBottom sx={{ 
              fontWeight: 700,
              background: `linear-gradient(45deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
              backgroundClip: 'text',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
            }}>
              Admin Testing Environment
            </Typography>
            <Typography variant="h6" color="text.secondary" sx={{ mb: 3 }}>
              Enterprise-grade testing interface for supply chain risk intelligence platform
            </Typography>
          </Box>
        </motion.div>

        {/* Tabbed Interface */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <Card elevation={3} sx={{ 
            mb: 3,
            backgroundColor: alpha(theme.palette.background.paper, 0.95),
            backdropFilter: 'blur(10px)',
            border: `1px solid ${alpha(theme.palette.primary.main, 0.1)}`,
          }}>
            <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
              <Tabs 
                value={activeTab} 
                onChange={handleTabChange}
                variant="fullWidth"
                sx={{
                  '& .MuiTab-root': {
                    minHeight: 72,
                    textTransform: 'none',
                    fontWeight: 600,
                    '&.Mui-selected': {
                      backgroundColor: alpha(theme.palette.primary.main, 0.1),
                    }
                  }
                }}
              >
                {tabData.map((tab, index) => (
                  <Tab
                    key={index}
                    icon={tab.icon}
                    label={tab.label}
                    iconPosition="start"
                    sx={{ 
                      flexDirection: 'row',
                      gap: 1,
                      '& .MuiSvgIcon-root': {
                        fontSize: 20
                      }
                    }}
                  />
                ))}
              </Tabs>
            </Box>

            {/* Tab Description */}
            <Box sx={{ p: 2, backgroundColor: alpha(theme.palette.primary.main, 0.05) }}>
              <Typography variant="body2" color="text.secondary">
                {tabData[activeTab].description}
              </Typography>
            </Box>
          </Card>
        </motion.div>

        {/* Tab Content */}
        <motion.div
          key={activeTab}
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.3 }}
        >
          {tabData[activeTab].component}
        </motion.div>
      </Container>
    </Box>
  );
};

export default TestingEnvironment;