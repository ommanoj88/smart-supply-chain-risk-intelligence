import React from 'react';
import {
  Grid,
  Box,
  Typography,
  IconButton,
  Button,
  useTheme,
  alpha,
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
import { motion } from 'framer-motion';
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
    <Box sx={{ 
      minHeight: '100vh',
      background: `linear-gradient(135deg, ${alpha(theme.palette.primary.main, 0.02)} 0%, ${alpha(theme.palette.secondary.main, 0.01)} 100%)`,
      p: { xs: 2, md: 4 }
    }}>
      {/* Premium Header Section */}
      <Box 
        sx={{
          display: 'flex',
          flexDirection: { xs: 'column', md: 'row' },
          justifyContent: 'space-between',
          alignItems: { xs: 'flex-start', md: 'center' },
          mb: 4,
          p: 3,
          background: 'rgba(255, 255, 255, 0.8)',
          backdropFilter: 'blur(20px)',
          borderRadius: 3,
          border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
          boxShadow: '0 8px 32px rgba(0, 0, 0, 0.06)',
        }}
      >
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <Box>
            <Typography 
              variant="h3" 
              component="h1" 
              fontWeight={700}
              sx={{
                background: `linear-gradient(135deg, ${theme.palette.primary.main}, ${theme.palette.secondary.main})`,
                backgroundClip: 'text',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                mb: 1,
              }}
            >
              Executive Dashboard
            </Typography>
            <Typography 
              variant="subtitle1" 
              color="text.secondary"
              sx={{ 
                opacity: 0.8,
                fontWeight: 500,
                letterSpacing: '0.025em',
              }}
            >
              Real-time supply chain intelligence and performance metrics
            </Typography>
            {lastRefresh && (
              <Box display="flex" alignItems="center" gap={1} mt={1}>
                <Box 
                  sx={{ 
                    width: 8, 
                    height: 8, 
                    borderRadius: '50%', 
                    backgroundColor: theme.palette.success.main,
                    animation: 'pulse 2s infinite',
                  }} 
                />
                <Typography variant="caption" color="text.secondary">
                  Live • Last updated: {new Date(lastRefresh).toLocaleTimeString()}
                </Typography>
              </Box>
            )}
          </Box>
        </motion.div>
        
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.6, delay: 0.2 }}
        >
          <Box display="flex" alignItems="center" gap={2} mt={{ xs: 2, md: 0 }}>
            {/* Time Range Selector */}
            <Box display="flex" gap={1} flexWrap="wrap">
              {timeRangeOptions.map((option, index) => (
                <motion.div
                  key={option.value}
                  initial={{ opacity: 0, scale: 0.9 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ duration: 0.3, delay: index * 0.1 }}
                >
                  <Button
                    variant={timeRange === option.value ? 'contained' : 'outlined'}
                    size="medium"
                    onClick={() => handleTimeRangeChange(option.value)}
                    sx={{
                      borderRadius: 2.5,
                      textTransform: 'none',
                      fontWeight: 500,
                      minWidth: 'auto',
                      px: 2.5,
                      py: 1,
                      '&.MuiButton-contained': {
                        boxShadow: `0 4px 16px ${alpha(theme.palette.primary.main, 0.3)}`,
                      },
                    }}
                  >
                    {option.label}
                  </Button>
                </motion.div>
              ))}
            </Box>
            
            {/* Refresh Button */}
            <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.95 }}>
              <IconButton 
                onClick={handleRefresh} 
                size="large"
                sx={{
                  backgroundColor: alpha(theme.palette.primary.main, 0.1),
                  color: theme.palette.primary.main,
                  '&:hover': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.2),
                    transform: 'rotate(180deg)',
                  },
                  transition: 'all 0.3s ease',
                }}
              >
                <Refresh />
              </IconButton>
            </motion.div>
          </Box>
        </motion.div>
      </Box>

      {/* Premium KPI Section */}
      <Box mb={4}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.3 }}
        >
          <Typography 
            variant="h5" 
            fontWeight={600}
            sx={{ 
              mb: 3,
              color: theme.palette.text.primary,
              display: 'flex',
              alignItems: 'center',
              gap: 1,
            }}
          >
            <Box 
              sx={{ 
                width: 4, 
                height: 24, 
                backgroundColor: theme.palette.primary.main, 
                borderRadius: 2,
              }} 
            />
            Key Performance Indicators
          </Typography>
        </motion.div>

        <Grid container spacing={3}>
          {[
            {
              title: "Total Suppliers",
              value: metrics.totalSuppliers.toLocaleString(),
              subtitle: "Active supply partners",
              trend: { value: 5.2, direction: 'up' as const, period: 'vs last month', label: 'growth' },
              icon: <Inventory />,
              colorScheme: 'success' as const,
              target: 200,
              description: "Network expansion",
            },
            {
              title: "Active Shipments",
              value: metrics.activeShipments.toLocaleString(),
              subtitle: "In-transit deliveries",
              trend: { value: 12.3, direction: 'up' as const, period: 'vs last week', label: 'increase' },
              icon: <LocalShipping />,
              colorScheme: 'primary' as const,
              target: 2500,
              description: "Current logistics",
            },
            {
              title: "Risk Alerts",
              value: metrics.riskAlerts,
              subtitle: "Requiring attention",
              trend: { value: 8.1, direction: 'down' as const, period: 'vs last week', label: 'improvement' },
              icon: <Warning />,
              colorScheme: metrics.riskAlerts > 10 ? 'warning' as const : 'success' as const,
              target: 5,
              description: "Risk mitigation",
            },
            {
              title: "On-Time Delivery",
              value: `${metrics.onTimeDeliveryRate.toFixed(1)}%`,
              subtitle: "Performance metric",
              trend: { value: 2.1, direction: 'up' as const, period: 'vs target', label: 'improvement' },
              icon: <Speed />,
              colorScheme: 'accent' as const,
              target: 95,
              unit: "%",
              description: "Service excellence",
            },
          ].map((metric, index) => (
            <Grid item xs={12} sm={6} lg={3} key={metric.title}>
              <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: 0.4 + index * 0.1 }}
              >
                <MetricCard
                  {...metric}
                  variant="premium"
                  size="medium"
                />
              </motion.div>
            </Grid>
          ))}
        </Grid>
      </Box>

      {/* Financial & Risk Metrics */}
      <Box mb={4}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.7 }}
        >
          <Typography 
            variant="h5" 
            fontWeight={600}
            sx={{ 
              mb: 3,
              color: theme.palette.text.primary,
              display: 'flex',
              alignItems: 'center',
              gap: 1,
            }}
          >
            <Box 
              sx={{ 
                width: 4, 
                height: 24, 
                backgroundColor: theme.palette.secondary.main, 
                borderRadius: 2,
              }} 
            />
            Financial & Risk Intelligence
          </Typography>
        </motion.div>

        <Grid container spacing={3}>
          {[
            {
              title: "Total Value",
              value: `$${(metrics.totalValue / 1000000).toFixed(1)}M`,
              subtitle: "Supply chain value",
              trend: { value: 15.8, direction: 'up' as const, period: 'YoY', label: 'growth' },
              icon: <AccountBalance />,
              colorScheme: 'success' as const,
              unit: "M",
              description: "Enterprise value",
            },
            {
              title: "Risk Score",
              value: metrics.avgRiskScore.toFixed(1),
              subtitle: "Lower is better",
              trend: { value: 3.2, direction: 'down' as const, period: 'improvement', label: 'better' },
              icon: <Security />,
              colorScheme: 'accent' as const,
              target: 2.0,
              description: "Risk management",
            },
            {
              title: "Monthly Growth",
              value: `${metrics.monthlyGrowth.toFixed(1)}%`,
              subtitle: "Revenue increase",
              trend: { value: 1.8, direction: 'up' as const, period: 'accelerating', label: 'momentum' },
              icon: <TrendingUp />,
              colorScheme: 'primary' as const,
              unit: "%",
              description: "Business growth",
            },
            {
              title: "Critical Issues",
              value: metrics.criticalSuppliers,
              subtitle: "High-priority items",
              trend: { value: 12.5, direction: 'down' as const, period: 'resolved', label: 'improvement' },
              icon: <Assessment />,
              colorScheme: metrics.criticalSuppliers > 5 ? 'warning' as const : 'success' as const,
              target: 0,
              description: "Issue resolution",
            },
          ].map((metric, index) => (
            <Grid item xs={12} sm={6} lg={3} key={metric.title}>
              <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5, delay: 0.8 + index * 0.1 }}
              >
                <MetricCard
                  {...metric}
                  variant="glass"
                  size="medium"
                />
              </motion.div>
            </Grid>
          ))}
        </Grid>
      </Box>

      {/* Advanced Analytics Section */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, delay: 1.2 }}
      >
        <Typography 
          variant="h5" 
          fontWeight={600}
          sx={{ 
            mb: 3,
            color: theme.palette.text.primary,
            display: 'flex',
            alignItems: 'center',
            gap: 1,
          }}
        >
          <Box 
            sx={{ 
              width: 4, 
              height: 24, 
              backgroundColor: theme.palette.warning.main, 
              borderRadius: 2,
            }} 
          />
          Advanced Analytics & Insights
        </Typography>

        <Grid container spacing={3}>
          {/* Risk Trends */}
          <Grid item xs={12} lg={8}>
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.6, delay: 1.3 }}
            >
              <Box
                sx={{
                  background: 'rgba(255, 255, 255, 0.9)',
                  backdropFilter: 'blur(20px)',
                  borderRadius: 3,
                  border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
                  overflow: 'hidden',
                  height: '100%',
                }}
              >
                <EnhancedLineChart
                  title="Risk Trends Over Time"
                  data={riskTrendsData}
                  height={350}
                  xKey="date"
                  yKeys={['risk', 'compliance', 'financial']}
                  colors={[theme.palette.error.main, theme.palette.warning.main, theme.palette.success.main]}
                />
              </Box>
            </motion.div>
          </Grid>
          
          {/* Supplier Distribution */}
          <Grid item xs={12} lg={4}>
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.6, delay: 1.4 }}
            >
              <Box
                sx={{
                  background: 'rgba(255, 255, 255, 0.9)',
                  backdropFilter: 'blur(20px)',
                  borderRadius: 3,
                  border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
                  overflow: 'hidden',
                  height: '100%',
                }}
              >
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
              </Box>
            </motion.div>
          </Grid>
          
          {/* Shipment Volume */}
          <Grid item xs={12} lg={6}>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 1.5 }}
            >
              <Box
                sx={{
                  background: 'rgba(255, 255, 255, 0.9)',
                  backdropFilter: 'blur(20px)',
                  borderRadius: 3,
                  border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
                  overflow: 'hidden',
                  height: '100%',
                }}
              >
                <EnhancedAreaChart
                  title="Shipment Volume & Value Trends"
                  data={shipmentVolumeData}
                  height={300}
                  xKey="month"
                  yKey="volume"
                  colors={[theme.palette.primary.main]}
                  gradient
                />
              </Box>
            </motion.div>
          </Grid>
          
          {/* Performance Metrics */}
          <Grid item xs={12} lg={6}>
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: 1.6 }}
            >
              <Box
                sx={{
                  background: 'rgba(255, 255, 255, 0.9)',
                  backdropFilter: 'blur(20px)',
                  borderRadius: 3,
                  border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
                  overflow: 'hidden',
                  height: '100%',
                }}
              >
                <EnhancedBarChart
                  title="Performance vs Targets"
                  data={performanceData}
                  height={300}
                  xKey="metric"
                  yKey="current"
                  colors={[theme.palette.success.main]}
                />
              </Box>
            </motion.div>
          </Grid>
        </Grid>
      </motion.div>
    </Box>
  );
};

export default ExecutiveDashboard;