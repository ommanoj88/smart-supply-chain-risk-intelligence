import React, { useState, useEffect } from 'react';
import {
  Grid,
  Box,
  Typography,
  CardContent,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  LinearProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Switch,
  FormControlLabel,
  useTheme,
  alpha,
} from '@mui/material';
import {
  Stop,
  Refresh,
  Warning,
  CheckCircle,
  Error,
  Info,
  Speed,
  Security,
  Storage,
  NetworkCheck,
  Timeline,
  Assessment,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { 
  PremiumLineChart,
} from '../common/PremiumCharts';
import { 
  PremiumKPICard,
  GlassCard,
  EnhancedFloatingActionButton,
} from '../ui/PremiumComponents';

/**
 * Enterprise-Grade Admin Testing Dashboard
 * SAP-level testing environment with comprehensive scenario simulation,
 * mock data generation, API testing, and real-time monitoring
 */
export const AdminTestingDashboard: React.FC = () => {
  const theme = useTheme();
  
  // State management
  const [systemStats, setSystemStats] = useState({
    totalSuppliers: 156,
    totalShipments: 2450,
    totalTrackingEvents: 15670,
    onTimeDeliveryRate: 94.8,
    averageRiskScore: 2.3,
    highRiskSuppliers: 12,
    activeShipments: 847,
    recentShipments: 23,
    recentEvents: 145,
  });
  
  const [loading, setLoading] = useState(false);
  const [activeSimulation, setActiveSimulation] = useState<any>(null);
  const [simulationResults, setSimulationResults] = useState<any[]>([]);
  const [realTimeUpdates, setRealTimeUpdates] = useState(true);
  
  // Load testing configuration
  const [loadTestConfig, setLoadTestConfig] = useState({
    suppliers: 1000,
    shipments: 10000,
    events: 100000,
    duration: 60,
  });
  
  const [apiTestResults, setApiTestResults] = useState([
    { api: 'SAP Digital Manufacturing', status: 'online', responseTime: 245, lastTest: '2 min ago' },
    { api: 'Oracle ERP', status: 'online', responseTime: 178, lastTest: '1 min ago' },
    { api: 'Weather Service', status: 'online', responseTime: 89, lastTest: '30 sec ago' },
    { api: 'Currency Exchange', status: 'warning', responseTime: 1240, lastTest: '5 min ago' },
    { api: 'Customs API', status: 'online', responseTime: 334, lastTest: '3 min ago' },
  ]);

  useEffect(() => {
    loadSystemStats();
    loadAvailableScenarios();
    
    if (realTimeUpdates) {
      const interval = setInterval(() => {
        updateRealtimeData();
      }, 15000); // Update every 15 seconds
      
      return () => clearInterval(interval);
    }
    
    return () => {}; // Return cleanup function for all code paths
  }, [realTimeUpdates]);

  const loadSystemStats = async () => {
    try {
      const response = await fetch('/api/admin/data-stats');
      if (response.ok) {
        const stats = await response.json();
        setSystemStats(stats);
      }
    } catch (error) {
      console.error('Failed to load system stats:', error);
    }
  };

  const loadAvailableScenarios = async () => {
    try {
      const response = await fetch('/api/admin/testing/scenarios');
      if (response.ok) {
        const data = await response.json();
        // Scenarios loaded successfully
        console.log('Scenarios loaded:', data.scenarios);
      }
    } catch (error) {
      console.error('Failed to load scenarios:', error);
    }
  };

  const updateRealtimeData = () => {
    // Simulate real-time updates
    setSystemStats(prev => ({
      ...prev,
      activeShipments: prev.activeShipments + Math.floor(Math.random() * 10 - 5),
      recentEvents: prev.recentEvents + Math.floor(Math.random() * 20),
      onTimeDeliveryRate: Math.max(90, Math.min(99, prev.onTimeDeliveryRate + (Math.random() - 0.5) * 2)),
    }));
    
    // Update API test results
    setApiTestResults(prev => prev.map(api => ({
      ...api,
      responseTime: Math.max(50, api.responseTime + Math.floor(Math.random() * 100 - 50)),
      lastTest: 'Just now',
    })));
  };

  const runScenario = async (scenarioType: string, config: any = {}) => {
    setLoading(true);
    try {
      const response = await fetch('/api/admin/testing/generate-scenario', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          scenarioType,
          intensity: config.intensity || 3,
          affectedRegions: config.affectedRegions || ['Asia', 'Europe'],
          ...config
        })
      });
      
      if (response.ok) {
        const result = await response.json();
        setActiveSimulation(result.scenario);
        setSimulationResults(prev => [result, ...prev.slice(0, 9)]);
      }
    } catch (error) {
      console.error('Failed to run scenario:', error);
    } finally {
      setLoading(false);
    }
  };

  const generateLoadTestData = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/admin/testing/load-test-data', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(loadTestConfig)
      });
      
      if (response.ok) {
        const result = await response.json();
        console.log('Load test data generation started:', result);
      }
    } catch (error) {
      console.error('Failed to generate load test data:', error);
    } finally {
      setLoading(false);
    }
  };

  const resetTestingData = async () => {
    if (window.confirm('Are you sure you want to reset all testing data? This action cannot be undone.')) {
      setLoading(true);
      try {
        const response = await fetch('/api/admin/testing/reset-data', {
          method: 'POST'
        });
        
        if (response.ok) {
          await loadSystemStats();
          setSimulationResults([]);
          setActiveSimulation(null);
        }
      } catch (error) {
        console.error('Failed to reset testing data:', error);
      } finally {
        setLoading(false);
      }
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'online': return theme.palette.success.main;
      case 'warning': return theme.palette.warning.main;
      case 'offline': return theme.palette.error.main;
      default: return theme.palette.grey[500];
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'online': return <CheckCircle />;
      case 'warning': return <Warning />;
      case 'offline': return <Error />;
      default: return <Info />;
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: '100vw', overflow: 'hidden' }}>
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Box>
            <Typography variant="h4" fontWeight="bold" gutterBottom>
              Enterprise Testing Environment
            </Typography>
            <Typography variant="subtitle1" color="text.secondary">
              SAP-Level Testing Dashboard with Advanced Scenario Simulation
            </Typography>
          </Box>
          
          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
            <FormControlLabel
              control={
                <Switch
                  checked={realTimeUpdates}
                  onChange={(e) => setRealTimeUpdates(e.target.checked)}
                />
              }
              label="Real-time Updates"
            />
            <Button
              variant="outlined"
              startIcon={<Refresh />}
              onClick={loadSystemStats}
            >
              Refresh
            </Button>
            <Button
              variant="contained"
              color="error"
              onClick={resetTestingData}
              disabled={loading}
            >
              Reset Data
            </Button>
          </Box>
        </Box>
      </motion.div>

      {/* System Health Overview */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <PremiumKPICard
            title="Total Suppliers"
            value={systemStats.totalSuppliers}
            icon={<Storage />}
            color="primary"
            trend={{ value: 5.2, direction: 'up' }}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <PremiumKPICard
            title="Active Shipments"
            value={systemStats.activeShipments}
            icon={<NetworkCheck />}
            color="success"
            trend={{ value: 2.1, direction: 'up' }}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <PremiumKPICard
            title="On-Time Rate"
            value={`${systemStats.onTimeDeliveryRate}%`}
            icon={<Timeline />}
            color="info"
            trend={{ value: 1.3, direction: 'up' }}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <PremiumKPICard
            title="Risk Score"
            value={systemStats.averageRiskScore}
            icon={<Security />}
            color="warning"
            trend={{ value: 0.2, direction: 'down' }}
          />
        </Grid>
      </Grid>

      {/* Testing Control Panel */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {/* Scenario Testing */}
        <Grid item xs={12} md={6}>
          <GlassCard>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Crisis Scenario Simulation
              </Typography>
              
              <Grid container spacing={2}>
                {[
                  { key: 'HURRICANE_DISRUPTION', label: 'Hurricane', icon: '🌀', color: 'error' },
                  { key: 'PANDEMIC_CRISIS', label: 'Pandemic', icon: '🦠', color: 'warning' },
                  { key: 'TRADE_WAR', label: 'Trade War', icon: '⚔️', color: 'info' },
                  { key: 'SUPPLIER_BANKRUPTCY', label: 'Bankruptcy', icon: '💼', color: 'error' },
                  { key: 'CYBER_ATTACK', label: 'Cyber Attack', icon: '🔒', color: 'error' },
                  { key: 'SUEZ_BLOCKAGE', label: 'Port Blockage', icon: '🚢', color: 'warning' },
                ].map((scenario) => (
                  <Grid item xs={6} key={scenario.key}>
                    <Button
                      fullWidth
                      variant="outlined"
                      color={scenario.color as any}
                      onClick={() => runScenario(scenario.key)}
                      disabled={loading}
                      startIcon={<span>{scenario.icon}</span>}
                      sx={{ height: 60 }}
                    >
                      {scenario.label}
                    </Button>
                  </Grid>
                ))}
              </Grid>
              
              {loading && (
                <Box sx={{ mt: 2 }}>
                  <LinearProgress />
                  <Typography variant="body2" textAlign="center" sx={{ mt: 1 }}>
                    Generating scenario...
                  </Typography>
                </Box>
              )}
            </CardContent>
          </GlassCard>
        </Grid>

        {/* API Testing */}
        <Grid item xs={12} md={6}>
          <GlassCard>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                External API Monitoring
              </Typography>
              
              <TableContainer component={Paper} sx={{ maxHeight: 300 }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>API</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell>Response Time</TableCell>
                      <TableCell>Last Test</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {apiTestResults.map((api, index) => (
                      <TableRow key={index}>
                        <TableCell>{api.api}</TableCell>
                        <TableCell>
                          <Chip
                            icon={getStatusIcon(api.status)}
                            label={api.status.toUpperCase()}
                            size="small"
                            sx={{ 
                              backgroundColor: alpha(getStatusColor(api.status), 0.1),
                              color: getStatusColor(api.status),
                            }}
                          />
                        </TableCell>
                        <TableCell>{api.responseTime}ms</TableCell>
                        <TableCell>{api.lastTest}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </GlassCard>
        </Grid>
      </Grid>

      {/* Load Testing Configuration */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} md={8}>
          <GlassCard>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Load Testing Configuration
              </Typography>
              
              <Grid container spacing={3}>
                <Grid item xs={6} md={3}>
                  <FormControl fullWidth>
                    <InputLabel>Suppliers</InputLabel>
                    <Select
                      value={loadTestConfig.suppliers}
                      onChange={(e) => setLoadTestConfig({...loadTestConfig, suppliers: e.target.value as number})}
                    >
                      <MenuItem value={500}>500</MenuItem>
                      <MenuItem value={1000}>1,000</MenuItem>
                      <MenuItem value={5000}>5,000</MenuItem>
                      <MenuItem value={10000}>10,000</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                
                <Grid item xs={6} md={3}>
                  <FormControl fullWidth>
                    <InputLabel>Shipments</InputLabel>
                    <Select
                      value={loadTestConfig.shipments}
                      onChange={(e) => setLoadTestConfig({...loadTestConfig, shipments: e.target.value as number})}
                    >
                      <MenuItem value={5000}>5,000</MenuItem>
                      <MenuItem value={10000}>10,000</MenuItem>
                      <MenuItem value={50000}>50,000</MenuItem>
                      <MenuItem value={100000}>100,000</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                
                <Grid item xs={6} md={3}>
                  <FormControl fullWidth>
                    <InputLabel>Events</InputLabel>
                    <Select
                      value={loadTestConfig.events}
                      onChange={(e) => setLoadTestConfig({...loadTestConfig, events: e.target.value as number})}
                    >
                      <MenuItem value={50000}>50,000</MenuItem>
                      <MenuItem value={100000}>100,000</MenuItem>
                      <MenuItem value={500000}>500,000</MenuItem>
                      <MenuItem value={1000000}>1,000,000</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>
                
                <Grid item xs={6} md={3}>
                  <Button
                    fullWidth
                    variant="contained"
                    color="primary"
                    onClick={generateLoadTestData}
                    disabled={loading}
                    startIcon={<Speed />}
                    sx={{ height: 56 }}
                  >
                    Generate
                  </Button>
                </Grid>
              </Grid>
            </CardContent>
          </GlassCard>
        </Grid>

        {/* Active Simulation Status */}
        <Grid item xs={12} md={4}>
          <GlassCard>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Active Simulation
              </Typography>
              
              {activeSimulation ? (
                <Box>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    {activeSimulation.scenarioName || 'Crisis Simulation'}
                  </Typography>
                  <Typography variant="body1" gutterBottom>
                    Intensity: {activeSimulation.intensity}/5
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    Started: {new Date(activeSimulation.generatedAt || activeSimulation.startTime).toLocaleTimeString()}
                  </Typography>
                  
                  <Button
                    variant="outlined"
                    color="error"
                    size="small"
                    startIcon={<Stop />}
                    onClick={() => setActiveSimulation(null)}
                    sx={{ mt: 2 }}
                  >
                    Stop Simulation
                  </Button>
                </Box>
              ) : (
                <Box textAlign="center" py={3}>
                  <Typography variant="body2" color="text.secondary">
                    No active simulation
                  </Typography>
                  <Button
                    variant="outlined"
                    size="small"
                    onClick={() => console.log('Start scenario clicked')}
                    sx={{ mt: 1 }}
                  >
                    Start Scenario
                  </Button>
                </Box>
              )}
            </CardContent>
          </GlassCard>
        </Grid>
      </Grid>

      {/* Real-time Metrics Chart */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <GlassCard>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Real-time System Performance
              </Typography>
              
              <PremiumLineChart
                data={[
                  { time: '10:00', throughput: 1450, responseTime: 245, errors: 2 },
                  { time: '10:15', throughput: 1520, responseTime: 234, errors: 1 },
                  { time: '10:30', throughput: 1480, responseTime: 267, errors: 3 },
                  { time: '10:45', throughput: 1600, responseTime: 223, errors: 1 },
                  { time: '11:00', throughput: 1580, responseTime: 245, errors: 2 },
                ]}
                xKey="time"
                yKeys={["throughput"]}
                title="System Throughput"
                height={300}
                loading={false}
                variant="premium"
                animation={true}
              />
            </CardContent>
          </GlassCard>
        </Grid>

        <Grid item xs={12} md={4}>
          <GlassCard>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Recent Test Results
              </Typography>
              
              <Box sx={{ maxHeight: 300, overflowY: 'auto' }}>
                {simulationResults.slice(0, 5).map((result, index) => (
                  <Box
                    key={index}
                    sx={{
                      p: 2,
                      mb: 2,
                      border: 1,
                      borderColor: 'divider',
                      borderRadius: 1,
                      backgroundColor: alpha(theme.palette.primary.main, 0.05),
                    }}
                  >
                    <Typography variant="body2" fontWeight="bold">
                      {result.scenario?.scenarioName || 'Test Scenario'}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      Intensity: {result.scenario?.intensity}/5
                    </Typography>
                    <Typography variant="caption" display="block">
                      {new Date(result.scenario?.generatedAt).toLocaleString()}
                    </Typography>
                  </Box>
                ))}
              </Box>
            </CardContent>
          </GlassCard>
        </Grid>
      </Grid>

      {/* Floating Action Button */}
      <EnhancedFloatingActionButton
        icon={<Assessment />}
        onClick={() => console.log('Assessment clicked')}
        color="primary"
        size="medium"
      />
    </Box>
  );
};

export default AdminTestingDashboard;