import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Button,
  CircularProgress,
  Alert,
  Chip,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Tooltip,
  LinearProgress
} from '@mui/material';
import {
  Psychology,
  TrendingUp,
  TrendingDown,
  TrendingFlat,
  Lightbulb,
  Security,
  LocalShipping,
  MonetizationOn,
  CheckCircle,
  Warning,
  Error,
  Refresh,
  ThumbUp,
  ThumbDown
} from '@mui/icons-material';

interface RiskAssessment {
  supplierId: number;
  supplierName: string;
  currentRisk: Record<string, number>;
  predictedRisk: any;
  riskTrend: any;
  alerts: any[];
  confidence: number;
  lastUpdated: string;
  recommendations: string[];
}

interface AIInsight {
  id: string;
  type: 'RECOMMENDATION' | 'ALERT' | 'TREND' | 'OPPORTUNITY';
  title: string;
  description: string;
  confidence: number;
  impact: 'LOW' | 'MEDIUM' | 'HIGH';
  category: 'RISK' | 'COST' | 'QUALITY' | 'DELIVERY';
  actions: string[];
  generatedAt: string;
}

interface AIInsightsPanelProps {
  supplierId: number;
}

const AIInsightsPanel: React.FC<AIInsightsPanelProps> = ({ supplierId }) => {
  const [riskAssessment, setRiskAssessment] = useState<RiskAssessment | null>(null);
  const [insights, setInsights] = useState<AIInsight[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchRiskAssessment();
    generateInsights();
    
    // Set up real-time updates (WebSocket simulation)
    const interval = setInterval(() => {
      fetchRiskAssessment();
    }, 30000); // Update every 30 seconds

    return () => clearInterval(interval);
  }, [supplierId]);

  const fetchRiskAssessment = async () => {
    try {
      const response = await fetch(`/api/analytics/risk-assessment/${supplierId}`);
      
      if (!response.ok) {
        throw new Error('Failed to fetch risk assessment');
      }

      const data = await response.json();
      setRiskAssessment(data);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
    }
  };

  const generateInsights = () => {
    // Generate simulated AI insights for demonstration
    const mockInsights: AIInsight[] = [
      {
        id: '1',
        type: 'RECOMMENDATION',
        title: 'Alternative Supplier Opportunity',
        description: 'AI analysis suggests Supplier #1042 offers 15% cost savings with similar quality metrics',
        confidence: 87,
        impact: 'HIGH',
        category: 'COST',
        actions: [
          'Request quote from Supplier #1042',
          'Compare quality certifications',
          'Evaluate delivery capabilities'
        ],
        generatedAt: new Date().toISOString()
      },
      {
        id: '2',
        type: 'ALERT',
        title: 'Risk Trend Increasing',
        description: 'Delivery performance has decreased by 8% over the last 30 days, indicating potential capacity issues',
        confidence: 93,
        impact: 'MEDIUM',
        category: 'DELIVERY',
        actions: [
          'Schedule capacity review meeting',
          'Activate backup supplier',
          'Adjust safety stock levels'
        ],
        generatedAt: new Date().toISOString()
      },
      {
        id: '3',
        type: 'OPPORTUNITY',
        title: 'Quality Improvement Detected',
        description: 'Recent quality metrics show 12% improvement, suggesting optimized processes',
        confidence: 78,
        impact: 'MEDIUM',
        category: 'QUALITY',
        actions: [
          'Document best practices',
          'Consider increasing order volume',
          'Nominate for preferred supplier status'
        ],
        generatedAt: new Date().toISOString()
      }
    ];

    setInsights(mockInsights);
    setLoading(false);
  };

  const handleInsightAction = (insightId: string, action: string) => {
    // Simulate taking action on an insight
    console.log(`Taking action "${action}" for insight ${insightId}`);
    
    // In a real implementation, this would trigger the appropriate business process
    setInsights(prev => prev.map(insight => 
      insight.id === insightId 
        ? { ...insight, actions: insight.actions.filter(a => a !== action) }
        : insight
    ));
  };

  const getRiskGaugeColor = (risk: number) => {
    if (risk <= 30) return '#4caf50';
    if (risk <= 60) return '#ff9800';
    return '#f44336';
  };

  const getImpactColor = (impact: string) => {
    switch (impact) {
      case 'HIGH': return '#f44336';
      case 'MEDIUM': return '#ff9800';
      case 'LOW': return '#4caf50';
      default: return '#9e9e9e';
    }
  };

  const getCategoryIcon = (category: string) => {
    switch (category) {
      case 'RISK': return <Security />;
      case 'COST': return <MonetizationOn />;
      case 'QUALITY': return <CheckCircle />;
      case 'DELIVERY': return <LocalShipping />;
      default: return <Lightbulb />;
    }
  };

  const getInsightIcon = (type: string) => {
    switch (type) {
      case 'RECOMMENDATION': return <Lightbulb color="primary" />;
      case 'ALERT': return <Warning color="warning" />;
      case 'TREND': return <TrendingUp color="info" />;
      case 'OPPORTUNITY': return <CheckCircle color="success" />;
      default: return <Psychology />;
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="300px">
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>
          Generating AI Insights...
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mb: 2 }}>
        {error}
        <IconButton onClick={fetchRiskAssessment} color="inherit" size="small">
          <Refresh />
        </IconButton>
      </Alert>
    );
  }

  return (
    <Box sx={{ p: 2 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" gutterBottom>
          <Psychology sx={{ mr: 1, verticalAlign: 'middle' }} />
          AI Insights Panel
        </Typography>
        <Tooltip title="Refresh Insights">
          <IconButton onClick={() => {
            setLoading(true);
            fetchRiskAssessment();
            generateInsights();
          }} color="primary">
            <Refresh />
          </IconButton>
        </Tooltip>
      </Box>

      <Grid container spacing={3}>
        {/* Real-time Risk Assessment */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Real-time Risk Assessment
              </Typography>
              {riskAssessment && (
                <Box>
                  <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                    {riskAssessment.supplierName}
                  </Typography>
                  
                  {/* Risk Gauge */}
                  <Box textAlign="center" mb={2}>
                    <Typography variant="h2" color={getRiskGaugeColor(riskAssessment.currentRisk.overall)}>
                      {riskAssessment.currentRisk.overall}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Overall Risk Score
                    </Typography>
                    <LinearProgress 
                      variant="determinate" 
                      value={riskAssessment.currentRisk.overall} 
                      sx={{ 
                        mt: 1, 
                        height: 8, 
                        borderRadius: 4,
                        backgroundColor: '#f0f0f0',
                        '& .MuiLinearProgress-bar': {
                          backgroundColor: getRiskGaugeColor(riskAssessment.currentRisk.overall)
                        }
                      }}
                    />
                  </Box>

                  {/* Risk Breakdown */}
                  <Grid container spacing={1}>
                    <Grid item xs={6}>
                      <Typography variant="caption">Financial</Typography>
                      <Typography variant="h6" color={getRiskGaugeColor(riskAssessment.currentRisk.financial)}>
                        {riskAssessment.currentRisk.financial}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="caption">Operational</Typography>
                      <Typography variant="h6" color={getRiskGaugeColor(riskAssessment.currentRisk.operational)}>
                        {riskAssessment.currentRisk.operational}
                      </Typography>
                    </Grid>
                  </Grid>

                  <Box mt={2}>
                    <Typography variant="caption" color="text.secondary">
                      Prediction Confidence: {riskAssessment.confidence}%
                    </Typography>
                  </Box>
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* AI-Generated Insights */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                AI-Generated Insights
              </Typography>
              <List dense>
                {insights.map((insight) => (
                  <React.Fragment key={insight.id}>
                    <ListItem alignItems="flex-start">
                      <ListItemIcon>
                        {getInsightIcon(insight.type)}
                      </ListItemIcon>
                      <ListItemText
                        primary={
                          <Box display="flex" alignItems="center" gap={1}>
                            <Typography variant="subtitle2">{insight.title}</Typography>
                            <Chip 
                              label={insight.impact} 
                              size="small" 
                              sx={{ 
                                backgroundColor: getImpactColor(insight.impact),
                                color: 'white',
                                fontSize: '0.7rem'
                              }}
                            />
                          </Box>
                        }
                        secondary={
                          <Box>
                            <Typography variant="body2" color="text.secondary" paragraph>
                              {insight.description}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                              Confidence: {insight.confidence}% • {getCategoryIcon(insight.category)} {insight.category}
                            </Typography>
                          </Box>
                        }
                      />
                    </ListItem>
                    <Divider variant="inset" component="li" />
                  </React.Fragment>
                ))}
              </List>
            </CardContent>
          </Card>
        </Grid>

        {/* Smart Recommendations */}
        <Grid item xs={12}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Smart Recommendations
              </Typography>
              <Grid container spacing={2}>
                {insights.filter(insight => insight.type === 'RECOMMENDATION').map((recommendation) => (
                  <Grid item xs={12} md={6} key={recommendation.id}>
                    <Card variant="outlined" sx={{ 
                      bgcolor: recommendation.impact === 'HIGH' ? '#f3e5f5' : '#e8f5e8',
                      border: `1px solid ${getImpactColor(recommendation.impact)}`
                    }}>
                      <CardContent>
                        <Box display="flex" alignItems="center" mb={1}>
                          <Lightbulb color="primary" sx={{ mr: 1 }} />
                          <Typography variant="subtitle1" fontWeight="bold">
                            {recommendation.title}
                          </Typography>
                        </Box>
                        
                        <Typography variant="body2" color="text.secondary" mb={2}>
                          {recommendation.description}
                        </Typography>

                        <Typography variant="subtitle2" mb={1}>
                          Recommended Actions:
                        </Typography>
                        
                        {recommendation.actions.map((action, index) => (
                          <Box key={index} display="flex" alignItems="center" justifyContent="space-between" mb={1}>
                            <Typography variant="body2">{action}</Typography>
                            <Box>
                              <IconButton 
                                size="small" 
                                color="success"
                                onClick={() => handleInsightAction(recommendation.id, action)}
                              >
                                <ThumbUp fontSize="small" />
                              </IconButton>
                              <IconButton 
                                size="small" 
                                color="error"
                                onClick={() => handleInsightAction(recommendation.id, action)}
                              >
                                <ThumbDown fontSize="small" />
                              </IconButton>
                            </Box>
                          </Box>
                        ))}

                        <Box mt={2} display="flex" justifyContent="space-between" alignItems="center">
                          <Typography variant="caption" color="text.secondary">
                            Confidence: {recommendation.confidence}%
                          </Typography>
                          <Button 
                            size="small" 
                            variant="outlined" 
                            onClick={() => console.log('View details for', recommendation.id)}
                          >
                            View Details
                          </Button>
                        </Box>
                      </CardContent>
                    </Card>
                  </Grid>
                ))}
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Trend Analysis */}
        {riskAssessment && (
          <Grid item xs={12}>
            <Card>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Risk Trend Analysis
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={4}>
                    <Box textAlign="center">
                      {riskAssessment.riskTrend.trend_direction === 'INCREASING' ? 
                        <TrendingUp color="error" sx={{ fontSize: 40 }} /> :
                        <TrendingDown color="success" sx={{ fontSize: 40 }} />
                      }
                      <Typography variant="h6">
                        {riskAssessment.riskTrend.trend_direction}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Overall Trend
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={4}>
                    <Box textAlign="center">
                      <Typography variant="h4" color={
                        Math.abs(riskAssessment.riskTrend.overall_trend) > 10 ? '#f44336' : '#4caf50'
                      }>
                        {riskAssessment.riskTrend.overall_trend > 0 ? '+' : ''}
                        {riskAssessment.riskTrend.overall_trend.toFixed(1)}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        30-Day Change
                      </Typography>
                    </Box>
                  </Grid>
                  <Grid item xs={12} sm={4}>
                    <Box textAlign="center">
                      <Typography variant="h4" color="info.main">
                        {riskAssessment.alerts.length}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        Active Alerts
                      </Typography>
                    </Box>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        )}
      </Grid>

      {/* Footer */}
      <Box mt={2} textAlign="center">
        <Typography variant="caption" color="text.secondary">
          AI insights updated in real-time • Last update: {new Date().toLocaleTimeString()}
        </Typography>
      </Box>
    </Box>
  );
};

export default AIInsightsPanel;