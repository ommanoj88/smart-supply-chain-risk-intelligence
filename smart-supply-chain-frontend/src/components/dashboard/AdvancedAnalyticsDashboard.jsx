import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import {
  Grid, Card, CardContent, CardHeader, Typography, Box,
  Tabs, Tab, Button, IconButton, Tooltip, Alert, Chip,
  Drawer, AppBar, Toolbar, useTheme, useMediaQuery
} from '@mui/material';
import {
  Analytics, Dashboard, Insights, Speed, Security,
  Settings, Refresh, Fullscreen, FullscreenExit,
  Menu as MenuIcon
} from '@mui/icons-material';

// Import our custom components
import PredictiveAnalyticsDashboard from './PredictiveAnalyticsDashboard';
import AIInsightsPanel from './AIInsightsPanel';
import { useAnalyticsWebSocket } from '../../hooks/useWebSocketConnection';
import analyticsService from '../../services/analyticsService';

const AdvancedAnalyticsDashboard = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));
  
  const [selectedTab, setSelectedTab] = useState(0);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const [drawerOpen, setDrawerOpen] = useState(!isMobile);
  const [dashboardData, setDashboardData] = useState({});
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);

  // WebSocket connection for real-time updates
  const {
    connectionStatus,
    analyticsData,
    realTimeUpdates,
    riskUpdates,
    recommendations,
    isConnected
  } = useAnalyticsWebSocket({
    autoConnect: true,
    debug: false
  });

  // Tab configuration
  const tabs = [
    {
      label: 'Overview',
      icon: <Dashboard />,
      component: OverviewPanel
    },
    {
      label: 'Predictive Analytics',
      icon: <Analytics />,
      component: PredictiveAnalyticsDashboard
    },
    {
      label: 'AI Insights',
      icon: <Insights />,
      component: AIInsightsPanel
    },
    {
      label: 'Performance',
      icon: <Speed />,
      component: PerformancePanel
    },
    {
      label: 'Security',
      icon: <Security />,
      component: SecurityPanel
    }
  ];

  // Load initial data
  useEffect(() => {
    const loadDashboardData = async () => {
      setLoading(true);
      try {
        const [summary, health] = await Promise.all([
          analyticsService.getAnalyticsSummary(),
          analyticsService.getHealthStatus()
        ]);
        
        setDashboardData({
          summary,
          health,
          lastUpdated: new Date()
        });
      } catch (error) {
        console.error('Error loading dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, []);

  // Handle manual refresh
  const handleRefresh = async () => {
    setRefreshing(true);
    try {
      const summary = await analyticsService.getAnalyticsSummary();
      setDashboardData(prev => ({
        ...prev,
        summary,
        lastUpdated: new Date()
      }));
    } catch (error) {
      console.error('Error refreshing data:', error);
    } finally {
      setRefreshing(false);
    }
  };

  // Toggle fullscreen mode
  const toggleFullscreen = () => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen();
      setIsFullscreen(true);
    } else {
      document.exitFullscreen();
      setIsFullscreen(false);
    }
  };

  // Handle tab change
  const handleTabChange = (event, newValue) => {
    setSelectedTab(newValue);
  };

  const CurrentTabComponent = tabs[selectedTab]?.component || OverviewPanel;

  return (
    <Box sx={{ display: 'flex', height: '100vh', backgroundColor: 'grey.50' }}>
      {/* Sidebar Navigation */}
      <Drawer
        variant={isMobile ? 'temporary' : 'persistent'}
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        sx={{
          width: 280,
          flexShrink: 0,
          '& .MuiDrawer-paper': {
            width: 280,
            boxSizing: 'border-box',
            backgroundColor: 'primary.dark',
            color: 'white'
          }
        }}
      >
        <Box sx={{ p: 2 }}>
          <Typography variant="h6" component="div" gutterBottom>
            Advanced Analytics
          </Typography>
          <Chip 
            label={connectionStatus} 
            size="small"
            color={isConnected ? 'success' : 'error'}
            sx={{ mb: 2 }}
          />
        </Box>
        
        <Tabs
          value={selectedTab}
          onChange={handleTabChange}
          orientation="vertical"
          variant="fullWidth"
          sx={{
            '& .MuiTab-root': {
              color: 'rgba(255, 255, 255, 0.7)',
              alignItems: 'flex-start',
              textAlign: 'left',
              '&.Mui-selected': {
                color: 'white',
                backgroundColor: 'rgba(255, 255, 255, 0.1)'
              }
            }
          }}
        >
          {tabs.map((tab, index) => (
            <Tab
              key={index}
              icon={tab.icon}
              label={tab.label}
              iconPosition="start"
              sx={{ justifyContent: 'flex-start', minHeight: 48 }}
            />
          ))}
        </Tabs>

        {/* Real-time Updates Summary */}
        <Box sx={{ p: 2, mt: 'auto' }}>
          <Typography variant="subtitle2" gutterBottom>
            Recent Updates
          </Typography>
          <Typography variant="body2" color="rgba(255, 255, 255, 0.7)">
            {realTimeUpdates.length} new updates
          </Typography>
          <Typography variant="caption" color="rgba(255, 255, 255, 0.5)">
            Last updated: {new Date().toLocaleTimeString()}
          </Typography>
        </Box>
      </Drawer>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          display: 'flex',
          flexDirection: 'column',
          ml: isMobile ? 0 : drawerOpen ? 0 : '-280px',
          transition: theme.transitions.create(['margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
          })
        }}
      >
        {/* Top App Bar */}
        <AppBar 
          position="static" 
          elevation={1}
          sx={{ backgroundColor: 'white', color: 'text.primary' }}
        >
          <Toolbar>
            {isMobile && (
              <IconButton
                edge="start"
                onClick={() => setDrawerOpen(true)}
                sx={{ mr: 2 }}
              >
                <MenuIcon />
              </IconButton>
            )}
            
            <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
              {tabs[selectedTab]?.label} Dashboard
            </Typography>

            {/* Status Indicators */}
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Tooltip title="WebSocket Status">
                <Chip
                  size="small"
                  label={isConnected ? 'Live' : 'Offline'}
                  color={isConnected ? 'success' : 'error'}
                />
              </Tooltip>

              <Tooltip title="Refresh Data">
                <IconButton onClick={handleRefresh} disabled={refreshing}>
                  <Refresh className={refreshing ? 'animate-spin' : ''} />
                </IconButton>
              </Tooltip>

              <Tooltip title={isFullscreen ? 'Exit Fullscreen' : 'Fullscreen'}>
                <IconButton onClick={toggleFullscreen}>
                  {isFullscreen ? <FullscreenExit /> : <Fullscreen />}
                </IconButton>
              </Tooltip>

              <Tooltip title="Settings">
                <IconButton>
                  <Settings />
                </IconButton>
              </Tooltip>
            </Box>
          </Toolbar>
        </AppBar>

        {/* Connection Status Alert */}
        {!isConnected && (
          <Alert severity="warning" sx={{ m: 2 }}>
            Real-time updates are currently unavailable. Some features may not work as expected.
          </Alert>
        )}

        {/* Tab Content */}
        <Box sx={{ flexGrow: 1, p: 3, overflow: 'auto' }}>
          <motion.div
            key={selectedTab}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
          >
            <CurrentTabComponent
              dashboardData={dashboardData}
              analyticsData={analyticsData}
              realTimeUpdates={realTimeUpdates}
              riskUpdates={riskUpdates}
              recommendations={recommendations}
              isConnected={isConnected}
              loading={loading}
            />
          </motion.div>
        </Box>
      </Box>
    </Box>
  );
};

// Overview Panel Component
const OverviewPanel = ({ dashboardData, analyticsData, realTimeUpdates, isConnected }) => {
  const kpis = [
    {
      title: 'Total Suppliers',
      value: dashboardData.summary?.totalSuppliers || 0,
      trend: '+5%',
      color: 'primary'
    },
    {
      title: 'Avg Risk Score',
      value: dashboardData.summary?.averageRiskScore || 0,
      trend: '-2%',
      color: 'success'
    },
    {
      title: 'Active Alerts',
      value: realTimeUpdates.filter(u => u.type === 'ANALYTICS_ALERT').length,
      trend: '+12%',
      color: 'warning'
    },
    {
      title: 'Model Accuracy',
      value: '94.2%',
      trend: '+1.5%',
      color: 'info'
    }
  ];

  return (
    <Grid container spacing={3}>
      {/* KPI Cards */}
      {kpis.map((kpi, index) => (
        <Grid item xs={12} sm={6} lg={3} key={index}>
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: index * 0.1 }}
          >
            <Card>
              <CardContent>
                <Typography color="textSecondary" gutterBottom variant="body2">
                  {kpi.title}
                </Typography>
                <Typography variant="h4" component="div">
                  {kpi.value}
                </Typography>
                <Typography variant="body2" color={`${kpi.color}.main`}>
                  {kpi.trend} from last month
                </Typography>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
      ))}

      {/* System Status */}
      <Grid item xs={12} md={6}>
        <Card>
          <CardHeader title="System Status" />
          <CardContent>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="body2">Analytics Engine</Typography>
              <Chip label="Operational" color="success" size="small" />
            </Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="body2">ML Prediction Service</Typography>
              <Chip label="Operational" color="success" size="small" />
            </Box>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="body2">Real-time Updates</Typography>
              <Chip 
                label={isConnected ? 'Connected' : 'Disconnected'} 
                color={isConnected ? 'success' : 'error'} 
                size="small" 
              />
            </Box>
            <Box display="flex" justifyContent="space-between" alignItems="center">
              <Typography variant="body2">Database</Typography>
              <Chip label="Operational" color="success" size="small" />
            </Box>
          </CardContent>
        </Card>
      </Grid>

      {/* Recent Activity */}
      <Grid item xs={12} md={6}>
        <Card>
          <CardHeader title="Recent Activity" />
          <CardContent>
            <Box maxHeight={300} overflow="auto">
              {realTimeUpdates.slice(0, 5).map((update, index) => (
                <Box key={index} mb={2} p={1} bgcolor="grey.50" borderRadius={1}>
                  <Typography variant="body2" gutterBottom>
                    {update.type}: {update.message || 'System update'}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {new Date(update.timestamp).toLocaleString()}
                  </Typography>
                </Box>
              ))}
              {realTimeUpdates.length === 0 && (
                <Typography variant="body2" color="text.secondary" textAlign="center">
                  No recent activity
                </Typography>
              )}
            </Box>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

// Performance Panel Component
const PerformancePanel = ({ analyticsData }) => {
  return (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <Card>
          <CardHeader title="Analytics Performance Metrics" />
          <CardContent>
            <Typography variant="h6" gutterBottom>
              System performance monitoring and ML model metrics will be displayed here.
            </Typography>
            <Alert severity="info">
              Performance monitoring dashboard is being enhanced with real-time metrics.
            </Alert>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

// Security Panel Component
const SecurityPanel = () => {
  return (
    <Grid container spacing={3}>
      <Grid item xs={12}>
        <Card>
          <CardHeader title="Security & Risk Monitoring" />
          <CardContent>
            <Typography variant="h6" gutterBottom>
              Security monitoring and risk assessment tools will be displayed here.
            </Typography>
            <Alert severity="info">
              Security dashboard is being enhanced with advanced threat detection.
            </Alert>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export default AdvancedAnalyticsDashboard;