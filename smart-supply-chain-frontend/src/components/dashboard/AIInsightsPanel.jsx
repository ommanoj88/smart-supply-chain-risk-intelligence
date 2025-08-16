import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {
  Card, CardContent, CardHeader, Typography, Button, Box,
  Grid, Chip, Alert, LinearProgress, Gauge, Accordion,
  AccordionSummary, AccordionDetails, Avatar, List,
  ListItem, ListItemText, ListItemAvatar, IconButton,
  Tooltip, CircularProgress
} from '@mui/material';
import {
  Psychology, TrendingUp, TrendingDown, Warning,
  CheckCircle, Speed, Security, Insights, SmartToy,
  ExpandMore, ThumbUp, ThumbDown, Refresh
} from '@mui/icons-material';

const AIInsightsPanel = ({ supplierId = null }) => {
  const [insights, setInsights] = useState([]);
  const [realTimeRisk, setRealTimeRisk] = useState({});
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [connectionStatus, setConnectionStatus] = useState('DISCONNECTED');
  const [selectedInsight, setSelectedInsight] = useState(null);
  const stompClientRef = useRef(null);

  // WebSocket connection setup
  useEffect(() => {
    const connectWebSocket = () => {
      try {
        setConnectionStatus('CONNECTING');
        
        const client = new Client({
          webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
          connectHeaders: {},
          debug: (str) => console.log('STOMP:', str),
          onConnect: () => {
            console.log('AI Insights WebSocket connected');
            setConnectionStatus('CONNECTED');
            
            // Subscribe to real-time risk updates
            client.subscribe('/topic/risk-updates', (message) => {
              const update = JSON.parse(message.body);
              if (!supplierId || update.supplierId === supplierId) {
                setRealTimeRisk(prevRisk => ({
                  ...prevRisk,
                  newRiskScore: update.newRiskScore,
                  confidence: update.confidence,
                  riskFactors: update.riskFactors,
                  trend: calculateTrend(prevRisk.newRiskScore, update.newRiskScore),
                  lastUpdated: new Date(update.timestamp)
                }));
              }
            });
            
            // Subscribe to recommendations
            client.subscribe('/topic/recommendations', (message) => {
              const update = JSON.parse(message.body);
              if (update.type === 'SUPPLIER_RECOMMENDATION') {
                setRecommendations(update.recommendations);
              }
            });
            
            // Subscribe to analytics alerts
            client.subscribe('/topic/alerts', (message) => {
              const alert = JSON.parse(message.body);
              if (alert.type === 'ANALYTICS_ALERT') {
                setInsights(prev => [
                  createInsightFromAlert(alert),
                  ...prev.slice(0, 9)
                ]);
              }
            });
          },
          onDisconnect: () => {
            console.log('AI Insights WebSocket disconnected');
            setConnectionStatus('DISCONNECTED');
          },
          onStompError: (frame) => {
            console.error('STOMP error:', frame);
            setConnectionStatus('ERROR');
          }
        });
        
        client.activate();
        stompClientRef.current = client;
        
      } catch (error) {
        console.error('WebSocket connection error:', error);
        setConnectionStatus('ERROR');
      }
    };

    connectWebSocket();

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, [supplierId]);

  // Initial data load
  useEffect(() => {
    const fetchInitialData = async () => {
      setLoading(true);
      try {
        await Promise.all([
          fetchAIInsights(),
          fetchRealTimeRisk(),
          fetchRecommendations()
        ]);
      } catch (error) {
        console.error('Error fetching initial data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchInitialData();
  }, [supplierId]);

  const fetchAIInsights = async () => {
    try {
      // Generate mock insights for now
      const mockInsights = generateMockInsights();
      setInsights(mockInsights);
    } catch (error) {
      console.error('Error fetching AI insights:', error);
    }
  };

  const fetchRealTimeRisk = async () => {
    try {
      if (supplierId) {
        const response = await fetch(
          `http://localhost:8080/api/analytics/real-time-risk/${supplierId}`,
          {
            headers: {
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
          }
        );
        
        if (response.ok) {
          const riskData = await response.json();
          setRealTimeRisk(riskData);
        }
      } else {
        // Mock aggregate risk data
        setRealTimeRisk({
          newRiskScore: 45 + Math.random() * 30,
          confidence: 85 + Math.random() * 10,
          trend: 'stable',
          riskFactors: {
            financial: 35,
            operational: 50,
            geographic: 40,
            compliance: 30
          }
        });
      }
    } catch (error) {
      console.error('Error fetching real-time risk:', error);
    }
  };

  const fetchRecommendations = async () => {
    try {
      if (supplierId) {
        const response = await fetch(
          'http://localhost:8080/api/analytics/recommendations/suppliers',
          {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${localStorage.getItem('token')}`
            },
            body: JSON.stringify({
              currentSupplierId: supplierId,
              criteria: {
                maxRecommendations: 5,
                businessPriority: 'RISK_MINIMIZATION'
              }
            })
          }
        );
        
        if (response.ok) {
          const recommendations = await response.json();
          setRecommendations(recommendations);
        }
      } else {
        // Mock general recommendations
        setRecommendations(generateMockRecommendations());
      }
    } catch (error) {
      console.error('Error fetching recommendations:', error);
    }
  };

  const generateMockInsights = () => {
    const insightTypes = [
      'RISK_PATTERN_DETECTED',
      'PERFORMANCE_ANOMALY',
      'COST_OPTIMIZATION',
      'SUPPLIER_DIVERSIFICATION',
      'MARKET_TREND_ANALYSIS'
    ];
    
    return insightTypes.map((type, index) => ({
      id: `insight-${index}`,
      type,
      title: getInsightTitle(type),
      description: getInsightDescription(type),
      confidence: 75 + Math.random() * 20,
      severity: getSeverity(type),
      actionable: true,
      timestamp: new Date(Date.now() - index * 3600000),
      metadata: {
        dataPoints: Math.floor(1000 + Math.random() * 5000),
        modelVersion: '2.1.0',
        accuracy: 85 + Math.random() * 10
      }
    }));
  };

  const generateMockRecommendations = () => {
    return [
      {
        id: 'rec-1',
        type: 'SUPPLIER_DIVERSIFICATION',
        title: 'Diversify Supplier Base',
        description: 'Consider adding 2-3 alternative suppliers to reduce concentration risk',
        priority: 'HIGH',
        impact: 'Reduce risk by 25%',
        effort: 'Medium',
        timeline: '30 days'
      },
      {
        id: 'rec-2',
        type: 'ROUTE_OPTIMIZATION',
        title: 'Optimize Shipping Routes',
        description: 'Switch to multimodal transport for 15% cost reduction',
        priority: 'MEDIUM',
        impact: 'Save $50k annually',
        effort: 'Low',
        timeline: '14 days'
      }
    ];
  };

  const getInsightTitle = (type) => {
    const titles = {
      'RISK_PATTERN_DETECTED': 'Risk Pattern Detected',
      'PERFORMANCE_ANOMALY': 'Performance Anomaly',
      'COST_OPTIMIZATION': 'Cost Optimization Opportunity',
      'SUPPLIER_DIVERSIFICATION': 'Supplier Diversification Needed',
      'MARKET_TREND_ANALYSIS': 'Market Trend Analysis'
    };
    return titles[type] || 'AI Insight';
  };

  const getInsightDescription = (type) => {
    const descriptions = {
      'RISK_PATTERN_DETECTED': 'AI detected unusual risk patterns in supplier performance data that may indicate potential issues.',
      'PERFORMANCE_ANOMALY': 'Statistical anomaly detected in delivery performance compared to historical baseline.',
      'COST_OPTIMIZATION': 'Machine learning identified potential cost savings through route and supplier optimization.',
      'SUPPLIER_DIVERSIFICATION': 'Analysis suggests high concentration risk with current supplier portfolio.',
      'MARKET_TREND_ANALYSIS': 'Market data analysis reveals emerging trends that may impact supply chain operations.'
    };
    return descriptions[type] || 'AI-generated insight based on data analysis.';
  };

  const getSeverity = (type) => {
    const severities = {
      'RISK_PATTERN_DETECTED': 'high',
      'PERFORMANCE_ANOMALY': 'medium',
      'COST_OPTIMIZATION': 'low',
      'SUPPLIER_DIVERSIFICATION': 'high',
      'MARKET_TREND_ANALYSIS': 'medium'
    };
    return severities[type] || 'low';
  };

  const calculateTrend = (oldValue, newValue) => {
    if (!oldValue || !newValue) return 'stable';
    const diff = newValue - oldValue;
    if (Math.abs(diff) < 2) return 'stable';
    return diff > 0 ? 'increasing' : 'decreasing';
  };

  const createInsightFromAlert = (alert) => {
    return {
      id: `alert-${Date.now()}`,
      type: alert.alertType,
      title: alert.message,
      description: 'Real-time alert from analytics engine',
      confidence: 90,
      severity: alert.severity.toLowerCase(),
      actionable: alert.actionRequired,
      timestamp: new Date(),
      metadata: alert.data
    };
  };

  const handleInsightAction = (insightId, action) => {
    setInsights(prev => 
      prev.map(insight => 
        insight.id === insightId 
          ? { ...insight, userAction: action, actionTimestamp: new Date() }
          : insight
      )
    );
  };

  const handleAcceptRecommendation = (recommendationId) => {
    setRecommendations(prev =>
      prev.map(rec =>
        rec.id === recommendationId
          ? { ...rec, status: 'ACCEPTED', acceptedAt: new Date() }
          : rec
      )
    );
  };

  const handleDeclineRecommendation = (recommendationId) => {
    setRecommendations(prev =>
      prev.map(rec =>
        rec.id === recommendationId
          ? { ...rec, status: 'DECLINED', declinedAt: new Date() }
          : rec
      )
    );
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="300px">
        <CircularProgress />
        <Typography variant="h6" sx={{ ml: 2 }}>
          Loading AI Insights...
        </Typography>
      </Box>
    );
  }

  return (
    <div className="ai-insights-panel">
      {/* Header */}
      <Card sx={{ mb: 3 }}>
        <CardHeader
          title={
            <Box display="flex" alignItems="center" gap={2}>
              <SmartToy color="primary" />
              <Typography variant="h5">AI Insights & Recommendations</Typography>
              <Chip 
                label={connectionStatus} 
                color={connectionStatus === 'CONNECTED' ? 'success' : 'error'}
                size="small"
              />
            </Box>
          }
          action={
            <Button
              startIcon={<Refresh />}
              onClick={() => window.location.reload()}
              variant="outlined"
            >
              Refresh
            </Button>
          }
        />
      </Card>

      <Grid container spacing={3}>
        {/* Real-time Risk Assessment */}
        <Grid item xs={12} lg={6}>
          <Card>
            <CardHeader 
              title="Real-time Risk Assessment" 
              avatar={<Avatar><Security /></Avatar>}
            />
            <CardContent>
              {/* Risk Gauge */}
              <Box display="flex" justifyContent="center" alignItems="center" mb={3}>
                <Box position="relative" display="inline-flex">
                  <CircularProgress
                    variant="determinate"
                    value={realTimeRisk.newRiskScore || 0}
                    size={120}
                    thickness={4}
                    sx={{
                      color: (realTimeRisk.newRiskScore || 0) > 70 ? 'error.main' :
                             (realTimeRisk.newRiskScore || 0) > 40 ? 'warning.main' : 'success.main'
                    }}
                  />
                  <Box
                    position="absolute"
                    top={0}
                    left={0}
                    bottom={0}
                    right={0}
                    display="flex"
                    flexDirection="column"
                    alignItems="center"
                    justifyContent="center"
                  >
                    <Typography variant="h6" component="div">
                      {Math.round(realTimeRisk.newRiskScore || 0)}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      Risk Score
                    </Typography>
                  </Box>
                </Box>
              </Box>

              {/* Risk Factors Breakdown */}
              <Typography variant="subtitle2" gutterBottom>
                Risk Factors:
              </Typography>
              {realTimeRisk.riskFactors && Object.entries(realTimeRisk.riskFactors).map(([factor, value]) => (
                <Box key={factor} mb={1}>
                  <Box display="flex" justifyContent="between" alignItems="center">
                    <Typography variant="body2" textTransform="capitalize">
                      {factor}
                    </Typography>
                    <Typography variant="body2">
                      {value}%
                    </Typography>
                  </Box>
                  <LinearProgress 
                    variant="determinate" 
                    value={value} 
                    sx={{ height: 8, borderRadius: 4 }}
                    color={value > 70 ? 'error' : value > 40 ? 'warning' : 'success'}
                  />
                </Box>
              ))}

              {/* Trend Indicator */}
              <Box display="flex" alignItems="center" justifyContent="center" mt={2}>
                <Chip
                  icon={
                    realTimeRisk.trend === 'increasing' ? <TrendingUp /> :
                    realTimeRisk.trend === 'decreasing' ? <TrendingDown /> :
                    <CheckCircle />
                  }
                  label={`Risk ${realTimeRisk.trend || 'stable'}`}
                  color={
                    realTimeRisk.trend === 'increasing' ? 'error' :
                    realTimeRisk.trend === 'decreasing' ? 'success' : 'default'
                  }
                  variant="outlined"
                />
              </Box>

              <Typography variant="caption" display="block" textAlign="center" mt={1}>
                Confidence: {Math.round(realTimeRisk.confidence || 0)}%
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* AI-Generated Insights */}
        <Grid item xs={12} lg={6}>
          <Card>
            <CardHeader 
              title="AI-Generated Insights" 
              avatar={<Avatar><Psychology /></Avatar>}
            />
            <CardContent>
              <Box maxHeight={400} overflow="auto">
                {insights.map((insight, index) => (
                  <motion.div
                    key={insight.id}
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.1 }}
                  >
                    <Accordion sx={{ mb: 1 }}>
                      <AccordionSummary expandIcon={<ExpandMore />}>
                        <Box display="flex" alignItems="center" gap={2} width="100%">
                          <Avatar 
                            sx={{ 
                              width: 32, 
                              height: 32,
                              bgcolor: insight.severity === 'high' ? 'error.main' :
                                       insight.severity === 'medium' ? 'warning.main' : 'info.main'
                            }}
                          >
                            <Insights fontSize="small" />
                          </Avatar>
                          <Box flex={1}>
                            <Typography variant="subtitle2">{insight.title}</Typography>
                            <Typography variant="caption" color="text.secondary">
                              Confidence: {Math.round(insight.confidence)}%
                            </Typography>
                          </Box>
                          <Chip 
                            label={insight.severity.toUpperCase()} 
                            size="small"
                            color={
                              insight.severity === 'high' ? 'error' :
                              insight.severity === 'medium' ? 'warning' : 'info'
                            }
                          />
                        </Box>
                      </AccordionSummary>
                      <AccordionDetails>
                        <Typography variant="body2" paragraph>
                          {insight.description}
                        </Typography>
                        <Box display="flex" gap={1} mt={2}>
                          <Button
                            size="small"
                            startIcon={<ThumbUp />}
                            onClick={() => handleInsightAction(insight.id, 'ACCEPTED')}
                            disabled={insight.userAction === 'ACCEPTED'}
                            color="success"
                          >
                            Useful
                          </Button>
                          <Button
                            size="small"
                            startIcon={<ThumbDown />}
                            onClick={() => handleInsightAction(insight.id, 'DISMISSED')}
                            disabled={insight.userAction === 'DISMISSED'}
                            color="error"
                          >
                            Dismiss
                          </Button>
                        </Box>
                      </AccordionDetails>
                    </Accordion>
                  </motion.div>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Smart Recommendations */}
        <Grid item xs={12}>
          <Card>
            <CardHeader 
              title="Smart Recommendations" 
              avatar={<Avatar><Insights /></Avatar>}
            />
            <CardContent>
              <Grid container spacing={2}>
                {recommendations.map((recommendation, index) => (
                  <Grid item xs={12} md={6} key={recommendation.id}>
                    <motion.div
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ delay: index * 0.1 }}
                    >
                      <Card variant="outlined" sx={{ height: '100%' }}>
                        <CardContent>
                          <Box display="flex" justifyContent="between" alignItems="flex-start" mb={2}>
                            <Typography variant="h6" gutterBottom>
                              {recommendation.title || recommendation.supplierName}
                            </Typography>
                            <Chip 
                              label={recommendation.priority || 'MEDIUM'} 
                              size="small"
                              color={
                                recommendation.priority === 'HIGH' ? 'error' :
                                recommendation.priority === 'MEDIUM' ? 'warning' : 'success'
                              }
                            />
                          </Box>
                          
                          <Typography variant="body2" color="text.secondary" paragraph>
                            {recommendation.description || 'AI-generated recommendation'}
                          </Typography>

                          {recommendation.impact && (
                            <Alert severity="info" sx={{ mb: 2 }}>
                              <Typography variant="caption">
                                <strong>Impact:</strong> {recommendation.impact}
                              </Typography>
                            </Alert>
                          )}

                          <Box display="flex" gap={1} mt={2}>
                            <Button
                              size="small"
                              variant="contained"
                              color="primary"
                              onClick={() => handleAcceptRecommendation(recommendation.id)}
                              disabled={recommendation.status === 'ACCEPTED'}
                            >
                              {recommendation.status === 'ACCEPTED' ? 'Accepted' : 'Accept'}
                            </Button>
                            <Button
                              size="small"
                              variant="outlined"
                              onClick={() => handleDeclineRecommendation(recommendation.id)}
                              disabled={recommendation.status === 'DECLINED'}
                            >
                              {recommendation.status === 'DECLINED' ? 'Declined' : 'Decline'}
                            </Button>
                          </Box>
                        </CardContent>
                      </Card>
                    </motion.div>
                  </Grid>
                ))}
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        {/* Model Performance Indicator */}
        <Grid item xs={12}>
          <Card>
            <CardHeader 
              title="AI Model Performance" 
              avatar={<Avatar><Speed /></Avatar>}
            />
            <CardContent>
              <Grid container spacing={3}>
                <Grid item xs={12} sm={4}>
                  <Box textAlign="center">
                    <Typography variant="h4" color="primary">
                      94.2%
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Model Accuracy
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Box textAlign="center">
                    <Typography variant="h4" color="success.main">
                      1.2s
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Avg Response Time
                    </Typography>
                  </Box>
                </Grid>
                <Grid item xs={12} sm={4}>
                  <Box textAlign="center">
                    <Typography variant="h4" color="info.main">
                      15.2k
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Predictions Today
                    </Typography>
                  </Box>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </div>
  );
};

export default AIInsightsPanel;