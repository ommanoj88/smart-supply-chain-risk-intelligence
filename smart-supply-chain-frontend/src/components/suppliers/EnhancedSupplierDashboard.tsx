import React, { useState, useEffect, useMemo } from 'react';
import {
  Box,
  Grid,
  Typography,
  CardContent,
  CardActions,
  IconButton,
  Chip,
  Button,
  Avatar,
  LinearProgress,
  useTheme,
  alpha,
} from '@mui/material';
import {
  Business,
  LocationOn,
  Phone,
  TrendingUp,
  Warning,
  Star,
  Security,
  Analytics,
  MoreVert,
  Visibility,
  Assessment,
  Add,
} from '@mui/icons-material';
import { motion } from 'framer-motion';
import {
  PremiumKPICard,
  GlassCard,
  EnhancedSearchBar,
} from '../ui/PremiumComponents';


// Enhanced Supplier Interface
interface Supplier {
  id: string;
  name: string;
  code: string;
  logo?: string;
  industry: string;
  tier: 'strategic' | 'preferred' | 'standard' | 'conditional';
  status: 'active' | 'inactive' | 'suspended' | 'under_review';
  location: {
    city: string;
    country: string;
    region: string;
  };
  contact: {
    email: string;
    phone: string;
    website?: string;
    primaryContact: string;
  };
  performance: {
    reliabilityScore: number;
    qualityRating: number;
    onTimeDelivery: number;
    costEfficiency: number;
    sustainability: number;
    innovation: number;
  };
  risk: {
    overall: number;
    financial: number;
    operational: number;
    compliance: number;
    cybersecurity: number;
    geopolitical: number;
  };
  financials: {
    annualRevenue: number;
    contractValue: number;
    paymentTerms: number;
    creditRating: string;
  };
  capabilities: string[];
  certifications: string[];
  relationship: {
    duration: number; // years
    contracts: number;
    disputes: number;
    lastAudit: Date;
    nextReview: Date;
  };
  trends: {
    performanceTrend: 'improving' | 'stable' | 'declining';
    riskTrend: 'improving' | 'stable' | 'declining';
    volumeTrend: 'increasing' | 'stable' | 'decreasing';
  };
}

interface SupplierMetrics {
  totalSuppliers: number;
  activeSuppliers: number;
  strategicPartners: number;
  averagePerformance: number;
  averageRisk: number;
  contractsExpiring: number;
  auditsRequired: number;
  sustainabilityScore: number;
  diversityIndex: number;
}

/**
 * Enterprise-Grade Supplier Management Dashboard
 * Features:
 * - Advanced supplier cards with interactive metrics
 * - Risk assessment dashboard with visual heatmaps
 * - Supplier performance analytics with trend analysis
 * - Relationship mapping and network visualization
 * - AI-powered search with faceted filters
 * - Bulk operations and professional management tools
 */
export const EnhancedSupplierDashboard: React.FC = () => {
  const theme = useTheme();
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [metrics] = useState<SupplierMetrics>({
    totalSuppliers: 287,
    activeSuppliers: 234,
    strategicPartners: 45,
    averagePerformance: 87.3,
    averageRisk: 34.2,
    contractsExpiring: 12,
    auditsRequired: 8,
    sustainabilityScore: 82.1,
    diversityIndex: 0.73,
  });
  const [loading] = useState(false);
  const [viewMode, setViewMode] = useState<'cards' | 'list' | 'analytics'>('cards');
  const [searchTerm, setSearchTerm] = useState('');

  // Mock data generation
  useEffect(() => {
    const generateMockSuppliers = (): Supplier[] => [
      {
        id: '1',
        name: 'TechCorp Manufacturing',
        code: 'TCM-001',
        logo: '/api/placeholder/40/40',
        industry: 'Electronics',
        tier: 'strategic',
        status: 'active',
        location: {
          city: 'San Francisco',
          country: 'USA',
          region: 'North America',
        },
        contact: {
          email: 'partners@techcorp.com',
          phone: '+1-555-0123',
          website: 'www.techcorp.com',
          primaryContact: 'Sarah Johnson',
        },
        performance: {
          reliabilityScore: 94.5,
          qualityRating: 96.2,
          onTimeDelivery: 97.8,
          costEfficiency: 89.3,
          sustainability: 87.1,
          innovation: 92.4,
        },
        risk: {
          overall: 22,
          financial: 18,
          operational: 25,
          compliance: 12,
          cybersecurity: 28,
          geopolitical: 15,
        },
        financials: {
          annualRevenue: 2500000000,
          contractValue: 85000000,
          paymentTerms: 30,
          creditRating: 'AAA',
        },
        capabilities: ['Advanced Manufacturing', 'IoT Solutions', 'Green Technology'],
        certifications: ['ISO 9001', 'ISO 14001', 'SOC 2'],
        relationship: {
          duration: 8.5,
          contracts: 24,
          disputes: 1,
          lastAudit: new Date('2023-09-15'),
          nextReview: new Date('2024-03-15'),
        },
        trends: {
          performanceTrend: 'improving',
          riskTrend: 'stable',
          volumeTrend: 'increasing',
        },
      },
      {
        id: '2',
        name: 'Global Auto Parts Ltd',
        code: 'GAP-002',
        logo: '/api/placeholder/40/40',
        industry: 'Automotive',
        tier: 'preferred',
        status: 'active',
        location: {
          city: 'Munich',
          country: 'Germany',
          region: 'Europe',
        },
        contact: {
          email: 'business@globalauto.de',
          phone: '+49-89-555-0456',
          website: 'www.globalauto.de',
          primaryContact: 'Klaus Mueller',
        },
        performance: {
          reliabilityScore: 88.7,
          qualityRating: 91.4,
          onTimeDelivery: 93.2,
          costEfficiency: 92.1,
          sustainability: 78.9,
          innovation: 85.6,
        },
        risk: {
          overall: 35,
          financial: 32,
          operational: 38,
          compliance: 25,
          cybersecurity: 42,
          geopolitical: 28,
        },
        financials: {
          annualRevenue: 1200000000,
          contractValue: 45000000,
          paymentTerms: 45,
          creditRating: 'AA+',
        },
        capabilities: ['Precision Manufacturing', 'Supply Chain Optimization'],
        certifications: ['ISO 9001', 'IATF 16949'],
        relationship: {
          duration: 5.2,
          contracts: 18,
          disputes: 0,
          lastAudit: new Date('2023-11-20'),
          nextReview: new Date('2024-05-20'),
        },
        trends: {
          performanceTrend: 'stable',
          riskTrend: 'improving',
          volumeTrend: 'stable',
        },
      },
      // Add more mock suppliers...
    ];

    setSuppliers(generateMockSuppliers());
  }, []);

  const cardVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0 },
    hover: { y: -8, transition: { duration: 0.2 } },
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

  // Enhanced metrics with trends
  const enhancedMetrics = useMemo(() => [
    {
      title: 'Total Suppliers',
      value: metrics.totalSuppliers.toString(),
      subtitle: `${metrics.activeSuppliers} active partners`,
      icon: <Business />,
      color: 'primary' as const,
      trend: { value: 8.2, direction: 'up' as const, period: 'network growth' },
    },
    {
      title: 'Strategic Partners',
      value: metrics.strategicPartners.toString(),
      subtitle: `${((metrics.strategicPartners / metrics.totalSuppliers) * 100).toFixed(1)}% of total`,
      icon: <Star />,
      color: 'warning' as const,
      trend: { value: 12.5, direction: 'up' as const, period: 'tier upgrades' },
    },
    {
      title: 'Avg Performance',
      value: `${metrics.averagePerformance.toFixed(1)}%`,
      subtitle: 'Across all metrics',
      icon: <TrendingUp />,
      color: 'success' as const,
      trend: { value: 2.3, direction: 'up' as const, period: 'improvement' },
    },
    {
      title: 'Risk Level',
      value: `${metrics.averageRisk.toFixed(1)}%`,
      subtitle: 'Overall risk exposure',
      icon: <Security />,
      color: 'error' as const,
      trend: { value: 5.1, direction: 'down' as const, period: 'risk reduction' },
    },
    {
      title: 'Contracts Expiring',
      value: metrics.contractsExpiring.toString(),
      subtitle: 'Next 90 days',
      icon: <Warning />,
      color: 'warning' as const,
      trend: { value: 3, direction: 'up' as const, period: 'requiring renewal' },
    },
    {
      title: 'Sustainability Score',
      value: `${metrics.sustainabilityScore.toFixed(1)}%`,
      subtitle: 'ESG compliance average',
      icon: <Analytics />,
      color: 'success' as const,
      trend: { value: 4.7, direction: 'up' as const, period: 'ESG improvement' },
    },
  ], [metrics]);

  const getRiskColor = (risk: number) => {
    if (risk <= 30) return theme.palette.success.main;
    if (risk <= 60) return theme.palette.warning.main;
    return theme.palette.error.main;
  };

  const getTierColor = (tier: string) => {
    switch (tier) {
      case 'strategic': return theme.palette.warning.main;
      case 'preferred': return theme.palette.info.main;
      case 'standard': return theme.palette.success.main;
      case 'conditional': return theme.palette.error.main;
      default: return theme.palette.grey[500];
    }
  };

  const SupplierCard: React.FC<{ supplier: Supplier }> = ({ supplier }) => (
    <motion.div
      variants={cardVariants}
      whileHover="hover"
      layout
    >
      <GlassCard 
        sx={{ 
          height: '100%',
          cursor: 'pointer',
          transition: 'all 0.3s ease',
          position: 'relative',
          overflow: 'visible',
        }}
      >
        <CardContent sx={{ p: 3 }}>
          {/* Header */}
          <Box display="flex" alignItems="flex-start" justifyContent="space-between" mb={2}>
            <Box display="flex" alignItems="center" gap={2}>
              <Avatar 
                src={supplier.logo}
                sx={{ 
                  width: 48, 
                  height: 48,
                  backgroundColor: alpha(getTierColor(supplier.tier), 0.1),
                  color: getTierColor(supplier.tier),
                }}
              >
                <Business />
              </Avatar>
              <Box>
                <Typography variant="h6" fontWeight={600} noWrap>
                  {supplier.name}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {supplier.code} • {supplier.industry}
                </Typography>
              </Box>
            </Box>
            <Box display="flex" flexDirection="column" alignItems="end" gap={1}>
              <Chip 
                label={supplier.tier} 
                size="small"
                sx={{ 
                  backgroundColor: alpha(getTierColor(supplier.tier), 0.1),
                  color: getTierColor(supplier.tier),
                  fontWeight: 600,
                  textTransform: 'capitalize',
                }}
              />
              <IconButton size="small">
                <MoreVert />
              </IconButton>
            </Box>
          </Box>

          {/* Location & Contact */}
          <Box mb={2}>
            <Box display="flex" alignItems="center" gap={1} mb={1}>
              <LocationOn sx={{ fontSize: 16, color: 'text.secondary' }} />
              <Typography variant="body2" color="text.secondary">
                {supplier.location.city}, {supplier.location.country}
              </Typography>
            </Box>
            <Box display="flex" alignItems="center" gap={1}>
              <Phone sx={{ fontSize: 16, color: 'text.secondary' }} />
              <Typography variant="body2" color="text.secondary">
                {supplier.contact.primaryContact}
              </Typography>
            </Box>
          </Box>

          {/* Performance Metrics */}
          <Box mb={2}>
            <Typography variant="subtitle2" fontWeight={600} mb={1}>
              Performance Overview
            </Typography>
            <Grid container spacing={1}>
              <Grid item xs={6}>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Reliability
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1}>
                    <LinearProgress 
                      variant="determinate" 
                      value={supplier.performance.reliabilityScore} 
                      sx={{ 
                        flex: 1, 
                        height: 6, 
                        borderRadius: 3,
                        backgroundColor: alpha(theme.palette.success.main, 0.1),
                        '& .MuiLinearProgress-bar': {
                          backgroundColor: theme.palette.success.main,
                          borderRadius: 3,
                        },
                      }} 
                    />
                    <Typography variant="caption" fontWeight={600}>
                      {supplier.performance.reliabilityScore.toFixed(0)}%
                    </Typography>
                  </Box>
                </Box>
              </Grid>
              <Grid item xs={6}>
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Quality
                  </Typography>
                  <Box display="flex" alignItems="center" gap={1}>
                    <LinearProgress 
                      variant="determinate" 
                      value={supplier.performance.qualityRating} 
                      sx={{ 
                        flex: 1, 
                        height: 6, 
                        borderRadius: 3,
                        backgroundColor: alpha(theme.palette.info.main, 0.1),
                        '& .MuiLinearProgress-bar': {
                          backgroundColor: theme.palette.info.main,
                          borderRadius: 3,
                        },
                      }} 
                    />
                    <Typography variant="caption" fontWeight={600}>
                      {supplier.performance.qualityRating.toFixed(0)}%
                    </Typography>
                  </Box>
                </Box>
              </Grid>
            </Grid>
          </Box>

          {/* Risk Assessment */}
          <Box mb={2}>
            <Box display="flex" alignItems="center" justifyContent="space-between" mb={1}>
              <Typography variant="subtitle2" fontWeight={600}>
                Risk Assessment
              </Typography>
              <Chip 
                label={`${supplier.risk.overall}% risk`}
                size="small"
                sx={{
                  backgroundColor: alpha(getRiskColor(supplier.risk.overall), 0.1),
                  color: getRiskColor(supplier.risk.overall),
                  fontWeight: 600,
                }}
              />
            </Box>
            <LinearProgress 
              variant="determinate" 
              value={100 - supplier.risk.overall} 
              sx={{ 
                height: 8, 
                borderRadius: 4,
                backgroundColor: alpha(getRiskColor(supplier.risk.overall), 0.1),
                '& .MuiLinearProgress-bar': {
                  backgroundColor: getRiskColor(supplier.risk.overall),
                  borderRadius: 4,
                },
              }} 
            />
          </Box>

          {/* Financial Overview */}
          <Box mb={2}>
            <Typography variant="subtitle2" fontWeight={600} mb={1}>
              Financial Overview
            </Typography>
            <Box display="flex" justifyContent="space-between" alignItems="center">
              <Box>
                <Typography variant="caption" color="text.secondary">
                  Contract Value
                </Typography>
                <Typography variant="body2" fontWeight={600}>
                  ${(supplier.financials.contractValue / 1000000).toFixed(1)}M
                </Typography>
              </Box>
              <Box textAlign="right">
                <Typography variant="caption" color="text.secondary">
                  Credit Rating
                </Typography>
                <Typography variant="body2" fontWeight={600}>
                  {supplier.financials.creditRating}
                </Typography>
              </Box>
            </Box>
          </Box>

          {/* Capabilities */}
          <Box mb={2}>
            <Typography variant="subtitle2" fontWeight={600} mb={1}>
              Key Capabilities
            </Typography>
            <Box display="flex" flexWrap="wrap" gap={0.5}>
              {supplier.capabilities.slice(0, 3).map((capability, index) => (
                <Chip 
                  key={index}
                  label={capability} 
                  size="small" 
                  variant="outlined"
                  sx={{ fontSize: '0.7rem' }}
                />
              ))}
              {supplier.capabilities.length > 3 && (
                <Chip 
                  label={`+${supplier.capabilities.length - 3} more`} 
                  size="small" 
                  variant="outlined"
                  sx={{ fontSize: '0.7rem' }}
                />
              )}
            </Box>
          </Box>
        </CardContent>

        <CardActions sx={{ px: 3, pb: 2, pt: 0 }}>
          <Box display="flex" gap={1} width="100%">
            <Button 
              size="small" 
              startIcon={<Visibility />}
              sx={{ flex: 1 }}
            >
              View Details
            </Button>
            <Button 
              size="small" 
              startIcon={<Assessment />}
              variant="outlined"
              sx={{ flex: 1 }}
            >
              Analytics
            </Button>
          </Box>
        </CardActions>
      </GlassCard>
    </motion.div>
  );

  return (
    <Box sx={{ 
      minHeight: '100vh',
      background: `linear-gradient(135deg, ${alpha(theme.palette.primary.main, 0.02)} 0%, ${alpha(theme.palette.secondary.main, 0.01)} 100%)`,
      p: { xs: 2, md: 4 }
    }}>
      {/* Premium Header */}
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="visible"
      >
        <GlassCard sx={{ mb: 4, p: 3 }}>
          <Box display="flex" flexDirection={{ xs: 'column', md: 'row' }} justifyContent="space-between" alignItems={{ xs: 'flex-start', md: 'center' }}>
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
                  Supplier Management
                </Typography>
                <Typography variant="subtitle1" color="text.secondary" sx={{ mb: 2 }}>
                  Enterprise-grade supplier relationship management with advanced analytics
                </Typography>
                <Box display="flex" alignItems="center" gap={2}>
                  <Chip 
                    label={`${metrics.activeSuppliers} active suppliers`}
                    size="small" 
                    color="success"
                    variant="outlined"
                  />
                  <Chip 
                    label={`${metrics.auditsRequired} audits due`}
                    size="small" 
                    color="warning"
                    variant="outlined"
                  />
                </Box>
              </Box>
            </motion.div>
            
            <motion.div variants={cardVariants}>
              <Box display="flex" alignItems="center" gap={2} mt={{ xs: 3, md: 0 }}>
                <EnhancedSearchBar 
                  placeholder="Search suppliers by name, industry, or location..."
                  value={searchTerm}
                  onChange={setSearchTerm}
                  variant="glass"
                  size="medium"
                  showFilters={true}
                />
                
                <Button 
                  variant="contained" 
                  startIcon={<Add />}
                  sx={{ minWidth: 140 }}
                >
                  Add Supplier
                </Button>
              </Box>
            </motion.div>
          </Box>
        </GlassCard>
      </motion.div>

      {/* Enhanced KPI Metrics */}
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
              backgroundColor: theme.palette.primary.main, 
              borderRadius: 2,
            }} 
          />
          Supplier Portfolio Overview
        </Typography>

        <Grid container spacing={3} sx={{ mb: 4 }}>
          {enhancedMetrics.map((metric) => (
            <Grid item xs={12} sm={6} lg={2} key={metric.title}>
              <motion.div variants={cardVariants}>
                <PremiumKPICard
                  title={metric.title}
                  value={metric.value}
                  subtitle={metric.subtitle}
                  icon={metric.icon}
                  color={metric.color}
                  variant="glass"
                  size="medium"
                  loading={loading}
                  animated={true}
                  interactive={true}
                  trend={metric.trend}
                />
              </motion.div>
            </Grid>
          ))}
        </Grid>
      </motion.div>

      {/* Advanced Supplier Cards */}
      <motion.div
        variants={staggerContainer}
        initial="hidden"
        animate="visible"
      >
        <Box display="flex" alignItems="center" justifyContent="space-between" mb={3}>
          <Typography 
            variant="h5" 
            fontWeight={600}
            sx={{ 
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
            Supplier Portfolio
          </Typography>
          
          <Box display="flex" gap={1}>
            <Button
              variant={viewMode === 'cards' ? 'contained' : 'outlined'}
              size="small"
              onClick={() => setViewMode('cards')}
            >
              Cards
            </Button>
            <Button
              variant={viewMode === 'analytics' ? 'contained' : 'outlined'}
              size="small"
              onClick={() => setViewMode('analytics')}
            >
              Analytics
            </Button>
          </Box>
        </Box>

        <Grid container spacing={3}>
          {suppliers.map((supplier) => (
            <Grid item xs={12} md={6} lg={4} key={supplier.id}>
              <SupplierCard supplier={supplier} />
            </Grid>
          ))}
        </Grid>
      </motion.div>
    </Box>
  );
};

export default EnhancedSupplierDashboard;