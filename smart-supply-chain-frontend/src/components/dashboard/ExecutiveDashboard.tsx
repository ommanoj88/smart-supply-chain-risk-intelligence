import React from 'react';
import {
  Grid,
  Box,
  Typography,
  IconButton,
  Button,
  useTheme,
} from '@mui/material';
import {
  Refresh,
  TrendingUp,
  Inventory,
  LocalShipping,
  Warning,
  Assessment,
  AccountBalance,
  Speed,
  Security,
} from '@mui/icons-material';
import { useSelector, useDispatch } from 'react-redux';
import MetricCard from '../common/MetricCard';
import { 
  EnhancedAreaChart, 
  EnhancedBarChart, 
  EnhancedLineChart, 
  EnhancedPieChart 
} from '../common/EnhancedCharts';
import { 
  selectDashboardMetrics, 
  selectTimeRange,
  selectLastRefresh,
  refreshDashboard,
  setTimeRange 
} from '../../store/slices/dashboardSlice';

/**
 * Executive Dashboard with comprehensive KPIs and analytics
 */
export const ExecutiveDashboard: React.FC = () => {
  const theme = useTheme();
  const dispatch = useDispatch();
  
  // Mock initial metrics data - in real app this would come from API
  const initialMetrics = {
    totalSuppliers: 150,
    activeShipments: 2340,
    riskAlerts: 7,
    onTimeDeliveryRate: 94.2,
    avgRiskScore: 3.1,
    totalValue: 127500000,
    monthlyGrowth: 8.3,
    criticalSuppliers: 3,
  };
  
  const metrics = useSelector(selectDashboardMetrics) || initialMetrics;
  const timeRange = useSelector(selectTimeRange);
  const lastRefresh = useSelector(selectLastRefresh);

  const handleRefresh = () => {
    dispatch(refreshDashboard());
  };

  const handleTimeRangeChange = (range: string) => {
    dispatch(setTimeRange(range as any));
  };

  const timeRangeOptions = [
    { value: '24h', label: '24 Hours' },
    { value: '7d', label: '7 Days' },
    { value: '30d', label: '30 Days' },
    { value: '90d', label: '90 Days' },
    { value: '1y', label: '1 Year' },
  ];

  // Sample data for charts - in real app, this would come from the store
  const riskTrendsData = [
    { date: '2024-01', risk: 65, compliance: 78, financial: 82 },
    { date: '2024-02', risk: 59, compliance: 80, financial: 85 },
    { date: '2024-03', risk: 62, compliance: 83, financial: 88 },
    { date: '2024-04', risk: 58, compliance: 85, financial: 90 },
    { date: '2024-05', risk: 55, compliance: 87, financial: 92 },
    { date: '2024-06', risk: 52, compliance: 89, financial: 94 },
  ];

  const supplierDistributionData = [
    { name: 'North America', value: 45, suppliers: 68 },
    { name: 'Asia Pacific', value: 35, suppliers: 52 },
    { name: 'Europe', value: 15, suppliers: 23 },
    { name: 'Others', value: 5, suppliers: 7 },
  ];

  const shipmentVolumeData = [
    { month: 'Jan', volume: 1250, value: 2.1 },
    { month: 'Feb', volume: 1380, value: 2.3 },
    { month: 'Mar', volume: 1520, value: 2.8 },
    { month: 'Apr', volume: 1680, value: 3.1 },
    { month: 'May', volume: 1850, value: 3.4 },
    { month: 'Jun', volume: 1920, value: 3.6 },
  ];

  const performanceData = [
    { metric: 'On-Time Delivery', current: 94.2, target: 95 },
    { metric: 'Quality Score', current: 97.8, target: 98 },
    { metric: 'Cost Efficiency', current: 89.5, target: 90 },
    { metric: 'Risk Mitigation', current: 92.1, target: 93 },
  ];

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box>
          <Typography variant="h4" component="h1" fontWeight={700} gutterBottom>
            Executive Dashboard
          </Typography>
          <Typography variant="body1" color="text.secondary">
            Real-time supply chain intelligence and performance metrics
          </Typography>
        </Box>
        
        <Box display="flex" alignItems="center" gap={2}>
          {/* Time Range Selector */}
          <Box display="flex" gap={1}>
            {timeRangeOptions.map((option) => (
              <Button
                key={option.value}
                variant={timeRange === option.value ? 'contained' : 'outlined'}
                size="small"
                onClick={() => handleTimeRangeChange(option.value)}
              >
                {option.label}
              </Button>
            ))}
          </Box>
          
          {/* Refresh Button */}
          <IconButton onClick={handleRefresh} color="primary">
            <Refresh />
          </IconButton>
          
          {/* Last Refresh */}
          {lastRefresh && (
            <Typography variant="caption" color="text.secondary">
              Last updated: {new Date(lastRefresh).toLocaleTimeString()}
            </Typography>
          )}
        </Box>
      </Box>

      {/* Key Metrics */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Total Suppliers"
            value={metrics.totalSuppliers.toLocaleString()}
            subtitle="Active supply partners"
            trend={{ value: 5.2, direction: 'up', period: 'vs last month' }}
            status="success"
            icon={<Inventory />}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Active Shipments"
            value={metrics.activeShipments.toLocaleString()}
            subtitle="In-transit deliveries"
            trend={{ value: 12.3, direction: 'up', period: 'vs last week' }}
            status="info"
            icon={<LocalShipping />}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Risk Alerts"
            value={metrics.riskAlerts}
            subtitle="Requiring attention"
            trend={{ value: 8.1, direction: 'down', period: 'vs last week' }}
            status={metrics.riskAlerts > 10 ? 'warning' : 'success'}
            icon={<Warning />}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="On-Time Delivery"
            value={`${metrics.onTimeDeliveryRate.toFixed(1)}%`}
            subtitle="Last 30 days"
            trend={{ value: 2.1, direction: 'up', period: 'vs target' }}
            status="success"
            icon={<Speed />}
          />
        </Grid>
      </Grid>

      {/* Financial Metrics */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Total Value"
            value={`$${(metrics.totalValue / 1000000).toFixed(1)}M`}
            subtitle="Supply chain value"
            trend={{ value: 15.8, direction: 'up', period: 'YoY growth' }}
            status="success"
            icon={<AccountBalance />}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Avg Risk Score"
            value={metrics.avgRiskScore.toFixed(1)}
            subtitle="Lower is better"
            trend={{ value: 3.2, direction: 'down', period: 'improvement' }}
            status="success"
            icon={<Security />}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Monthly Growth"
            value={`${metrics.monthlyGrowth.toFixed(1)}%`}
            subtitle="Revenue increase"
            trend={{ value: 1.8, direction: 'up', period: 'accelerating' }}
            status="success"
            icon={<TrendingUp />}
          />
        </Grid>
        
        <Grid item xs={12} sm={6} md={3}>
          <MetricCard
            title="Critical Suppliers"
            value={metrics.criticalSuppliers}
            subtitle="High-risk partners"
            trend={{ value: 12.5, direction: 'down', period: 'reduced risk' }}
            status={metrics.criticalSuppliers > 5 ? 'warning' : 'success'}
            icon={<Assessment />}
          />
        </Grid>
      </Grid>

      {/* Charts Section */}
      <Grid container spacing={3}>
        {/* Risk Trends */}
        <Grid item xs={12} lg={8}>
          <EnhancedLineChart
            title="Risk Trends Over Time"
            data={riskTrendsData}
            height={350}
            xKey="date"
            yKeys={['risk', 'compliance', 'financial']}
            colors={[theme.palette.error.main, theme.palette.warning.main, theme.palette.success.main]}
          />
        </Grid>
        
        {/* Supplier Distribution */}
        <Grid item xs={12} lg={4}>
          <EnhancedPieChart
            title="Supplier Distribution by Region"
            data={supplierDistributionData}
            height={350}
            dataKey="value"
            nameKey="name"
            colors={[
              theme.palette.primary.main,
              theme.palette.secondary.main,
              theme.palette.info.main,
              theme.palette.warning.main,
            ]}
          />
        </Grid>
        
        {/* Shipment Volume */}
        <Grid item xs={12} lg={6}>
          <EnhancedAreaChart
            title="Shipment Volume & Value"
            data={shipmentVolumeData}
            height={300}
            xKey="month"
            yKey="volume"
            colors={[theme.palette.primary.main]}
            gradient
          />
        </Grid>
        
        {/* Performance Metrics */}
        <Grid item xs={12} lg={6}>
          <EnhancedBarChart
            title="Performance vs Targets"
            data={performanceData}
            height={300}
            xKey="metric"
            yKey="current"
            colors={[theme.palette.success.main]}
          />
        </Grid>
      </Grid>
    </Box>
  );
};

export default ExecutiveDashboard;