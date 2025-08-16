import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Box,
  Chip,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Switch,
  FormControlLabel,
  Slider,
  Alert,
  LinearProgress,
  useTheme,
  alpha,
} from '@mui/material';
import {
  CheckCircle,
  Error,
  Warning,
  Api,
  Speed,
  Refresh,
  Settings,
} from '@mui/icons-material';
import { motion } from 'framer-motion';

interface APIStatus {
  name: string;
  status: 'online' | 'offline' | 'warning' | 'error';
  responseTime: number;
  successRate: number;
  lastTest: string;
  endpoint: string;
  enabled: boolean;
}

interface SimulationSettings {
  responseDelay: number;
  errorRate: number;
  timeoutRate: number;
  enableChaos: boolean;
}

const APISimulationDashboard: React.FC = () => {
  const theme = useTheme();
  const [apiStatuses, setApiStatuses] = useState<APIStatus[]>([
    {
      name: 'DHL Tracking API',
      status: 'online',
      responseTime: 245,
      successRate: 98.5,
      lastTest: '30 seconds ago',
      endpoint: '/mock-apis/carriers/dhl/tracking',
      enabled: true
    },
    {
      name: 'FedEx Shipping API',
      status: 'online',
      responseTime: 189,
      successRate: 99.2,
      lastTest: '45 seconds ago',
      endpoint: '/mock-apis/carriers/fedex/shipping',
      enabled: true
    },
    {
      name: 'Maersk Container API',
      status: 'warning',
      responseTime: 1240,
      successRate: 94.8,
      lastTest: '2 minutes ago',
      endpoint: '/mock-apis/carriers/maersk/containers',
      enabled: true
    },
    {
      name: 'SAP Digital Manufacturing',
      status: 'online',
      responseTime: 334,
      successRate: 97.1,
      lastTest: '1 minute ago',
      endpoint: '/mock-apis/enterprise/sap/manufacturing',
      enabled: true
    },
    {
      name: 'Oracle ERP Cloud',
      status: 'online',
      responseTime: 178,
      successRate: 99.8,
      lastTest: '15 seconds ago',
      endpoint: '/mock-apis/enterprise/oracle/erp',
      enabled: true
    },
    {
      name: 'Weather Service API',
      status: 'online',
      responseTime: 89,
      successRate: 99.9,
      lastTest: '1 minute ago',
      endpoint: '/mock-apis/weather/current',
      enabled: true
    },
    {
      name: 'Currency Exchange API',
      status: 'error',
      responseTime: 0,
      successRate: 0,
      lastTest: '5 minutes ago',
      endpoint: '/mock-apis/enterprise/currency/rates',
      enabled: false
    },
    {
      name: 'Customs API',
      status: 'online',
      responseTime: 567,
      successRate: 96.3,
      lastTest: '3 minutes ago',
      endpoint: '/mock-apis/enterprise/customs/tariffs',
      enabled: true
    }
  ]);

  const [simulationSettings, setSimulationSettings] = useState<SimulationSettings>({
    responseDelay: 100,
    errorRate: 5,
    timeoutRate: 2,
    enableChaos: false
  });

  const [monitoring, setMonitoring] = useState(true);
  const [liveMetrics, setLiveMetrics] = useState({
    totalRequests: 15420,
    successfulRequests: 14987,
    failedRequests: 433,
    averageResponseTime: 245
  });

  useEffect(() => {
    if (monitoring) {
      const interval = setInterval(() => {
        updateAPIStatuses();
        updateLiveMetrics();
      }, 5000);

      return () => clearInterval(interval);
    }
    return undefined;
  }, [monitoring]);

  const updateAPIStatuses = () => {
    setApiStatuses(prev => prev.map(api => ({
      ...api,
      responseTime: api.enabled ? Math.max(50, api.responseTime + (Math.random() - 0.5) * 100) : 0,
      successRate: api.enabled ? Math.max(90, Math.min(100, api.successRate + (Math.random() - 0.5) * 2)) : 0,
      lastTest: api.enabled ? 'Just now' : api.lastTest,
      status: api.enabled ? (api.responseTime > 1000 ? 'warning' : 'online') : 'offline'
    })));
  };

  const updateLiveMetrics = () => {
    setLiveMetrics(prev => ({
      totalRequests: prev.totalRequests + Math.floor(Math.random() * 20),
      successfulRequests: prev.successfulRequests + Math.floor(Math.random() * 18),
      failedRequests: prev.failedRequests + Math.floor(Math.random() * 3),
      averageResponseTime: Math.max(100, prev.averageResponseTime + (Math.random() - 0.5) * 50)
    }));
  };

  const toggleAPIStatus = async (index: number) => {
    const api = apiStatuses[index];
    try {
      const response = await fetch('/mock-apis/enterprise/simulation/configure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          endpoint: api.endpoint,
          enabled: !api.enabled
        })
      });

      if (response.ok) {
        setApiStatuses(prev => prev.map((item, i) => 
          i === index ? { ...item, enabled: !item.enabled } : item
        ));
      }
    } catch (error) {
      console.error('Failed to toggle API status:', error);
    }
  };

  const updateSimulationSettings = async () => {
    try {
      const response = await fetch('/mock-apis/enterprise/simulation/configure', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(simulationSettings)
      });

      if (response.ok) {
        console.log('Simulation settings updated');
      }
    } catch (error) {
      console.error('Failed to update simulation settings:', error);
    }
  };

  const testAllAPIs = async () => {
    setApiStatuses(prev => prev.map(api => ({ ...api, lastTest: 'Testing...' })));
    
    // Simulate testing all APIs
    setTimeout(() => {
      updateAPIStatuses();
    }, 2000);
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'online':
        return <CheckCircle color="success" />;
      case 'warning':
        return <Warning color="warning" />;
      case 'error':
      case 'offline':
        return <Error color="error" />;
      default:
        return <CheckCircle />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'online':
        return theme.palette.success.main;
      case 'warning':
        return theme.palette.warning.main;
      case 'error':
      case 'offline':
        return theme.palette.error.main;
      default:
        return theme.palette.grey[500];
    }
  };

  return (
    <Grid container spacing={3}>
      {/* Live Metrics */}
      <Grid item xs={12}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6} md={3}>
              <Card elevation={2}>
                <CardContent sx={{ textAlign: 'center' }}>
                  <Api color="primary" sx={{ fontSize: 40, mb: 1 }} />
                  <Typography variant="h5">{liveMetrics.totalRequests.toLocaleString()}</Typography>
                  <Typography variant="body2" color="text.secondary">Total Requests</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card elevation={2}>
                <CardContent sx={{ textAlign: 'center' }}>
                  <CheckCircle color="success" sx={{ fontSize: 40, mb: 1 }} />
                  <Typography variant="h5">{liveMetrics.successfulRequests.toLocaleString()}</Typography>
                  <Typography variant="body2" color="text.secondary">Successful</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card elevation={2}>
                <CardContent sx={{ textAlign: 'center' }}>
                  <Error color="error" sx={{ fontSize: 40, mb: 1 }} />
                  <Typography variant="h5">{liveMetrics.failedRequests.toLocaleString()}</Typography>
                  <Typography variant="body2" color="text.secondary">Failed</Typography>
                </CardContent>
              </Card>
            </Grid>
            <Grid item xs={12} sm={6} md={3}>
              <Card elevation={2}>
                <CardContent sx={{ textAlign: 'center' }}>
                  <Speed color="info" sx={{ fontSize: 40, mb: 1 }} />
                  <Typography variant="h5">{Math.round(liveMetrics.averageResponseTime)}ms</Typography>
                  <Typography variant="body2" color="text.secondary">Avg Response</Typography>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </motion.div>
      </Grid>

      {/* API Status Table */}
      <Grid item xs={12} lg={8}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
        >
          <Card elevation={2}>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6">API Status Monitor</Typography>
                <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={monitoring}
                        onChange={(e) => setMonitoring(e.target.checked)}
                        color="primary"
                      />
                    }
                    label="Live Monitoring"
                  />
                  <Button
                    variant="outlined"
                    startIcon={<Refresh />}
                    onClick={testAllAPIs}
                    size="small"
                  >
                    Test All
                  </Button>
                </Box>
              </Box>

              <TableContainer component={Paper} variant="outlined">
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>API Service</TableCell>
                      <TableCell align="center">Status</TableCell>
                      <TableCell align="right">Response Time</TableCell>
                      <TableCell align="right">Success Rate</TableCell>
                      <TableCell align="center">Last Test</TableCell>
                      <TableCell align="center">Control</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {apiStatuses.map((api, index) => (
                      <TableRow key={api.name} hover>
                        <TableCell>
                          <Box>
                            <Typography variant="body2" fontWeight="medium">
                              {api.name}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              {api.endpoint}
                            </Typography>
                          </Box>
                        </TableCell>
                        <TableCell align="center">
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
                        <TableCell align="right">
                          <Typography variant="body2">
                            {api.enabled ? `${Math.round(api.responseTime)}ms` : 'N/A'}
                          </Typography>
                        </TableCell>
                        <TableCell align="right">
                          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
                            <Typography variant="body2" sx={{ mr: 1 }}>
                              {api.enabled ? `${api.successRate.toFixed(1)}%` : 'N/A'}
                            </Typography>
                            {api.enabled && (
                              <LinearProgress
                                variant="determinate"
                                value={api.successRate}
                                sx={{ width: 50, height: 4 }}
                              />
                            )}
                          </Box>
                        </TableCell>
                        <TableCell align="center">
                          <Typography variant="caption">
                            {api.lastTest}
                          </Typography>
                        </TableCell>
                        <TableCell align="center">
                          <Switch
                            checked={api.enabled}
                            onChange={() => toggleAPIStatus(index)}
                            size="small"
                            color="primary"
                          />
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </CardContent>
          </Card>
        </motion.div>
      </Grid>

      {/* Simulation Settings */}
      <Grid item xs={12} lg={4}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <Card elevation={2}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Settings color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Simulation Settings</Typography>
              </Box>

              <Box sx={{ mb: 3 }}>
                <Typography variant="body2" gutterBottom>
                  Response Delay: {simulationSettings.responseDelay}ms
                </Typography>
                <Slider
                  value={simulationSettings.responseDelay}
                  onChange={(_, value) => setSimulationSettings(prev => ({ ...prev, responseDelay: value as number }))}
                  min={0}
                  max={2000}
                  step={50}
                  marks={[
                    { value: 0, label: '0ms' },
                    { value: 1000, label: '1s' },
                    { value: 2000, label: '2s' },
                  ]}
                />
              </Box>

              <Box sx={{ mb: 3 }}>
                <Typography variant="body2" gutterBottom>
                  Error Rate: {simulationSettings.errorRate}%
                </Typography>
                <Slider
                  value={simulationSettings.errorRate}
                  onChange={(_, value) => setSimulationSettings(prev => ({ ...prev, errorRate: value as number }))}
                  min={0}
                  max={50}
                  step={1}
                  marks={[
                    { value: 0, label: '0%' },
                    { value: 25, label: '25%' },
                    { value: 50, label: '50%' },
                  ]}
                />
              </Box>

              <Box sx={{ mb: 3 }}>
                <Typography variant="body2" gutterBottom>
                  Timeout Rate: {simulationSettings.timeoutRate}%
                </Typography>
                <Slider
                  value={simulationSettings.timeoutRate}
                  onChange={(_, value) => setSimulationSettings(prev => ({ ...prev, timeoutRate: value as number }))}
                  min={0}
                  max={20}
                  step={1}
                  marks={[
                    { value: 0, label: '0%' },
                    { value: 10, label: '10%' },
                    { value: 20, label: '20%' },
                  ]}
                />
              </Box>

              <FormControlLabel
                control={
                  <Switch
                    checked={simulationSettings.enableChaos}
                    onChange={(e) => setSimulationSettings(prev => ({ ...prev, enableChaos: e.target.checked }))}
                    color="warning"
                  />
                }
                label="Enable Chaos Engineering"
                sx={{ mb: 3 }}
              />

              {simulationSettings.enableChaos && (
                <Alert severity="warning" sx={{ mb: 2 }}>
                  Chaos engineering mode will randomly introduce failures and delays
                </Alert>
              )}

              <Button
                fullWidth
                variant="contained"
                onClick={updateSimulationSettings}
                startIcon={<Settings />}
              >
                Apply Settings
              </Button>
            </CardContent>
          </Card>
        </motion.div>
      </Grid>
    </Grid>
  );
};

export default APISimulationDashboard;