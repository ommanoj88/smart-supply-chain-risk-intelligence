import React, { useState, useEffect } from 'react';
import {
  Grid,
  Box,
  Typography,
  Card,
  CardContent,
  Button,
  Tabs,
  Tab,
  IconButton,
  useTheme,
  alpha,
  Chip,
  Alert,
  LinearProgress,
} from '@mui/material';
import {
  Refresh,
  Analytics,
  TrendingUp,
  Assessment,
  PrecisionManufacturing,
  Inventory,
  MonetizationOn,
  Timeline,
  Security,
  Warning,
  CheckCircle,
  Speed,
  Science,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import { 
  PremiumAreaChart, 
  PremiumBarChart, 
  PremiumLineChart, 
  PremiumPieChart,
} from '../common/PremiumCharts';
import { 
  PremiumKPICard,
  GlassCard,
  EnhancedFloatingActionButton,
} from '../ui/PremiumComponents';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel({ children, value, index, ...other }: TabPanelProps) {
  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`dashboard-tabpanel-${index}`}
      aria-labelledby={`dashboard-tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  );
}

/**
 * SAP-Level Enterprise Executive Dashboard
 * Comprehensive supply chain intelligence with advanced analytics,
 * predictive insights, and real-time monitoring capabilities
 */
export const EnterpriseExecutiveDashboard: React.FC = () => {
  const theme = useTheme();
  const [tabValue, setTabValue] = useState(0);
  const [loading, setLoading] = useState(false);
  
  // Enterprise-grade KPIs and metrics
  const [enterpriseKPIs, setEnterpriseKPIs] = useState({
    // Supply Chain Health Score (0-100)
    supplyChainHealth: 91.7,
    digitalMaturityIndex: 87.3,
    resilience: 94.2,
    sustainability: 82.5,
    
    // Financial Performance
    totalCostSavings: 12500000,
    roi: 284.5,
    costPerUnit: 47.82,
    workingCapital: 8200000,
    
    // Operational Excellence
    perfectOrderRate: 96.8,
    cycleTimeReduction: 23.4,
    capacityUtilization: 87.9,
    qualityIndex: 99.2,
    
    // Risk & Compliance
    riskExposure: 27.3,
    complianceScore: 98.1,
    supplierCertification: 94.7,
    auditScore: 96.5,
    
    // Innovation & Technology
    automationLevel: 78.3,
    aiDeployment: 65.7,
    dataQuality: 92.8,
    predictiveAccuracy: 89.4,
  });
  
  // Advanced analytics data
  const [planningMetrics, setPlanningMetrics] = useState({
    demandAccuracy: 89.4,
    supplyPlanAdherence: 92.1,
    inventoryTurnover: 14.2,
    serviceLevel: 97.3,
  });
  
  const [predictiveInsights, setPredictiveInsights] = useState([
    {
      type: 'DEMAND_FORECAST',
      confidence: 91.2,
      impact: 'HIGH',
      timeframe: '4 weeks',
      description: '18% demand increase predicted for automotive components',
      recommendation: 'Increase capacity allocation and safety stock levels',
    },
    {
      type: 'RISK_ALERT',
      confidence: 87.6,
      impact: 'MEDIUM',
      timeframe: '2 weeks',
      description: 'Weather disruption likely to affect Southeast Asia routes',
      recommendation: 'Activate alternative shipping routes and expedite critical shipments',
    },
    {
      type: 'COST_OPTIMIZATION',
      confidence: 94.3,
      impact: 'HIGH',
      timeframe: '6 months',
      description: 'Consolidation opportunity identified in European operations',
      recommendation: 'Consider supplier consolidation to achieve 12% cost reduction',
    },
  ]);
  
  const [sapModuleStatus, setSapModuleStatus] = useState([
    { module: 'Digital Manufacturing', status: 'online', performance: 98.2, lastSync: '2 min ago' },
    { module: 'Ariba Sourcing', status: 'online', performance: 96.7, lastSync: '1 min ago' },
    { module: 'IBP Planning', status: 'online', performance: 94.8, lastSync: '30 sec ago' },
    { module: 'Transportation Mgmt', status: 'warning', performance: 91.3, lastSync: '5 min ago' },
    { module: 'Quality Management', status: 'online', performance: 97.9, lastSync: '1 min ago' },
  ]);

  useEffect(() => {
    loadEnterpriseData();
    
    // Set up real-time updates
    const interval = setInterval(() => {
      updateRealTimeMetrics();
    }, 30000); // Update every 30 seconds
    
    return () => clearInterval(interval);
  }, []);

  const loadEnterpriseData = async () => {
    setLoading(true);
    try {
      // Load planning analytics
      const planningResponse = await fetch('/api/planning/analytics');
      if (planningResponse.ok) {
        const planningData = await planningResponse.json();
        if (planningData.success) {
          setPlanningMetrics({
            demandAccuracy: planningData.analytics.demandAccuracy.currentMonth,
            supplyPlanAdherence: planningData.analytics.supplyPerformance.planAdherence,
            inventoryTurnover: planningData.analytics.inventoryMetrics.turnover,
            serviceLevel: planningData.analytics.inventoryMetrics.serviceLevel,
          });
        }
      }
      
      // Load system stats
      const statsResponse = await fetch('/api/admin/data-stats');
      if (statsResponse.ok) {
        const statsData = await statsResponse.json();
        // Update enterprise KPIs with real data
        setEnterpriseKPIs(prev => ({
          ...prev,
          perfectOrderRate: statsData.onTimeDeliveryRate || prev.perfectOrderRate,
        }));
      }
    } catch (error) {
      console.error('Failed to load enterprise data:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateRealTimeMetrics = () => {
    // Simulate real-time updates with small variations
    setEnterpriseKPIs(prev => ({
      ...prev,
      supplyChainHealth: Math.max(85, Math.min(100, prev.supplyChainHealth + (Math.random() - 0.5) * 2)),
      riskExposure: Math.max(0, Math.min(50, prev.riskExposure + (Math.random() - 0.5) * 3)),
      capacityUtilization: Math.max(70, Math.min(95, prev.capacityUtilization + (Math.random() - 0.5) * 2)),
    }));
    
    // Update SAP module performance
    setSapModuleStatus(prev => prev.map(module => ({
      ...module,
      performance: Math.max(85, Math.min(100, module.performance + (Math.random() - 0.5) * 2)),
      lastSync: 'Just now',
    })));
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'online': return theme.palette.success.main;
      case 'warning': return theme.palette.warning.main;
      case 'offline': return theme.palette.error.main;
      default: return theme.palette.grey[500];
    }
  };

  const getImpactColor = (impact: string) => {
    switch (impact) {
      case 'HIGH': return theme.palette.error.main;
      case 'MEDIUM': return theme.palette.warning.main;
      case 'LOW': return theme.palette.success.main;
      default: return theme.palette.grey[500];
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
              Enterprise Supply Chain Intelligence
            </Typography>
            <Typography variant="subtitle1" color="text.secondary">
              SAP-Level Executive Dashboard with Advanced Analytics & AI Insights
            </Typography>
          </Box>
          
          <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
            <Chip
              label="Live"
              color="success"
              size="small"
              icon={<CheckCircle />}
              sx={{ animation: 'pulse 2s infinite' }}
            />
            <IconButton onClick={loadEnterpriseData} disabled={loading}>
              <Refresh />
            </IconButton>
          </Box>
        </Box>
      </motion.div>

      {/* Navigation Tabs */}
      <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 3 }}>
        <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)}>
          <Tab label="Executive Overview" icon={<Assessment />} />
          <Tab label="Planning & Analytics" icon={<Analytics />} />
          <Tab label="Predictive Insights" icon={<Science />} />
          <Tab label="SAP Integration" icon={<PrecisionManufacturing />} />
        </Tabs>
      </Box>

      {/* Executive Overview Tab */}
      <TabPanel value={tabValue} index={0}>
        {/* Top-Level KPIs */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} sm={6} md={3}>
            <PremiumKPICard
              title="Supply Chain Health"
              value={`${enterpriseKPIs.supplyChainHealth}%`}
              icon={<Timeline />}
              color="primary"
              trend={{ value: 2.1, direction: 'up' }}
              subtitle="Composite health score"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <PremiumKPICard
              title="Cost Savings YTD"
              value={`$${(enterpriseKPIs.totalCostSavings / 1000000).toFixed(1)}M`}
              icon={<MonetizationOn />}
              color="success"
              trend={{ value: 18.3, direction: 'up' }}
              subtitle="vs. baseline plan"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <PremiumKPICard
              title="Perfect Order Rate"
              value={`${enterpriseKPIs.perfectOrderRate}%`}
              icon={<CheckCircle />}
              color="info"
              trend={{ value: 1.4, direction: 'up' }}
              subtitle="Quality & delivery"
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <PremiumKPICard
              title="Risk Exposure"
              value={`${enterpriseKPIs.riskExposure}%`}
              icon={<Security />}
              color="warning"
              trend={{ value: 3.2, direction: 'down' }}
              subtitle="Enterprise risk index"
            />
          </Grid>
        </Grid>

        {/* Executive Performance Matrix */}
        <Grid container spacing={3} sx={{ mb: 4 }}>
          <Grid item xs={12} md={6}>
            <GlassCard>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Operational Excellence Matrix
                </Typography>
                
                <Grid container spacing={2}>
                  {[
                    { label: 'Digital Maturity', value: enterpriseKPIs.digitalMaturityIndex, target: 90 },
                    { label: 'Resilience Index', value: enterpriseKPIs.resilience, target: 95 },
                    { label: 'Sustainability Score', value: enterpriseKPIs.sustainability, target: 85 },
                    { label: 'Automation Level', value: enterpriseKPIs.automationLevel, target: 80 },
                  ].map((metric, index) => (
                    <Grid item xs={6} key={index}>
                      <Box sx={{ textAlign: 'center', p: 2 }}>
                        <Typography variant="h5" fontWeight="bold" 
                          color={metric.value >= metric.target ? 'success.main' : 'warning.main'}>
                          {metric.value}%
                        </Typography>
                        <Typography variant="body2" color="text.secondary">
                          {metric.label}
                        </Typography>
                        <LinearProgress
                          variant="determinate"
                          value={metric.value}
                          sx={{ mt: 1, height: 6, borderRadius: 3 }}
                          color={metric.value >= metric.target ? 'success' : 'warning'}
                        />
                      </Box>
                    </Grid>
                  ))}
                </Grid>
              </CardContent>
            </GlassCard>
          </Grid>

          <Grid item xs={12} md={6}>
            <GlassCard>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Financial Performance Trends
                </Typography>
                
                <PremiumAreaChart
                  data={[
                    { month: 'Jan', savings: 1800, costs: 450, roi: 280 },
                    { month: 'Feb', savings: 2200, costs: 380, roi: 285 },
                    { month: 'Mar', savings: 1950, costs: 520, roi: 275 },
                    { month: 'Apr', savings: 2400, costs: 410, roi: 290 },
                    { month: 'May', savings: 2100, costs: 470, roi: 282 },
                    { month: 'Jun', savings: 2650, costs: 390, roi: 295 },
                  ]}
                  xKey="month"
                  yKey="savings"
                  title=""
                  height={250}
                  loading={loading}
                  variant="premium"
                  animation={true}
                />
              </CardContent>
            </GlassCard>
          </Grid>
        </Grid>

        {/* Supply Chain Network Visualization */}
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <GlassCard>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Global Supply Chain Network Status
                </Typography>
                
                {/* Network visualization would go here in a real implementation */}
                <Box 
                  sx={{ 
                    height: 300, 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center',
                    backgroundColor: alpha(theme.palette.primary.main, 0.05),
                    borderRadius: 2,
                  }}
                >
                  <Typography variant="h6" color="text.secondary">
                    Interactive 3D Network Visualization
                    <br />
                    <Typography variant="body2">
                      Global supplier network with real-time status indicators
                    </Typography>
                  </Typography>
                </Box>
              </CardContent>
            </GlassCard>
          </Grid>
        </Grid>
      </TabPanel>

      {/* Planning & Analytics Tab */}
      <TabPanel value={tabValue} index={1}>
        <Grid container spacing={3}>
          {/* Planning KPIs */}
          <Grid item xs={12} sm={6} md={3}>
            <PremiumKPICard
              title="Demand Accuracy"
              value={`${planningMetrics.demandAccuracy}%`}
              icon={<TrendingUp />}
              color="primary"
              trend={{ value: 2.3, direction: 'up' }}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <PremiumKPICard
              title="Plan Adherence"
              value={`${planningMetrics.supplyPlanAdherence}%`}
              icon={<Assessment />}
              color="success"
              trend={{ value: 1.8, direction: 'up' }}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <PremiumKPICard
              title="Inventory Turnover"
              value={planningMetrics.inventoryTurnover}
              icon={<Inventory />}
              color="info"
              trend={{ value: 0.9, direction: 'up' }}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <PremiumKPICard
              title="Service Level"
              value={`${planningMetrics.serviceLevel}%`}
              icon={<Speed />}
              color="success"
              trend={{ value: 0.5, direction: 'up' }}
            />
          </Grid>

          {/* Advanced Analytics Charts */}
          <Grid item xs={12} md={8}>
            <GlassCard>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Supply-Demand Balance Analysis
                </Typography>
                
                <PremiumLineChart
                  data={[
                    { week: 'W1', demand: 2400, supply: 2450, gap: 50 },
                    { week: 'W2', demand: 2200, supply: 2180, gap: -20 },
                    { week: 'W3', demand: 2600, supply: 2580, gap: -20 },
                    { week: 'W4', demand: 2350, supply: 2400, gap: 50 },
                    { week: 'W5', demand: 2500, supply: 2520, gap: 20 },
                    { week: 'W6', demand: 2300, supply: 2350, gap: 50 },
                  ]}
                  xKey="week"
                  yKey="demand"
                  title=""
                  height={300}
                  loading={loading}
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
                  Planning Recommendations
                </Typography>
                
                <Box sx={{ maxHeight: 300, overflowY: 'auto' }}>
                  {[
                    { type: 'CAPACITY', priority: 'HIGH', action: 'Increase capacity for Q3' },
                    { type: 'INVENTORY', priority: 'MEDIUM', action: 'Optimize safety stock levels' },
                    { type: 'SOURCING', priority: 'MEDIUM', action: 'Diversify supplier base' },
                    { type: 'DEMAND', priority: 'LOW', action: 'Review forecast models' },
                  ].map((rec, index) => (
                    <Alert
                      key={index}
                      severity={rec.priority === 'HIGH' ? 'error' : rec.priority === 'MEDIUM' ? 'warning' : 'info'}
                      sx={{ mb: 1 }}
                    >
                      <Typography variant="body2" fontWeight="bold">
                        {rec.type}
                      </Typography>
                      <Typography variant="caption">
                        {rec.action}
                      </Typography>
                    </Alert>
                  ))}
                </Box>
              </CardContent>
            </GlassCard>
          </Grid>
        </Grid>
      </TabPanel>

      {/* Predictive Insights Tab */}
      <TabPanel value={tabValue} index={2}>
        <Grid container spacing={3}>
          {predictiveInsights.map((insight, index) => (
            <Grid item xs={12} md={4} key={index}>
              <GlassCard>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Typography variant="h6" color={getImpactColor(insight.impact)}>
                      {insight.type.replace('_', ' ')}
                    </Typography>
                    <Chip
                      label={`${insight.confidence}% confidence`}
                      size="small"
                      color={insight.confidence > 90 ? 'success' : insight.confidence > 80 ? 'warning' : 'default'}
                    />
                  </Box>
                  
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Impact: {insight.impact} • Timeframe: {insight.timeframe}
                  </Typography>
                  
                  <Typography variant="body1" gutterBottom>
                    {insight.description}
                  </Typography>
                  
                  <Typography variant="body2" fontWeight="bold" color="primary.main">
                    Recommendation: {insight.recommendation}
                  </Typography>
                </CardContent>
              </GlassCard>
            </Grid>
          ))}
        </Grid>
      </TabPanel>

      {/* SAP Integration Tab */}
      <TabPanel value={tabValue} index={3}>
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <GlassCard>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  SAP Module Integration Status
                </Typography>
                
                <Grid container spacing={2}>
                  {sapModuleStatus.map((module, index) => (
                    <Grid item xs={12} sm={6} md={4} key={index}>
                      <Box 
                        sx={{ 
                          p: 2, 
                          border: 1, 
                          borderColor: 'divider', 
                          borderRadius: 2,
                          backgroundColor: alpha(getStatusColor(module.status), 0.05),
                        }}
                      >
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                          <Typography variant="subtitle1" fontWeight="bold">
                            {module.module}
                          </Typography>
                          <Chip
                            label={module.status.toUpperCase()}
                            size="small"
                            sx={{ 
                              backgroundColor: getStatusColor(module.status),
                              color: 'white',
                            }}
                          />
                        </Box>
                        
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                          Performance: {module.performance}%
                        </Typography>
                        
                        <LinearProgress
                          variant="determinate"
                          value={module.performance}
                          sx={{ mb: 1, height: 6, borderRadius: 3 }}
                          color={module.performance > 95 ? 'success' : module.performance > 90 ? 'warning' : 'error'}
                        />
                        
                        <Typography variant="caption" color="text.secondary">
                          Last sync: {module.lastSync}
                        </Typography>
                      </Box>
                    </Grid>
                  ))}
                </Grid>
              </CardContent>
            </GlassCard>
          </Grid>
        </Grid>
      </TabPanel>

      {/* Floating Action Button */}
      <EnhancedFloatingActionButton
        icon={<Analytics />}
        onClick={() => loadEnterpriseData()}
        color="primary"
        size="medium"
      />
    </Box>
  );
};

export default EnterpriseExecutiveDashboard;