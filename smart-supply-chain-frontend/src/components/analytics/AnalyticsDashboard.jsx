import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Chip,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Alert,
  CircularProgress,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  TrendingUp,
  Assessment,
  Speed,
  Warning,
  Refresh,
  Timeline,
  Analytics
} from '@mui/icons-material';
import RiskTrendChart from './RiskTrendChart';
import SupplierPerformanceMatrix from './SupplierPerformanceMatrix';
import mlService from '../../services/mlService';

/**
 * Enhanced Analytics Dashboard with ML-powered insights
 * Executive-level reporting with predictive analytics
 */
const AnalyticsDashboard = () => {
  const [timeRange, setTimeRange] = useState('30d');
  const [loading, setLoading] = useState(true);
  const [mlHealth, setMlHealth] = useState(null);
  const [analyticsData, setAnalyticsData] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadDashboardData();
    checkMLServiceHealth();
  }, [timeRange]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [analytics, health] = await Promise.all([
        mlService.getPredictiveAnalytics({ timeRange }),
        mlService.getMLServiceHealth()
      ]);
      
      setAnalyticsData(analytics);
      setMlHealth(health);
    } catch (error) {
      console.error('Error loading dashboard data:', error);
      setAnalyticsData(generateFallbackAnalytics());
      setMlHealth({ ml_service_healthy: false, status: 'DOWN' });
    } finally {
      setLoading(false);
    }
  };

  const checkMLServiceHealth = async () => {
    try {
      const health = await mlService.getMLServiceHealth();
      setMlHealth(health);
    } catch (error) {
      setMlHealth({ ml_service_healthy: false, status: 'DOWN' });
    }
  };

  const handleRefresh = async () => {
    setRefreshing(true);
    await loadDashboardData();
    setRefreshing(false);
  };

  const generateFallbackAnalytics = () => ({
    totalShipments: 2847,
    riskDistribution: {
      low: 45,
      medium: 35,
      high: 15,
      critical: 5
    },
    avgDelayPrediction: 4.2,
    supplierAnomalies: 3,
    predictions: {
      nextWeekRisk: 42.5,
      delayProbability: 18.3,
      costImpact: 125000
    },
    performanceMetrics: {
      onTimeDelivery: 87.3,
      qualityScore: 8.6,
      customerSatisfaction: 92.1
    },
    topRisks: [
      { type: 'Weather Disruption', probability: 65, impact: 'High' },
      { type: 'Supplier Capacity', probability: 45, impact: 'Medium' },
      { type: 'Transportation Delay', probability: 38, impact: 'Medium' }
    ]
  });

  const getRiskColor = (level) => {
    const colors = {
      low: 'success',
      medium: 'warning',
      high: 'error',
      critical: 'error'
    };
    return colors[level] || 'default';
  };

  const formatNumber = (num) => {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="400px">
        <CircularProgress size={60} />
      </Box>
    );
  }

  return (
    <Box p={3}>
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1" gutterBottom>
          Supply Chain Analytics Dashboard
        </Typography>
        
        <Box display="flex" alignItems="center" gap={2}>
          {/* ML Service Status */}
          <Chip
            icon={mlHealth?.ml_service_healthy ? <Analytics /> : <Warning />}
            label={`ML Service: ${mlHealth?.status || 'Unknown'}`}
            color={mlHealth?.ml_service_healthy ? 'success' : 'error'}
            variant="outlined"
          />
          
          {/* Time Range Selector */}
          <FormControl size="small" style={{ minWidth: 120 }}>
            <InputLabel>Time Range</InputLabel>
            <Select
              value={timeRange}
              label="Time Range"
              onChange={(e) => setTimeRange(e.target.value)}
            >
              <MenuItem value="7d">Last 7 days</MenuItem>
              <MenuItem value="30d">Last 30 days</MenuItem>
              <MenuItem value="90d">Last 90 days</MenuItem>
            </Select>
          </FormControl>
          
          {/* Refresh Button */}
          <Tooltip title="Refresh Data">
            <IconButton onClick={handleRefresh} disabled={refreshing}>
              <Refresh className={refreshing ? 'rotating' : ''} />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* ML Service Alert */}
      {!mlHealth?.ml_service_healthy && (
        <Alert severity="warning" sx={{ mb: 3 }}>
          ML prediction service is unavailable. Dashboard showing cached data and fallback predictions.
        </Alert>
      )}

      {/* Key Metrics Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Total Shipments
                  </Typography>
                  <Typography variant="h4">
                    {formatNumber(analyticsData?.totalShipments || 0)}
                  </Typography>
                </Box>
                <TrendingUp color="primary" fontSize="large" />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Avg Delay Prediction
                  </Typography>
                  <Typography variant="h4">
                    {analyticsData?.avgDelayPrediction?.toFixed(1) || '0.0'}h
                  </Typography>
                </Box>
                <Speed color="warning" fontSize="large" />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    Supplier Anomalies
                  </Typography>
                  <Typography variant="h4">
                    {analyticsData?.supplierAnomalies || 0}
                  </Typography>
                </Box>
                <Warning color="error" fontSize="large" />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography color="textSecondary" gutterBottom>
                    On-time Delivery
                  </Typography>
                  <Typography variant="h4">
                    {analyticsData?.performanceMetrics?.onTimeDelivery?.toFixed(1) || '0.0'}%
                  </Typography>
                </Box>
                <Assessment color="success" fontSize="large" />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Risk Distribution */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Risk Distribution
              </Typography>
              <Grid container spacing={2}>
                {Object.entries(analyticsData?.riskDistribution || {}).map(([level, count]) => (
                  <Grid item xs={6} sm={3} key={level}>
                    <Box textAlign="center">
                      <Typography variant="h5" color={getRiskColor(level)}>
                        {count}%
                      </Typography>
                      <Chip
                        label={level.toUpperCase()}
                        color={getRiskColor(level)}
                        size="small"
                        variant="outlined"
                      />
                    </Box>
                  </Grid>
                ))}
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Predictive Insights
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <Box display="flex" justifyContent="space-between">
                    <Typography variant="body2">Next Week Risk Score:</Typography>
                    <Typography variant="h6" color="warning.main">
                      {analyticsData?.predictions?.nextWeekRisk?.toFixed(1) || '0.0'}
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12}>
                  <Box display="flex" justifyContent="space-between">
                    <Typography variant="body2">Delay Probability:</Typography>
                    <Typography variant="h6" color="error.main">
                      {analyticsData?.predictions?.delayProbability?.toFixed(1) || '0.0'}%
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12}>
                  <Box display="flex" justifyContent="space-between">
                    <Typography variant="body2">Potential Cost Impact:</Typography>
                    <Typography variant="h6" color="error.main">
                      ${formatNumber(analyticsData?.predictions?.costImpact || 0)}
                    </Typography>
                  </Box>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Top Risks */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Top Risk Factors
              </Typography>
              <Grid container spacing={2}>
                {(analyticsData?.topRisks || []).map((risk, index) => (
                  <Grid item xs={12} md={4} key={index}>
                    <Box p={2} border={1} borderColor="grey.300" borderRadius={1}>
                      <Typography variant="subtitle1" fontWeight="bold">
                        {risk.type}
                      </Typography>
                      <Box display="flex" justifyContent="space-between" mt={1}>
                        <Typography variant="body2" color="textSecondary">
                          Probability: {risk.probability}%
                        </Typography>
                        <Chip
                          label={risk.impact}
                          size="small"
                          color={risk.impact === 'High' ? 'error' : 'warning'}
                          variant="outlined"
                        />
                      </Box>
                    </Box>
                  </Grid>
                ))}
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Advanced Charts */}
      <Grid container spacing={3}>
        <Grid item xs={12}>
          <RiskTrendChart timeRange={timeRange} height={400} />
        </Grid>
        
        <Grid item xs={12}>
          <SupplierPerformanceMatrix height={500} />
        </Grid>
      </Grid>
    </Box>
  );
};

export default AnalyticsDashboard;

// Add rotating animation for refresh button
const styles = `
  @keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
  }
  
  .rotating {
    animation: rotate 1s linear infinite;
  }
`;

// Inject styles
if (typeof document !== 'undefined') {
  const styleSheet = document.createElement('style');
  styleSheet.textContent = styles;
  document.head.appendChild(styleSheet);
}