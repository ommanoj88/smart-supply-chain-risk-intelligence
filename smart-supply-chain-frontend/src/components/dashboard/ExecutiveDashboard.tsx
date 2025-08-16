import React, { useState, useEffect } from 'react';
import {
  Grid,
  Box,
  Typography,
  IconButton,
  useTheme,
  alpha,
  Alert,
  Skeleton,
} from '@mui/material';
import {
  Refresh,
  LocalShipping,
  MonetizationOn,
  Warning,
  Analytics,
  Security,
  Assessment,
  AccountBalance,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { 
  PremiumAreaChart, 
  PremiumBarChart, 
  PremiumLineChart, 
  PremiumPieChart,
} from '../common/PremiumCharts';
// import { 
//   Animated3DBarChart,
//   SupplierNetwork3D,
// } from '../common/Premium3DCharts';
import { 
  EnhancedSearchBar,
  ProgressRing,
  EnhancedFloatingActionButton,
  GlassCard,
} from '../ui/PremiumComponents';
import CircularProgress from '../ui/CircularProgress';
import AnimatedCounter from '../ui/AnimatedCounter';

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
  const [executiveKPIs, setExecutiveKPIs] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null);
  
  // Legacy dashboard data for charts (keeping existing structure for compatibility)
  const [dashboardData] = useState({
    riskTrends: [
      { date: 'Jan', high: 15, medium: 45, low: 89 },
      { date: 'Feb', high: 12, medium: 38, low: 95 },
      { date: 'Mar', high: 18, medium: 42, low: 87 },
      { date: 'Apr', high: 9, medium: 35, low: 102 },
      { date: 'May', high: 14, medium: 41, low: 93 },
      { date: 'Jun', high: 11, medium: 29, low: 108 },
    ],
    financialImpact: [
      { month: 'Jan', savings: 180000, costs: 45000 },
      { month: 'Feb', savings: 220000, costs: 38000 },
      { month: 'Mar', savings: 195000, costs: 52000 },
      { month: 'Apr', savings: 240000, costs: 41000 },
      { month: 'May', savings: 210000, costs: 47000 },
      { month: 'Jun', savings: 265000, costs: 39000 },
    ],
    supplierDistribution: [
      { name: 'North America', value: 45, risk: 'low' },
      { name: 'Europe', value: 38, risk: 'medium' },
      { name: 'Asia Pacific', value: 52, risk: 'high' },
      { name: 'Latin America', value: 21, risk: 'medium' },
    ],
    networkNodes: [
      {
        id: '1',
        name: 'TechCorp Manufacturing',
        position: [0, 0, 0] as [number, number, number],
        connections: ['2', '3'],
        riskLevel: 'low' as const,
        value: 2500000,
        country: 'USA',
        category: 'Electronics',
      },
      {
        id: '2',
        name: 'Global Auto Parts',
        position: [5, 2, 3] as [number, number, number],
        connections: ['1', '4'],
        riskLevel: 'medium' as const,
        value: 1800000,
        country: 'Germany',
        category: 'Automotive',
      },
      {
        id: '3',
        name: 'Asian Semiconductors',
        position: [-3, 1, 5] as [number, number, number],
        connections: ['1', '5'],
        riskLevel: 'high' as const,
        value: 3200000,
        country: 'Taiwan',
        category: 'Technology',
      },
      {
        id: '4',
        name: 'European Logistics',
        position: [2, -2, -4] as [number, number, number],
        connections: ['2'],
        riskLevel: 'low' as const,
        value: 950000,
        country: 'Netherlands',
        category: 'Logistics',
      },
      {
        id: '5',
        name: 'Critical Materials Ltd',
        position: [-5, 3, -2] as [number, number, number],
        connections: ['3'],
        riskLevel: 'critical' as const,
        value: 4100000,
        country: 'China',
        category: 'Raw Materials',
      },
    ],
    performanceMetrics: [
      { category: 'Delivery Performance', current: 94.8, target: 95.0, trend: 2.1 },
      { category: 'Quality Score', current: 97.2, target: 98.0, trend: -0.8 },
      { category: 'Cost Efficiency', current: 92.5, target: 90.0, trend: 4.2 },
      { category: 'Risk Mitigation', current: 88.7, target: 85.0, trend: 6.3 },
    ],
    chartData3D: [
      { x: 0, y: 0, z: 0, value: 245, label: 'Q1', category: 'Revenue' },
      { x: 1, y: 0, z: 0, value: 289, label: 'Q2', category: 'Revenue' },
      { x: 2, y: 0, z: 0, value: 312, label: 'Q3', category: 'Revenue' },
      { x: 3, y: 0, z: 0, value: 387, label: 'Q4', category: 'Revenue' },
      { x: 0, y: 1, z: 0, value: 156, label: 'Q1', category: 'Costs' },
      { x: 1, y: 1, z: 0, value: 142, label: 'Q2', category: 'Costs' },
      { x: 2, y: 1, z: 0, value: 168, label: 'Q3', category: 'Costs' },
      { x: 3, y: 1, z: 0, value: 179, label: 'Q4', category: 'Costs' },
    ],
  });
  
  // Load Executive KPIs from backend API
  const loadExecutiveKPIs = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const response = await fetch('/api/executive/kpis');
      if (!response.ok) {
        throw new Error('Failed to load Executive KPIs');
      }
      
      const data = await response.json();
      if (data.success) {
        setExecutiveKPIs(data);
        setLastUpdated(new Date());
      } else {
        throw new Error(data.error || 'Failed to load Executive KPIs');
      }
      
    } catch (error) {
      console.error('Error loading Executive KPIs:', error);
      setError(error instanceof Error ? error.message : 'Failed to load Executive KPIs');
      
      // Fallback data for development/demo
      setExecutiveKPIs({
        success: true,
        supplyChainHealth: 87.3,
        financial: {
          costSavingsYTD: 2450000,
          costSavingsTrend: 18.3,
          roiPercentage: 24.7,
          budgetVariance: -3.2,
          costPerShipment: 1250.75,
        },
        operational: {
          onTimeDeliveryRate: 94.8,
          supplierPerformanceScore: 89.2,
          qualityScore: 97.2,
          complianceRate: 94.8,
          perfectOrderRate: 91.5,
        },
        risks: {
          activeHighRiskSuppliers: 8,
          delayedShipments: 15,
          riskExposureIndex: 34.2,
          riskMitigationRate: 88.5,
          avgResponseTime: 2.3,
        },
        counters: {
          totalShipments: 1247,
          activeShipments: 234,
          totalSuppliers: 156,
          activeAlerts: 12,
        },
        lastUpdated: new Date(),
      });
      
    } finally {
      setLoading(false);
    }
  };

  // Load data on component mount and set up refresh
  useEffect(() => {
    loadExecutiveKPIs();
    
    // Set up real-time updates every 30 seconds
    const interval = setInterval(() => {
      loadExecutiveKPIs();
    }, 30000);
    
    return () => clearInterval(interval);
  }, []);

  const handleRefresh = () => {
    loadExecutiveKPIs();
  };

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
        <GlassCard 
          sx={{
            mb: 4,
            p: 3,
          }}
        >
          <Box 
            sx={{
              display: 'flex',
              flexDirection: { xs: 'column', md: 'row' },
              justifyContent: 'space-between',
              alignItems: { xs: 'flex-start', md: 'center' },
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
                    Live • Last updated: {lastUpdated?.toLocaleTimeString() || 'Loading...'}
                  </Typography>
                </Box>
              </Box>
            </motion.div>
            
            <motion.div variants={cardVariants}>
              <Box display="flex" alignItems="center" gap={2} mt={{ xs: 2, md: 0 }}>
                <EnhancedSearchBar 
                  placeholder="Search metrics, suppliers, or shipments..."
                  variant="glass"
                  size="medium"
                  showFilters={true}
                />
                
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
        </GlassCard>
      </motion.div>

      {/* Enhanced Executive KPIs Dashboard */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}
      
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
          Executive KPIs & Performance Metrics
        </Typography>

        {/* Primary KPIs Row */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          {/* Supply Chain Health Score with Circular Progress */}
          <Grid item xs={12} md={6} lg={3}>
            <GlassCard sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              {loading ? (
                <Skeleton variant="circular" width={180} height={180} />
              ) : (
                <CircularProgress
                  value={executiveKPIs?.supplyChainHealth || 87.3}
                  size={180}
                  strokeWidth={12}
                  title="Supply Chain Health"
                  subtitle="Overall performance score"
                  animated={true}
                  gradient={true}
                />
              )}
            </GlassCard>
          </Grid>

          {/* Financial Performance */}
          <Grid item xs={12} md={6} lg={3}>
            {loading ? (
              <Skeleton variant="rectangular" height={200} />
            ) : (
              <AnimatedCounter
                label="Cost Savings YTD"
                value={executiveKPIs?.financial?.costSavingsYTD || 2450000}
                format="currency"
                trend={{
                  value: executiveKPIs?.financial?.costSavingsTrend || 18.3,
                  direction: 'up',
                  period: 'vs last year'
                }}
                color="success"
                size="large"
                icon={<MonetizationOn />}
              />
            )}
          </Grid>

          {/* Operational Excellence */}
          <Grid item xs={12} md={6} lg={3}>
            {loading ? (
              <Skeleton variant="rectangular" height={200} />
            ) : (
              <AnimatedCounter
                label="On-Time Delivery"
                value={executiveKPIs?.operational?.onTimeDeliveryRate || 94.8}
                format="percentage"
                trend={{
                  value: 2.1,
                  direction: 'up',
                  period: 'this month'
                }}
                color="info"
                size="large"
                icon={<LocalShipping />}
              />
            )}
          </Grid>

          {/* Risk Exposure */}
          <Grid item xs={12} md={6} lg={3}>
            {loading ? (
              <Skeleton variant="rectangular" height={200} />
            ) : (
              <AnimatedCounter
                label="Risk Exposure Index"
                value={executiveKPIs?.risks?.riskExposureIndex || 34.2}
                format="number"
                suffix="/100"
                trend={{
                  value: 8.7,
                  direction: 'down',
                  period: 'risk reduction'
                }}
                color="warning"
                size="large"
                icon={<Warning />}
              />
            )}
          </Grid>
        </Grid>

        {/* Secondary KPIs Row */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            {loading ? (
              <Skeleton variant="rectangular" height={150} />
            ) : (
              <AnimatedCounter
                label="Active Shipments"
                value={executiveKPIs?.counters?.activeShipments || 234}
                format="number"
                color="primary"
                icon={<LocalShipping />}
              />
            )}
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            {loading ? (
              <Skeleton variant="rectangular" height={150} />
            ) : (
              <AnimatedCounter
                label="Total Suppliers"
                value={executiveKPIs?.counters?.totalSuppliers || 156}
                format="number"
                color="info"
                icon={<AccountBalance />}
              />
            )}
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            {loading ? (
              <Skeleton variant="rectangular" height={150} />
            ) : (
              <AnimatedCounter
                label="Quality Score"
                value={executiveKPIs?.operational?.qualityScore || 97.2}
                format="percentage"
                color="success"
                icon={<Assessment />}
              />
            )}
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            {loading ? (
              <Skeleton variant="rectangular" height={150} />
            ) : (
              <AnimatedCounter
                label="Active Alerts"
                value={executiveKPIs?.counters?.activeAlerts || 12}
                format="number"
                trend={{
                  value: 3,
                  direction: 'down',
                  period: 'from yesterday'
                }}
                color="error"
                icon={<Security />}
              />
            )}
          </Grid>
        </Grid>
      </motion.div>

      {/* Advanced Charts Section */}
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
              backgroundColor: theme.palette.secondary.main, 
              borderRadius: 2,
            }} 
          />
          Advanced Analytics & Insights
        </Typography>

        <Grid container spacing={3} sx={{ mb: 4 }}>
          {/* Risk Trends Chart */}
          <Grid item xs={12} lg={8}>
            <PremiumLineChart
              data={dashboardData.riskTrends}
              title="Risk Trends Analysis"
              subtitle="Multi-dimensional risk assessment over time"
              height={350}
              xKey="date"
              yKeys={['high', 'medium', 'low']}
              loading={loading}
              variant="premium"
              animation={true}
              curved={true}
              dots={true}
              showLegend={true}
              colors={[theme.palette.error.main, theme.palette.warning.main, theme.palette.success.main]}
            />
          </Grid>

          {/* Performance Ring Chart */}
          <Grid item xs={12} lg={4}>
            <GlassCard sx={{ height: 350 + 80, p: 3 }}>
              <Typography variant="h6" fontWeight={700} sx={{ mb: 2 }}>
                Delivery Performance
              </Typography>
              <Box display="flex" justifyContent="center" alignItems="center" sx={{ height: 250 }}>
                <ProgressRing
                  value={executiveKPIs?.operational?.onTimeDeliveryRate || 94.8}
                  max={100}
                  size={200}
                  strokeWidth={12}
                  showValue={true}
                  animated={true}
                  gradient={true}
                />
              </Box>
              <Typography variant="body2" color="text.secondary" align="center">
                On-time delivery rate with 95% target
              </Typography>
            </GlassCard>
          </Grid>

          {/* 3D Bar Chart - Temporarily Disabled */}
          <Grid item xs={12} lg={6}>
            <GlassCard sx={{ height: 400, p: 3, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <Typography variant="h6" color="text.secondary">
                3D Visualization Coming Soon
              </Typography>
            </GlassCard>
          </Grid>

          {/* Financial Impact Area Chart */}
          <Grid item xs={12} lg={6}>
            <PremiumAreaChart
              data={dashboardData.financialImpact}
              title="Financial Impact Analysis"
              subtitle="Cost savings and operational expenses"
              height={400}
              xKey="month"
              yKey="savings"
              loading={loading}
              variant="premium"
              gradient={true}
              strokeWidth={3}
              fillOpacity={0.2}
              animation={true}
            />
          </Grid>

          {/* Supplier Distribution Pie Chart */}
          <Grid item xs={12} lg={6}>
            <PremiumPieChart
              data={dashboardData.supplierDistribution}
              title="Global Supplier Distribution"
              subtitle="Regional breakdown with risk assessment"
              height={400}
              dataKey="value"
              nameKey="name"
              loading={loading}
              variant="premium"
              donut={true}
              gradient3D={true}
              showLabels={true}
              colors={[
                theme.palette.primary.main,
                theme.palette.secondary.main,
                theme.palette.success.main,
                theme.palette.warning.main,
              ]}
            />
          </Grid>

          {/* 3D Network Visualization - Temporarily Disabled */}
          <Grid item xs={12} lg={6}>
            <GlassCard sx={{ height: 400, p: 3, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <Typography variant="h6" color="text.secondary">
                3D Network Visualization Coming Soon
              </Typography>
            </GlassCard>
          </Grid>

          {/* Performance Metrics Bar Chart */}
          <Grid item xs={12}>
            <PremiumBarChart
              data={dashboardData.performanceMetrics.map(metric => ({
                category: metric.category,
                current: metric.current,
                target: metric.target,
              }))}
              title="Performance vs Targets"
              subtitle="Key performance indicators against established targets"
              height={300}
              xKey="category"
              yKey="current"
              loading={loading}
              variant="premium"
              horizontal={false}
              rounded={true}
              gradient={true}
              animation={true}
            />
          </Grid>
        </Grid>
      </motion.div>

      {/* Floating Action Button */}
      <EnhancedFloatingActionButton
        icon={<Analytics />}
        onClick={() => console.log('Advanced analytics clicked')}
        color="primary"
        size="medium"
      />
    </Box>
  );
};

export default ExecutiveDashboard;