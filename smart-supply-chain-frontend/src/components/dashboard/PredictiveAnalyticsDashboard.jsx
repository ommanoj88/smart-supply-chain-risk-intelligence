import React, { useState, useEffect, useRef } from 'react';
import { motion } from 'framer-motion';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {
  LineChart, Line, AreaChart, Area, BarChart, Bar,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend,
  ResponsiveContainer, PieChart, Pie, Cell
} from 'recharts';
import {
  Card, CardContent, CardHeader,
  Typography, Button, Select, MenuItem,
  FormControl, InputLabel, Chip, Alert,
  CircularProgress, Grid, Box, Tabs, Tab
} from '@mui/material';
import {
  TrendingUp, TrendingDown, Warning, CheckCircle,
  Analytics, PredictiveText, Speed, Security
} from '@mui/icons-material';

const PredictiveAnalyticsDashboard = () => {
  const [analyticsData, setAnalyticsData] = useState({});
  const [predictionHorizon, setPredictionHorizon] = useState(30);
  const [loading, setLoading] = useState(true);
  const [realTimeUpdates, setRealTimeUpdates] = useState([]);
  const [selectedTab, setSelectedTab] = useState(0);
  const [connectionStatus, setConnectionStatus] = useState('DISCONNECTED');
  const stompClientRef = useRef(null);

  // WebSocket connection setup
  useEffect(() => {
    const connectWebSocket = () => {
      try {
        setConnectionStatus('CONNECTING');
        
        // Create STOMP client
        const client = new Client({
          webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
          connectHeaders: {},
          debug: (str) => console.log('STOMP:', str),
          onConnect: () => {
            console.log('Connected to WebSocket');
            setConnectionStatus('CONNECTED');
            
            // Subscribe to analytics updates
            client.subscribe('/topic/analytics', (message) => {
              const update = JSON.parse(message.body);
              if (update.type === 'ANALYTICS_SUMMARY') {
                setAnalyticsData(prev => ({ ...prev, ...update.summary }));
              }
            });
            
            // Subscribe to predictions
            client.subscribe('/topic/predictions', (message) => {
              const update = JSON.parse(message.body);
              setRealTimeUpdates(prev => [update, ...prev.slice(0, 9)]);
            });
            
            // Subscribe to risk updates
            client.subscribe('/topic/risk-updates', (message) => {
              const update = JSON.parse(message.body);
              setRealTimeUpdates(prev => [update, ...prev.slice(0, 9)]);
            });
            
            // Subscribe to recommendations
            client.subscribe('/topic/recommendations', (message) => {
              const update = JSON.parse(message.body);
              setRealTimeUpdates(prev => [update, ...prev.slice(0, 9)]);
            });
          },
          onDisconnect: () => {
            console.log('Disconnected from WebSocket');
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
  }, []);

  // Initial data load
  useEffect(() => {
    const fetchPredictiveAnalytics = async () => {
      try {
        setLoading(true);
        
        const response = await fetch('http://localhost:8080/api/analytics/predictive-analytics', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify({
            analysisType: 'PREDICTIVE',
            timeHorizonDays: predictionHorizon,
            businessContext: {
              focus: 'RISK_PREDICTION'
            }
          })
        });
        
        if (response.ok) {
          const data = await response.json();
          setAnalyticsData({
            riskPredictions: generateMockRiskPredictions(),
            supplierForecasts: generateMockSupplierForecasts(),
            marketImpacts: generateMockMarketImpacts(),
            recommendations: data.recommendations || [],
            confidence: data.confidence || 85,
            performanceTrends: generateMockPerformanceTrends(),
            economicData: generateMockEconomicData()
          });
        }
        
      } catch (error) {
        console.error('Error fetching analytics:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPredictiveAnalytics();
  }, [predictionHorizon]);

  // Mock data generators (replace with real API calls)
  const generateMockRiskPredictions = () => {
    return Array.from({ length: 30 }, (_, i) => ({
      day: `Day ${i + 1}`,
      currentRisk: 35 + Math.random() * 30,
      predictedRisk: 40 + Math.random() * 25,
      confidence: 70 + Math.random() * 25
    }));
  };

  const generateMockSupplierForecasts = () => {
    return Array.from({ length: 6 }, (_, i) => ({
      month: `Month ${i + 1}`,
      performance: 80 + Math.random() * 15,
      reliability: 85 + Math.random() * 10,
      cost: 95 + Math.random() * 8
    }));
  };

  const generateMockMarketImpacts = () => {
    return [
      { name: 'Supply Chain', value: 65, trend: 'up' },
      { name: 'Economic', value: 45, trend: 'down' },
      { name: 'Political', value: 30, trend: 'stable' },
      { name: 'Environmental', value: 55, trend: 'up' }
    ];
  };

  const generateMockPerformanceTrends = () => {
    return Array.from({ length: 12 }, (_, i) => ({
      month: `M${i + 1}`,
      accuracy: 85 + Math.random() * 10,
      speed: 90 + Math.random() * 8,
      cost: 78 + Math.random() * 12
    }));
  };

  const generateMockEconomicData = () => {
    return Array.from({ length: 8 }, (_, i) => ({
      indicator: `Indicator ${i + 1}`,
      value: 100 + Math.random() * 50,
      change: -10 + Math.random() * 20
    }));
  };

  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'];

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={60} />
        <Typography variant="h6" sx={{ ml: 2 }}>
          Loading AI-Powered Analytics...
        </Typography>
      </Box>
    );
  }

  return (
    <div className="predictive-analytics-dashboard">
      {/* Header */}
      <Card sx={{ mb: 3 }}>
        <CardHeader
          title={
            <Box display="flex" alignItems="center" gap={2}>
              <PredictiveText color="primary" />
              <Typography variant="h4">AI-Powered Predictive Analytics</Typography>
              <Chip 
                label={connectionStatus} 
                color={connectionStatus === 'CONNECTED' ? 'success' : 'error'}
                size="small"
              />
            </Box>
          }
          action={
            <FormControl sx={{ minWidth: 120 }}>
              <InputLabel>Time Horizon</InputLabel>
              <Select
                value={predictionHorizon}
                onChange={(e) => setPredictionHorizon(e.target.value)}
                label="Time Horizon"
              >
                <MenuItem value={7}>7 Days</MenuItem>
                <MenuItem value={30}>30 Days</MenuItem>
                <MenuItem value={90}>90 Days</MenuItem>
                <MenuItem value={365}>1 Year</MenuItem>
              </Select>
            </FormControl>
          }
        />
      </Card>

      {/* Tab Navigation */}
      <Card sx={{ mb: 3 }}>
        <Tabs value={selectedTab} onChange={(e, newValue) => setSelectedTab(newValue)}>
          <Tab label="Risk Predictions" icon={<Security />} />
          <Tab label="Performance Forecasts" icon={<TrendingUp />} />
          <Tab label="Market Analysis" icon={<Analytics />} />
          <Tab label="Real-time Updates" icon={<Speed />} />
        </Tabs>
      </Card>

      {/* Tab Panels */}
      {selectedTab === 0 && (
        <Grid container spacing={3}>
          {/* Risk Predictions Chart */}
          <Grid item xs={12} lg={8}>
            <Card>
              <CardHeader title="Risk Score Predictions" />
              <CardContent>
                <ResponsiveContainer width="100%" height={400}>
                  <AreaChart data={analyticsData.riskPredictions}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="day" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Area
                      type="monotone"
                      dataKey="currentRisk"
                      stackId="1"
                      stroke="#8884d8"
                      fill="#8884d8"
                      name="Current Risk"
                    />
                    <Area
                      type="monotone"
                      dataKey="predictedRisk"
                      stackId="2"
                      stroke="#82ca9d"
                      fill="#82ca9d"
                      name="Predicted Risk"
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </Grid>

          {/* Confidence Indicator */}
          <Grid item xs={12} lg={4}>
            <Card>
              <CardHeader title="Prediction Confidence" />
              <CardContent>
                <Box display="flex" justifyContent="center" alignItems="center" height={200}>
                  <Box position="relative" display="inline-flex">
                    <CircularProgress
                      variant="determinate"
                      value={analyticsData.confidence}
                      size={120}
                      thickness={4}
                    />
                    <Box
                      position="absolute"
                      top={0}
                      left={0}
                      bottom={0}
                      right={0}
                      display="flex"
                      alignItems="center"
                      justifyContent="center"
                    >
                      <Typography variant="h4" component="div" color="text.secondary">
                        {analyticsData.confidence}%
                      </Typography>
                    </Box>
                  </Box>
                </Box>
                <Alert severity="info" sx={{ mt: 2 }}>
                  High confidence predictions based on 95% data quality
                </Alert>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {selectedTab === 1 && (
        <Grid container spacing={3}>
          {/* Supplier Performance Forecasts */}
          <Grid item xs={12} lg={8}>
            <Card>
              <CardHeader title="Supplier Performance Forecasts" />
              <CardContent>
                <ResponsiveContainer width="100%" height={400}>
                  <LineChart data={analyticsData.supplierForecasts}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    <Line type="monotone" dataKey="performance" stroke="#8884d8" name="Performance %" />
                    <Line type="monotone" dataKey="reliability" stroke="#82ca9d" name="Reliability %" />
                    <Line type="monotone" dataKey="cost" stroke="#ffc658" name="Cost Efficiency %" />
                  </LineChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </Grid>

          {/* Performance Trends */}
          <Grid item xs={12} lg={4}>
            <Card>
              <CardHeader title="Trend Analysis" />
              <CardContent>
                <ResponsiveContainer width="100%" height={400}>
                  <BarChart data={analyticsData.performanceTrends}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="accuracy" fill="#8884d8" />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {selectedTab === 2 && (
        <Grid container spacing={3}>
          {/* Market Impact Analysis */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardHeader title="Market Impact Visualization" />
              <CardContent>
                <ResponsiveContainer width="100%" height={400}>
                  <PieChart>
                    <Pie
                      data={analyticsData.marketImpacts}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={150}
                      dataKey="value"
                      nameKey="name"
                    >
                      {analyticsData.marketImpacts && analyticsData.marketImpacts.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </Grid>

          {/* Economic Indicators */}
          <Grid item xs={12} md={6}>
            <Card>
              <CardHeader title="Economic Indicator Trends" />
              <CardContent>
                <ResponsiveContainer width="100%" height={400}>
                  <BarChart data={analyticsData.economicData}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="indicator" />
                    <YAxis />
                    <Tooltip />
                    <Bar dataKey="value" fill="#82ca9d" />
                  </BarChart>
                </ResponsiveContainer>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {selectedTab === 3 && (
        <Grid container spacing={3}>
          {/* Real-time Updates Feed */}
          <Grid item xs={12}>
            <Card>
              <CardHeader title="Real-time Analytics Updates" />
              <CardContent>
                <Box maxHeight={500} overflow="auto">
                  {realTimeUpdates.map((update, index) => (
                    <motion.div
                      key={index}
                      initial={{ opacity: 0, y: 20 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ duration: 0.3 }}
                    >
                      <Alert 
                        severity={update.type === 'RISK_SCORE_UPDATE' ? 'warning' : 'info'}
                        sx={{ mb: 2 }}
                        icon={
                          update.type === 'RISK_SCORE_UPDATE' ? <Warning /> :
                          update.type === 'PREDICTION_UPDATE' ? <TrendingUp /> :
                          <CheckCircle />
                        }
                      >
                        <Typography variant="subtitle2">
                          {update.type}: {update.message || 'Real-time update received'}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {new Date(update.timestamp).toLocaleString()}
                        </Typography>
                      </Alert>
                    </motion.div>
                  ))}
                  {realTimeUpdates.length === 0 && (
                    <Typography variant="body2" color="text.secondary" textAlign="center">
                      No real-time updates received yet
                    </Typography>
                  )}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* AI Recommendations Panel */}
      <Card sx={{ mt: 3 }}>
        <CardHeader title="AI-Generated Recommendations" />
        <CardContent>
          <Grid container spacing={2}>
            {analyticsData.recommendations && analyticsData.recommendations.length > 0 ? (
              analyticsData.recommendations.map((rec, index) => (
                <Grid item xs={12} md={6} lg={4} key={index}>
                  <Alert severity="info" sx={{ height: '100%' }}>
                    <Typography variant="subtitle2" gutterBottom>
                      {rec.type || 'General Recommendation'}
                    </Typography>
                    <Typography variant="body2">
                      {rec.message || 'AI-powered recommendation based on current data trends'}
                    </Typography>
                  </Alert>
                </Grid>
              ))
            ) : (
              <Grid item xs={12}>
                <Typography variant="body2" color="text.secondary" textAlign="center">
                  AI recommendations will appear here based on your data analysis
                </Typography>
              </Grid>
            )}
          </Grid>
        </CardContent>
      </Card>
    </div>
  );
};

export default PredictiveAnalyticsDashboard;