import React, { useState, useEffect } from 'react';
import {
  Grid,
  Box,
  Typography,
  IconButton,
  useTheme,
  alpha,
} from '@mui/material';
import {
  Refresh,
  LocalShipping,
  MonetizationOn,
  Shield,
  Warning,
  Analytics,
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
  PremiumKPICard,
  EnhancedSearchBar,
  ProgressRing,
  EnhancedFloatingActionButton,
  GlassCard,
} from '../ui/PremiumComponents';

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
    supplyChainHealth: 87.3,
    totalCostImpact: 2450000,
    riskExposureIndex: 34.2,
    activeShipments: 1247,
    totalSuppliers: 156,
    onTimeDeliveryRate: 94.8,
    averageRiskScore: 2.4,
    monthlyGrowth: 8.5,
    criticalAlerts: 12,
    // Enhanced Executive KPIs
    budgetVariance: -3.2,
    roi: 18.7,
    qualityScore: 97.2,
    complianceRate: 99.1,
    capacityUtilization: 85.6,
    highRiskSuppliers: 8,
    mitigationEffectiveness: 92.4,
    supplierReliabilityScore: 89.3,
    costSavingsYTD: 3200000,
    predictedDelay: 5.2,
    demandForecastAccuracy: 94.1,
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
  const [loading, setLoading] = useState(false);
  const [lastUpdated, setLastUpdated] = useState(new Date());

  // Simulated real-time data updates
  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true);
      
      // Simulate API call with minimal delay for better UX
      await new Promise(resolve => setTimeout(resolve, 800));
      
      setLoading(false);
      setLastUpdated(new Date());
    };

    fetchDashboardData();
    
    // Set up real-time updates every 30 seconds
    const interval = setInterval(() => {
      setLastUpdated(new Date());
    }, 30000);
    return () => clearInterval(interval);
  }, []);

  const handleRefresh = () => {
    setLoading(true);
    setTimeout(() => {
      setDashboardData(prev => ({
        ...prev,
        supplyChainHealth: Math.max(80, Math.min(95, prev.supplyChainHealth + (Math.random() - 0.5) * 4)),
        onTimeDeliveryRate: Math.max(90, Math.min(98, prev.onTimeDeliveryRate + (Math.random() - 0.5) * 2)),
        activeShipments: prev.activeShipments + Math.floor((Math.random() - 0.5) * 20),
      }));
      setLoading(false);
      setLastUpdated(new Date());
    }, 1000);
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
                    Live • Last updated: {lastUpdated.toLocaleTimeString()}
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

      {/* Premium KPI Cards */}
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

        {/* Primary KPI Cards Row */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          {/* Supply Chain Health */}
          <Grid item xs={12} md={3}>
            <PremiumKPICard
              title="Supply Chain Health"
              value={`${dashboardData.supplyChainHealth.toFixed(1)}%`}
              subtitle="Overall network performance score"
              icon={<Shield />}
              color="success"
              variant="glass"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: 5.2,
                direction: 'up',
                period: 'vs last month',
              }}
            />
          </Grid>

          {/* Financial Impact */}
          <Grid item xs={12} md={3}>
            <PremiumKPICard
              title="Cost Savings YTD"
              value={`$${(dashboardData.costSavingsYTD / 1000000).toFixed(1)}M`}
              subtitle="Year-to-date cost optimization"
              icon={<MonetizationOn />}
              color="primary"
              variant="glass"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: 18.7,
                direction: 'up',
                period: 'ROI this quarter',
              }}
            />
          </Grid>

          {/* Risk Exposure Index */}
          <Grid item xs={12} md={3}>
            <PremiumKPICard
              title="Risk Exposure"
              value={`${dashboardData.riskExposureIndex.toFixed(1)}%`}
              subtitle="Overall supply chain risk level"
              icon={<Warning />}
              color="warning"
              variant="glass"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: 8.7,
                direction: 'down',
                period: 'risk reduction',
              }}
            />
          </Grid>

          {/* Active Shipments */}
          <Grid item xs={12} md={3}>
            <PremiumKPICard
              title="Active Shipments"
              value={dashboardData.activeShipments.toLocaleString()}
              subtitle="Currently in transit"
              icon={<LocalShipping />}
              color="info"
              variant="glass"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: 15.4,
                direction: 'up',
                period: 'volume increase',
              }}
            />
          </Grid>
        </Grid>

        {/* Secondary KPI Cards Row - Operational Performance */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          {/* On-Time Delivery */}
          <Grid item xs={12} md={4}>
            <PremiumKPICard
              title="On-Time Delivery"
              value={`${dashboardData.onTimeDeliveryRate.toFixed(1)}%`}
              subtitle="Target: 95.0% | Quality score: 97.2%"
              icon={<Analytics />}
              color="success"
              variant="gradient"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: dashboardData.qualityScore,
                direction: 'up',
                period: 'quality score',
              }}
            />
          </Grid>

          {/* Compliance Rate */}
          <Grid item xs={12} md={4}>
            <PremiumKPICard
              title="Compliance Rate"
              value={`${dashboardData.complianceRate.toFixed(1)}%`}
              subtitle="Regulatory & policy adherence"
              icon={<Shield />}
              color="primary"
              variant="gradient"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: 2.1,
                direction: 'up',
                period: 'improvement',
              }}
            />
          </Grid>

          {/* Supplier Reliability */}
          <Grid item xs={12} md={4}>
            <PremiumKPICard
              title="Supplier Reliability"
              value={`${dashboardData.supplierReliabilityScore.toFixed(1)}%`}
              subtitle={`${dashboardData.highRiskSuppliers} high-risk suppliers`}
              icon={<Warning />}
              color="warning"
              variant="gradient"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: dashboardData.mitigationEffectiveness,
                direction: 'up',
                period: 'mitigation effectiveness',
              }}
            />
          </Grid>
        </Grid>

        {/* Financial & Predictive Analytics Section */}
        <Typography 
          variant="h5" 
          fontWeight={600}
          sx={{ 
            mb: 3,
            mt: 2,
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
          Financial Impact & Predictive Analytics
        </Typography>

        <Grid container spacing={3} sx={{ mb: 4 }}>
          {/* Budget Variance */}
          <Grid item xs={12} md={3}>
            <PremiumKPICard
              title="Budget Variance"
              value={`${dashboardData.budgetVariance >= 0 ? '+' : ''}${dashboardData.budgetVariance.toFixed(1)}%`}
              subtitle="Against planned budget"
              icon={<MonetizationOn />}
              color={dashboardData.budgetVariance >= 0 ? "success" : "error"}
              variant="solid"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: Math.abs(dashboardData.budgetVariance),
                direction: dashboardData.budgetVariance >= 0 ? 'up' : 'down',
                period: 'vs budget',
              }}
            />
          </Grid>

          {/* Capacity Utilization */}
          <Grid item xs={12} md={3}>
            <PremiumKPICard
              title="Capacity Utilization"
              value={`${dashboardData.capacityUtilization.toFixed(1)}%`}
              subtitle="Optimal range: 80-90%"
              icon={<Analytics />}
              color="info"
              variant="solid"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: 4.2,
                direction: 'up',
                period: 'efficiency gain',
              }}
            />
          </Grid>

          {/* Predicted Delays */}
          <Grid item xs={12} md={3}>
            <PremiumKPICard
              title="Predicted Delays"
              value={`${dashboardData.predictedDelay.toFixed(1)}%`}
              subtitle="AI-powered forecast"
              icon={<Warning />}
              color="warning"
              variant="solid"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: 12.3,
                direction: 'down',
                period: 'improvement',
              }}
            />
          </Grid>

          {/* Demand Forecast Accuracy */}
          <Grid item xs={12} md={3}>
            <PremiumKPICard
              title="Forecast Accuracy"
              value={`${dashboardData.demandForecastAccuracy.toFixed(1)}%`}
              subtitle="Demand prediction precision"
              icon={<Analytics />}
              color="success"
              variant="solid"
              size="medium"
              loading={loading}
              animated={true}
              interactive={true}
              trend={{
                value: 2.8,
                direction: 'up',
                period: 'precision gain',
              }}
            />
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
                  value={dashboardData.onTimeDeliveryRate}
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