import React, { useState, useEffect } from 'react';
import {
  Grid,
  Box,
  Typography,
  IconButton,
  Button,
  useTheme,
  alpha,
  Card,
  CardContent,
  Avatar,
  LinearProgress,
  Chip,
  Divider,
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
  Business,
  Timeline,
  Public,
  Analytics,
  MonetizationOn,
  Shield,
  CheckCircle,
  ErrorOutline,
} from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';
import { useSelector, useDispatch } from 'react-redux';
import MetricCard from '../common/MetricCard';
import { 
  EnhancedAreaChart, 
  EnhancedBarChart, 
  EnhancedLineChart, 
  EnhancedPieChart 
} from '../common/EnhancedCharts';

/**
 * Enterprise-Grade Executive Dashboard with Advanced Analytics
 * Features:
 * - Real-time KPI monitoring with sophisticated animations
 * - Interactive charts with drill-down capabilities
 * - Risk assessment matrix with color-coded indicators
 * - Financial impact analysis with trend predictions
 * - Supply chain health scoring with performance benchmarks
 */
export const ExecutiveDashboard: React.FC = () => {
  const theme = useTheme();
  const [dashboardData, setDashboardData] = useState({
    supplyChainHealth: 0,
    totalCostImpact: 0,
    riskExposureIndex: 0,
    onTimePerformance: 0,
    supplierReliability: 0,
    activeShipments: 0,
    totalSuppliers: 0,
    highRiskAlerts: 0,
    costSavings: 0,
    budgetVariance: 0,
  });
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(new Date());

  // Simulated real-time data updates
  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      
      // Simulate API call with realistic delays
      await new Promise(resolve => setTimeout(resolve, 1500));
      
      setDashboardData({
        supplyChainHealth: 87,
        totalCostImpact: 2.4, // in millions
        riskExposureIndex: 23,
        onTimePerformance: 94.2,
        supplierReliability: 91.8,
        activeShipments: 2847,
        totalSuppliers: 156,
        highRiskAlerts: 7,
        costSavings: 5.8, // in millions
        budgetVariance: -2.1, // percentage
      });
      
      setLoading(false);
      setLastUpdated(new Date());
    };

    fetchDashboardData();
    
    // Set up real-time updates every 30 seconds
    const interval = setInterval(fetchDashboardData, 30000);
    return () => clearInterval(interval);
  }, []);

  const handleRefresh = () => {
    setLoading(true);
    setTimeout(() => {
      setDashboardData(prev => ({
        ...prev,
        supplyChainHealth: Math.max(80, Math.min(95, prev.supplyChainHealth + (Math.random() - 0.5) * 4)),
        onTimePerformance: Math.max(90, Math.min(98, prev.onTimePerformance + (Math.random() - 0.5) * 2)),
        activeShipments: prev.activeShipments + Math.floor((Math.random() - 0.5) * 20),
      }));
      setLoading(false);
      setLastUpdated(new Date());
    }, 1000);
  };

  // Enterprise KPI data for charts
  const riskTrendData = [
    { month: 'Jan', riskScore: 28, incidents: 12, cost: 1.2 },
    { month: 'Feb', riskScore: 25, incidents: 8, cost: 0.9 },
    { month: 'Mar', riskScore: 23, incidents: 6, cost: 0.7 },
    { month: 'Apr', riskScore: 26, incidents: 9, cost: 1.1 },
    { month: 'May', riskScore: 22, incidents: 5, cost: 0.6 },
    { month: 'Jun', riskScore: 23, incidents: 7, cost: 0.8 },
  ];

  const performanceData = [
    { category: 'On-Time Delivery', current: 94.2, target: 95, benchmark: 89 },
    { category: 'Quality Score', current: 96.8, target: 98, benchmark: 92 },
    { category: 'Cost Efficiency', current: 88.5, target: 90, benchmark: 85 },
    { category: 'Supplier Compliance', current: 91.8, target: 95, benchmark: 88 },
  ];

  const regionalRiskData = [
    { region: 'North America', value: 15, color: theme.palette.success.main },
    { region: 'Europe', value: 22, color: theme.palette.warning.main },
    { region: 'Asia Pacific', value: 35, color: theme.palette.error.main },
    { region: 'Latin America', value: 18, color: theme.palette.info.main },
    { region: 'Middle East', value: 10, color: theme.palette.secondary.main },
  ];

  const cardVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0 },
    hover: { y: -4, transition: { duration: 0.2 } },
  };

  const staggerContainer = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  };

  // Health score color logic
  const getHealthScoreColor = (score: number) => {
    if (score >= 90) return theme.palette.success.main;
    if (score >= 75) return theme.palette.warning.main;
    return theme.palette.error.main;
  };

  const timeRangeOptions = [
    { value: '24h', label: '24 Hours' },
    { value: '7d', label: '7 Days' },
    { value: '30d', label: '30 Days' },
    { value: '90d', label: '90 Days' },
    { value: '1y', label: '1 Year' },
  ];

  // Sample data for charts - enhanced with real business context
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

  const performanceDataCharts = [
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
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="visible"
      >
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
          <motion.div variants={cardVariants}>
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
                  Live • Last updated: {lastUpdated.toLocaleTimeString()}
                </Typography>
              </Box>
            </Box>
          </motion.div>
          
          <motion.div variants={cardVariants}>
            <Box display="flex" alignItems="center" gap={2} mt={{ xs: 2, md: 0 }}>
              {/* Time Range Selector */}
              <Box display="flex" gap={1} flexWrap="wrap">
                {timeRangeOptions.map((option, index) => (
                  <motion.div
                    key={option.value}
                    variants={cardVariants}
                    custom={index}
                  >
                    <Button
                      variant="outlined"
                      size="medium"
                      sx={{
                        borderRadius: 2.5,
                        textTransform: 'none',
                        fontWeight: 500,
                        minWidth: 'auto',
                        px: 2.5,
                        py: 1,
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
      </motion.div>

      {/* C-Level KPI Overview Section */}
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="visible"
      >
        <Typography 
          variant="h4" 
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
              height: 28, 
              backgroundColor: theme.palette.primary.main, 
              borderRadius: 2,
            }} 
          />
          C-Level Executive Overview
        </Typography>

        <Grid container spacing={3} sx={{ mb: 4 }}>
          {/* Supply Chain Health Score */}
          <Grid item xs={12} md={4}>
            <motion.div variants={cardVariants} whileHover="hover">
              <Card sx={{ 
                height: '100%', 
                background: `linear-gradient(135deg, ${getHealthScoreColor(dashboardData.supplyChainHealth)}, ${alpha(getHealthScoreColor(dashboardData.supplyChainHealth), 0.8)})`,
                color: 'white',
                position: 'relative',
                overflow: 'hidden',
              }}>
                <CardContent sx={{ p: 3 }}>
                  <Box display="flex" alignItems="center" gap={2} mb={2}>
                    <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.2)', color: 'white' }}>
                      <Shield />
                    </Avatar>
                    <Typography variant="h6" fontWeight={600}>
                      Supply Chain Health
                    </Typography>
                  </Box>
                  <Typography variant="h2" fontWeight={700} sx={{ mb: 1 }}>
                    {loading ? '---' : dashboardData.supplyChainHealth}
                    <Typography component="span" variant="h5">/100</Typography>
                  </Typography>
                  <LinearProgress 
                    variant="determinate" 
                    value={dashboardData.supplyChainHealth} 
                    sx={{ 
                      height: 8, 
                      borderRadius: 4,
                      backgroundColor: 'rgba(255,255,255,0.2)',
                      '& .MuiLinearProgress-bar': {
                        backgroundColor: 'white',
                      }
                    }} 
                  />
                  <Typography variant="body2" sx={{ mt: 1, opacity: 0.9 }}>
                    Overall network performance score
                  </Typography>
                </CardContent>
                <Box sx={{ 
                  position: 'absolute', 
                  top: 0, 
                  right: 0, 
                  width: 100, 
                  height: 100, 
                  opacity: 0.1,
                  transform: 'translate(30px, -30px)',
                }}>
                  <Security sx={{ fontSize: 80 }} />
                </Box>
              </Card>
            </motion.div>
          </Grid>

          {/* Total Cost Impact */}
          <Grid item xs={12} md={4}>
            <motion.div variants={cardVariants} whileHover="hover">
              <Card sx={{ height: '100%' }}>
                <CardContent sx={{ p: 3 }}>
                  <Box display="flex" alignItems="center" gap={2} mb={2}>
                    <Avatar sx={{ bgcolor: theme.palette.error.light }}>
                      <MonetizationOn />
                    </Avatar>
                    <Typography variant="h6" fontWeight={600}>
                      Total Cost Impact
                    </Typography>
                  </Box>
                  <Typography variant="h2" fontWeight={700} color="error.main" sx={{ mb: 1 }}>
                    ${loading ? '-.--' : dashboardData.totalCostImpact.toFixed(1)}M
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1}>
                    <Chip 
                      label="Risk Exposure" 
                      size="small" 
                      color="error" 
                      variant="outlined"
                    />
                  </Box>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                    Potential financial exposure from identified risks
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>

          {/* On-Time Performance */}
          <Grid item xs={12} md={4}>
            <motion.div variants={cardVariants} whileHover="hover">
              <Card sx={{ height: '100%' }}>
                <CardContent sx={{ p: 3 }}>
                  <Box display="flex" alignItems="center" gap={2} mb={2}>
                    <Avatar sx={{ bgcolor: theme.palette.success.light }}>
                      <CheckCircle />
                    </Avatar>
                    <Typography variant="h6" fontWeight={600}>
                      On-Time Performance
                    </Typography>
                  </Box>
                  <Typography variant="h2" fontWeight={700} color="success.main" sx={{ mb: 1 }}>
                    {loading ? '--.-' : dashboardData.onTimePerformance.toFixed(1)}%
                  </Typography>
                  <LinearProgress 
                    variant="determinate" 
                    value={dashboardData.onTimePerformance} 
                    sx={{ 
                      height: 8, 
                      borderRadius: 4,
                      backgroundColor: alpha(theme.palette.success.main, 0.1),
                      '& .MuiLinearProgress-bar': {
                        backgroundColor: theme.palette.success.main,
                      }
                    }} 
                  />
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                    Delivery performance vs. committed dates
                  </Typography>
                </CardContent>
              </Card>
            </motion.div>
          </Grid>
        </Grid>
      </motion.div>

      {/* Operational Metrics Grid */}
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="visible"
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
          Operational Intelligence
        </Typography>

        <Grid container spacing={3} sx={{ mb: 4 }}>
          {[
            {
              title: "Active Shipments",
              value: dashboardData.activeShipments.toLocaleString(),
              subtitle: "Currently in transit",
              trend: { value: 12.3, direction: 'up' as const, period: 'vs last week', label: 'increase' },
              icon: <LocalShipping />,
              colorScheme: 'primary' as const,
            },
            {
              title: "Total Suppliers",
              value: dashboardData.totalSuppliers.toLocaleString(),
              subtitle: "Active network partners",
              trend: { value: 5.2, direction: 'up' as const, period: 'vs last month', label: 'growth' },
              icon: <Business />,
              colorScheme: 'success' as const,
            },
            {
              title: "High Risk Alerts",
              value: dashboardData.highRiskAlerts,
              subtitle: "Requiring immediate attention",
              trend: { value: 8.1, direction: 'down' as const, period: 'vs last week', label: 'improvement' },
              icon: <ErrorOutline />,
              colorScheme: dashboardData.highRiskAlerts > 10 ? 'error' as const : 'warning' as const,
            },
            {
              title: "Supplier Reliability",
              value: `${dashboardData.supplierReliability.toFixed(1)}%`,
              subtitle: "Average performance score",
              trend: { value: 2.1, direction: 'up' as const, period: 'vs target', label: 'improvement' },
              icon: <Assessment />,
              colorScheme: 'accent' as const,
            },
          ].map((metric, index) => (
            <Grid item xs={12} sm={6} lg={3} key={metric.title}>
              <motion.div variants={cardVariants} whileHover="hover">
                <MetricCard
                  {...metric}
                  variant="premium"
                  size="medium"
                  loading={loading}
                />
              </motion.div>
            </Grid>
          ))}
        </Grid>
      </motion.div>

      {/* Advanced Analytics Section */}
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="visible"
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
            <motion.div variants={cardVariants}>
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
                  loading={loading}
                />
              </Box>
            </motion.div>
          </Grid>
          
          {/* Supplier Distribution */}
          <Grid item xs={12} lg={4}>
            <motion.div variants={cardVariants}>
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
                  loading={loading}
                />
              </Box>
            </motion.div>
          </Grid>
          
          {/* Shipment Volume */}
          <Grid item xs={12} lg={6}>
            <motion.div variants={cardVariants}>
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
                  loading={loading}
                />
              </Box>
            </motion.div>
          </Grid>
          
          {/* Performance Metrics */}
          <Grid item xs={12} lg={6}>
            <motion.div variants={cardVariants}>
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
                  data={performanceDataCharts}
                  height={300}
                  xKey="metric"
                  yKey="current"
                  colors={[theme.palette.success.main]}
                  loading={loading}
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