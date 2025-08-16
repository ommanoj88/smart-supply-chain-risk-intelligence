import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Button,
  LinearProgress,
  Alert,
  Tabs,
  Tab,
} from '@mui/material';
import {
  Assessment,
  Speed,
  CheckCircle,
  Error,
  Warning,
  Download,
  Refresh,
  Timeline,
  TrendingUp,
  TrendingDown,
  BarChart,
} from '@mui/icons-material';
import { motion } from 'framer-motion';

interface TestResult {
  id: string;
  name: string;
  type: string;
  status: 'completed' | 'running' | 'failed';
  startTime: Date;
  endTime?: Date;
  duration: number;
  metrics: {
    responseTime: number;
    throughput: number;
    errorRate: number;
    successRate: number;
  };
}

interface SystemMetrics {
  cpuUsage: number;
  memoryUsage: number;
  diskUsage: number;
  networkLatency: number;
}

const TestingResults: React.FC = () => {
  const [selectedTab, setSelectedTab] = useState(0);
  const [testResults, setTestResults] = useState<TestResult[]>([
    {
      id: '1',
      name: 'Category 4 Hurricane Simulation',
      type: 'crisis_scenario',
      status: 'completed',
      startTime: new Date(Date.now() - 3600000),
      endTime: new Date(Date.now() - 1800000),
      duration: 1800,
      metrics: {
        responseTime: 245,
        throughput: 1250,
        errorRate: 2.1,
        successRate: 97.9
      }
    },
    {
      id: '2',
      name: 'Load Test - 1000 Suppliers',
      type: 'load_test',
      status: 'completed',
      startTime: new Date(Date.now() - 7200000),
      endTime: new Date(Date.now() - 6300000),
      duration: 900,
      metrics: {
        responseTime: 189,
        throughput: 2450,
        errorRate: 0.8,
        successRate: 99.2
      }
    },
    {
      id: '3',
      name: 'API Integration Test',
      type: 'api_test',
      status: 'running',
      startTime: new Date(Date.now() - 300000),
      duration: 300,
      metrics: {
        responseTime: 334,
        throughput: 890,
        errorRate: 5.2,
        successRate: 94.8
      }
    },
    {
      id: '4',
      name: 'Trade War Impact Analysis',
      type: 'crisis_scenario',
      status: 'failed',
      startTime: new Date(Date.now() - 10800000),
      endTime: new Date(Date.now() - 9900000),
      duration: 900,
      metrics: {
        responseTime: 0,
        throughput: 0,
        errorRate: 100,
        successRate: 0
      }
    }
  ]);

  const [systemMetrics, setSystemMetrics] = useState<SystemMetrics>({
    cpuUsage: 45.2,
    memoryUsage: 67.8,
    diskUsage: 34.1,
    networkLatency: 23.4
  });

  const [performanceData] = useState([
    { time: '10:00', throughput: 1200, responseTime: 245, errorRate: 2.1 },
    { time: '10:15', throughput: 1350, responseTime: 234, errorRate: 1.8 },
    { time: '10:30', throughput: 1480, responseTime: 267, errorRate: 3.2 },
    { time: '10:45', throughput: 1600, responseTime: 223, errorRate: 1.5 },
    { time: '11:00', throughput: 1580, responseTime: 245, errorRate: 2.0 },
    { time: '11:15', throughput: 1420, responseTime: 298, errorRate: 4.1 },
    { time: '11:30', throughput: 1250, responseTime: 334, errorRate: 5.2 }
  ]);

  useEffect(() => {
    const interval = setInterval(() => {
      // Update system metrics
      setSystemMetrics(prev => ({
        cpuUsage: Math.max(20, Math.min(90, prev.cpuUsage + (Math.random() - 0.5) * 10)),
        memoryUsage: Math.max(30, Math.min(95, prev.memoryUsage + (Math.random() - 0.5) * 5)),
        diskUsage: Math.max(20, Math.min(80, prev.diskUsage + (Math.random() - 0.5) * 2)),
        networkLatency: Math.max(10, Math.min(100, prev.networkLatency + (Math.random() - 0.5) * 5))
      }));

      // Update running tests
      setTestResults(prev => prev.map(test => {
        if (test.status === 'running') {
          return {
            ...test,
            duration: test.duration + 5,
            metrics: {
              ...test.metrics,
              responseTime: Math.max(100, test.metrics.responseTime + (Math.random() - 0.5) * 50),
              throughput: Math.max(500, test.metrics.throughput + (Math.random() - 0.5) * 100),
              errorRate: Math.max(0, Math.min(10, test.metrics.errorRate + (Math.random() - 0.5) * 2)),
              successRate: 100 - test.metrics.errorRate
            }
          };
        }
        return test;
      }));
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'completed':
        return 'success';
      case 'running':
        return 'info';
      case 'failed':
        return 'error';
      default:
        return 'default';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'completed':
        return <CheckCircle />;
      case 'running':
        return <Timeline />;
      case 'failed':
        return <Error />;
      default:
        return <Warning />;
    }
  };

  const formatDuration = (seconds: number) => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}m ${remainingSeconds}s`;
  };

  const exportResults = () => {
    const csvContent = testResults.map(test => 
      `${test.name},${test.type},${test.status},${test.metrics.responseTime},${test.metrics.throughput},${test.metrics.errorRate}`
    ).join('\n');
    
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'testing-results.csv';
    a.click();
  };

  const getMetricTrend = (value: number, threshold: number) => {
    if (value > threshold) return { icon: <TrendingUp color="error" />, color: 'error' };
    return { icon: <TrendingDown color="success" />, color: 'success' };
  };

  return (
    <Box>
      {/* Performance Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <Card elevation={2}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Speed color="primary" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h5">
                  {Math.round(performanceData[performanceData.length - 1]?.responseTime || 0)}ms
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Avg Response Time
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 1 }}>
                  {getMetricTrend(245, 300).icon}
                  <Typography variant="caption" color={getMetricTrend(245, 300).color} sx={{ ml: 0.5 }}>
                    -5.2%
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.1 }}
          >
            <Card elevation={2}>
              <CardContent sx={{ textAlign: 'center' }}>
                <BarChart color="primary" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h5">
                  {Math.round(performanceData[performanceData.length - 1]?.throughput || 0)}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Requests/min
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 1 }}>
                  {getMetricTrend(1250, 1000).icon}
                  <Typography variant="caption" color={getMetricTrend(1250, 1000).color} sx={{ ml: 0.5 }}>
                    +8.3%
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.2 }}
          >
            <Card elevation={2}>
              <CardContent sx={{ textAlign: 'center' }}>
                <CheckCircle color="success" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h5">
                  {(100 - (performanceData[performanceData.length - 1]?.errorRate || 0)).toFixed(1)}%
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Success Rate
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 1 }}>
                  {getMetricTrend(5.2, 3).icon}
                  <Typography variant="caption" color={getMetricTrend(5.2, 3).color} sx={{ ml: 0.5 }}>
                    +2.1%
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            <Card elevation={2}>
              <CardContent sx={{ textAlign: 'center' }}>
                <Assessment color="primary" sx={{ fontSize: 40, mb: 1 }} />
                <Typography variant="h5">
                  {testResults.filter(t => t.status === 'completed').length}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Tests Completed
                </Typography>
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', mt: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    of {testResults.length} total
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </motion.div>
        </Grid>
      </Grid>

      {/* Main Content */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.4 }}
      >
        <Card elevation={2}>
          <CardContent>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6">Testing Results Dashboard</Typography>
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Button
                  variant="outlined"
                  startIcon={<Refresh />}
                  size="small"
                >
                  Refresh
                </Button>
                <Button
                  variant="outlined"
                  startIcon={<Download />}
                  onClick={exportResults}
                  size="small"
                >
                  Export
                </Button>
              </Box>
            </Box>

            <Tabs value={selectedTab} onChange={(_, newValue) => setSelectedTab(newValue)} sx={{ mb: 3 }}>
              <Tab label="Test Results" />
              <Tab label="System Metrics" />
              <Tab label="Performance Trends" />
            </Tabs>

            {/* Test Results Tab */}
            {selectedTab === 0 && (
              <TableContainer component={Paper} variant="outlined">
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Test Name</TableCell>
                      <TableCell>Type</TableCell>
                      <TableCell align="center">Status</TableCell>
                      <TableCell align="right">Duration</TableCell>
                      <TableCell align="right">Response Time</TableCell>
                      <TableCell align="right">Throughput</TableCell>
                      <TableCell align="right">Success Rate</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {testResults.map((test) => (
                      <TableRow key={test.id} hover>
                        <TableCell>
                          <Box>
                            <Typography variant="body2" fontWeight="medium">
                              {test.name}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              Started: {test.startTime.toLocaleTimeString()}
                            </Typography>
                          </Box>
                        </TableCell>
                        <TableCell>
                          <Chip
                            label={test.type.replace('_', ' ').toUpperCase()}
                            size="small"
                            variant="outlined"
                          />
                        </TableCell>
                        <TableCell align="center">
                          <Chip
                            icon={getStatusIcon(test.status)}
                            label={test.status.toUpperCase()}
                            color={getStatusColor(test.status)}
                            size="small"
                          />
                        </TableCell>
                        <TableCell align="right">
                          {test.status === 'running' ? (
                            <Box>
                              <Typography variant="body2">
                                {formatDuration(test.duration)}
                              </Typography>
                              <LinearProgress sx={{ width: 60 }} />
                            </Box>
                          ) : (
                            formatDuration(test.duration)
                          )}
                        </TableCell>
                        <TableCell align="right">
                          <Typography variant="body2">
                            {test.metrics.responseTime > 0 ? `${Math.round(test.metrics.responseTime)}ms` : 'N/A'}
                          </Typography>
                        </TableCell>
                        <TableCell align="right">
                          <Typography variant="body2">
                            {test.metrics.throughput > 0 ? `${Math.round(test.metrics.throughput)}/min` : 'N/A'}
                          </Typography>
                        </TableCell>
                        <TableCell align="right">
                          <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
                            <Typography variant="body2" sx={{ mr: 1 }}>
                              {test.metrics.successRate.toFixed(1)}%
                            </Typography>
                            <LinearProgress
                              variant="determinate"
                              value={test.metrics.successRate}
                              sx={{ width: 50, height: 4 }}
                              color={test.metrics.successRate > 95 ? 'success' : test.metrics.successRate > 90 ? 'warning' : 'error'}
                            />
                          </Box>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}

            {/* System Metrics Tab */}
            {selectedTab === 1 && (
              <Grid container spacing={3}>
                <Grid item xs={12} sm={6}>
                  <Card variant="outlined">
                    <CardContent>
                      <Typography variant="subtitle1" gutterBottom>
                        CPU Usage
                      </Typography>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <LinearProgress
                          variant="determinate"
                          value={systemMetrics.cpuUsage}
                          sx={{ flexGrow: 1, mr: 1, height: 8 }}
                          color={systemMetrics.cpuUsage > 80 ? 'error' : systemMetrics.cpuUsage > 60 ? 'warning' : 'success'}
                        />
                        <Typography variant="body2" sx={{ minWidth: 50 }}>
                          {systemMetrics.cpuUsage.toFixed(1)}%
                        </Typography>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Card variant="outlined">
                    <CardContent>
                      <Typography variant="subtitle1" gutterBottom>
                        Memory Usage
                      </Typography>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <LinearProgress
                          variant="determinate"
                          value={systemMetrics.memoryUsage}
                          sx={{ flexGrow: 1, mr: 1, height: 8 }}
                          color={systemMetrics.memoryUsage > 80 ? 'error' : systemMetrics.memoryUsage > 60 ? 'warning' : 'success'}
                        />
                        <Typography variant="body2" sx={{ minWidth: 50 }}>
                          {systemMetrics.memoryUsage.toFixed(1)}%
                        </Typography>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Card variant="outlined">
                    <CardContent>
                      <Typography variant="subtitle1" gutterBottom>
                        Disk Usage
                      </Typography>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <LinearProgress
                          variant="determinate"
                          value={systemMetrics.diskUsage}
                          sx={{ flexGrow: 1, mr: 1, height: 8 }}
                          color={systemMetrics.diskUsage > 80 ? 'error' : systemMetrics.diskUsage > 60 ? 'warning' : 'success'}
                        />
                        <Typography variant="body2" sx={{ minWidth: 50 }}>
                          {systemMetrics.diskUsage.toFixed(1)}%
                        </Typography>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Card variant="outlined">
                    <CardContent>
                      <Typography variant="subtitle1" gutterBottom>
                        Network Latency
                      </Typography>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <LinearProgress
                          variant="determinate"
                          value={Math.min(100, systemMetrics.networkLatency)}
                          sx={{ flexGrow: 1, mr: 1, height: 8 }}
                          color={systemMetrics.networkLatency > 50 ? 'error' : systemMetrics.networkLatency > 30 ? 'warning' : 'success'}
                        />
                        <Typography variant="body2" sx={{ minWidth: 50 }}>
                          {systemMetrics.networkLatency.toFixed(1)}ms
                        </Typography>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>
            )}

            {/* Performance Trends Tab */}
            {selectedTab === 2 && (
              <Box>
                <Alert severity="info" sx={{ mb: 2 }}>
                  Performance trends over the last testing session
                </Alert>
                <Typography variant="body2" color="text.secondary">
                  Advanced charting functionality would be implemented here using the existing PremiumLineChart component
                  to show performance trends over time.
                </Typography>
              </Box>
            )}
          </CardContent>
        </Card>
      </motion.div>
    </Box>
  );
};

export default TestingResults;