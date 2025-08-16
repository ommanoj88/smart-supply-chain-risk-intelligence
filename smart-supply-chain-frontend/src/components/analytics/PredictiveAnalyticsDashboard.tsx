import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  CircularProgress,
  Alert,
  Chip,
  IconButton,
  Tooltip
} from '@mui/material';
import {
  TrendingUp,
  TrendingDown,
  Psychology,
  Speed,
  Assessment,
  Warning,
  CheckCircle,
  Refresh
} from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, Legend, ResponsiveContainer } from 'recharts';
import { BarChart, Bar, PieChart, Pie, Cell } from 'recharts';

interface AnalyticsData {
  predictions: any;
  riskScores: Record<string, number>;
  recommendations: any[];
  confidence: number;
  generatedAt: string;
  processingTime: number;
}

interface PredictiveAnalyticsDashboardProps {
  supplierId?: number;
  timeHorizon?: number;
  onHorizonChange?: (horizon: number) => void;
}

const PredictiveAnalyticsDashboard: React.FC<PredictiveAnalyticsDashboardProps> = ({
  supplierId,
  timeHorizon = 30,
  onHorizonChange
}) => {
  const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedHorizon, setSelectedHorizon] = useState(timeHorizon);

  useEffect(() => {
    fetchAnalyticsData();
  }, [selectedHorizon, supplierId]);

  const fetchAnalyticsData = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const request = {
        analysisType: 'PREDICTIVE_RISK_ANALYSIS',
        timeHorizonDays: selectedHorizon,
        supplierIds: supplierId ? [supplierId] : undefined,
        predictionModel: 'ML_ENSEMBLE'
      };

      const response = await fetch('/api/analytics/predictive', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request),
      });

      if (!response.ok) {
        throw new Error('Failed to fetch analytics data');
      }

      const result = await response.json();
      
      // Parse JSON strings from the result
      const parsedData: AnalyticsData = {
        predictions: result.predictions ? JSON.parse(result.predictions) : {},
        riskScores: result.riskScores ? JSON.parse(result.riskScores) : {},
        recommendations: result.recommendations ? JSON.parse(result.recommendations) : [],
        confidence: result.confidenceScore || 0,
        generatedAt: result.createdAt,
        processingTime: result.processingTimeMs || 0
      };

      setAnalyticsData(parsedData);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleHorizonChange = (event: any) => {
    const newHorizon = event.target.value;
    setSelectedHorizon(newHorizon);
    if (onHorizonChange) {
      onHorizonChange(newHorizon);
    }
  };

  const getConfidenceColor = (confidence: number) => {
    if (confidence >= 80) return '#4caf50';
    if (confidence >= 60) return '#ff9800';
    return '#f44336';
  };

  const getRiskColor = (riskScore: number) => {
    if (riskScore <= 30) return '#4caf50';
    if (riskScore <= 60) return '#ff9800';
    return '#f44336';
  };

  const generateTrendData = () => {
    // Mock trend data for demonstration
    const data = [];
    for (let i = 0; i < 30; i++) {
      data.push({
        day: i + 1,
        riskScore: 40 + Math.sin(i / 5) * 10 + Math.random() * 5,
        predictedRisk: 45 + Math.sin(i / 5) * 12 + Math.random() * 4,
        confidence: 85 + Math.random() * 10
      });
    }
    return data;
  };

  const riskBreakdownData = analyticsData ? [
    { name: 'Financial', value: analyticsData.riskScores.current_financial || 0, color: '#8884d8' },
    { name: 'Operational', value: analyticsData.riskScores.current_operational || 0, color: '#82ca9d' },
    { name: 'Compliance', value: analyticsData.riskScores.current_compliance || 0, color: '#ffc658' },
    { name: 'Geographic', value: analyticsData.riskScores.current_geographic || 0, color: '#ff7300' }
  ] : [];

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress size={60} />
        <Typography variant="h6" sx={{ ml: 2 }}>
          Generating AI-Powered Analytics...
        </Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mb: 2 }}>
        {error}
        <IconButton onClick={fetchAnalyticsData} color="inherit" size="small">
          <Refresh />
        </IconButton>
      </Alert>
    );
  }

  if (!analyticsData) {
    return (
      <Alert severity="info">
        No analytics data available. Click refresh to generate predictions.
      </Alert>
    );
  }

  return (
    <Box sx={{ p: 2 }}>
      {/* Header Section */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" gutterBottom>
          <Psychology sx={{ mr: 1, verticalAlign: 'middle' }} />
          AI-Powered Predictive Analytics
        </Typography>
        
        <Box display="flex" alignItems="center" gap={2}>
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Time Horizon</InputLabel>
            <Select
              value={selectedHorizon}
              label="Time Horizon"
              onChange={handleHorizonChange}
            >
              <MenuItem value={7}>7 Days</MenuItem>
              <MenuItem value={14}>14 Days</MenuItem>
              <MenuItem value={30}>30 Days</MenuItem>
              <MenuItem value={60}>60 Days</MenuItem>
              <MenuItem value={90}>90 Days</MenuItem>
            </Select>
          </FormControl>
          
          <Tooltip title="Refresh Analytics">
            <IconButton onClick={fetchAnalyticsData} color="primary">
              <Refresh />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      {/* Key Metrics Row */}
      <Grid container spacing={3} mb={3}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Speed color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Confidence Score</Typography>
              </Box>
              <Typography variant="h3" color={getConfidenceColor(analyticsData.confidence)}>
                {analyticsData.confidence.toFixed(1)}%
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Prediction Confidence
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Assessment color="warning" sx={{ mr: 1 }} />
                <Typography variant="h6">Current Risk</Typography>
              </Box>
              <Typography variant="h3" color={getRiskColor(analyticsData.riskScores.current_overall || 0)}>
                {(analyticsData.riskScores.current_overall || 0).toFixed(0)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Overall Risk Score
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                {(analyticsData.riskScores.predicted_overall || 0) > (analyticsData.riskScores.current_overall || 0) ? 
                  <TrendingUp color="error" sx={{ mr: 1 }} /> : 
                  <TrendingDown color="success" sx={{ mr: 1 }} />
                }
                <Typography variant="h6">Risk Trend</Typography>
              </Box>
              <Typography variant="h3" color={
                (analyticsData.riskScores.predicted_overall || 0) > (analyticsData.riskScores.current_overall || 0) ? 
                '#f44336' : '#4caf50'
              }>
                {((analyticsData.riskScores.predicted_overall || 0) - (analyticsData.riskScores.current_overall || 0)).toFixed(1)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                30-Day Prediction
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Speed color="info" sx={{ mr: 1 }} />
                <Typography variant="h6">Processing Time</Typography>
              </Box>
              <Typography variant="h3" color="info.main">
                {analyticsData.processingTime}ms
              </Typography>
              <Typography variant="body2" color="text.secondary">
                ML Analysis Speed
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Charts Section */}
      <Grid container spacing={3} mb={3}>
        {/* Risk Trend Chart */}
        <Grid item xs={12} lg={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Risk Prediction Trends
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={generateTrendData()}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="day" />
                  <YAxis />
                  <RechartsTooltip />
                  <Legend />
                  <Line 
                    type="monotone" 
                    dataKey="riskScore" 
                    stroke="#8884d8" 
                    name="Current Risk"
                    strokeWidth={2}
                  />
                  <Line 
                    type="monotone" 
                    dataKey="predictedRisk" 
                    stroke="#82ca9d" 
                    name="Predicted Risk"
                    strokeWidth={2}
                    strokeDasharray="5 5"
                  />
                </LineChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        {/* Risk Breakdown Pie Chart */}
        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Risk Category Breakdown
              </Typography>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={riskBreakdownData}
                    cx="50%"
                    cy="50%"
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                    label={({ name, value }) => `${name}: ${value}%`}
                  >
                    {riskBreakdownData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <RechartsTooltip />
                </PieChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* AI Recommendations */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            AI-Generated Recommendations
          </Typography>
          {analyticsData.recommendations.length > 0 ? (
            <Grid container spacing={2}>
              {analyticsData.recommendations.map((rec: any, index: number) => (
                <Grid item xs={12} md={6} key={index}>
                  <Box 
                    p={2} 
                    border="1px solid #e0e0e0" 
                    borderRadius="8px"
                    bgcolor={rec.priority === 'HIGH' ? '#fff3e0' : rec.priority === 'MEDIUM' ? '#f3e5f5' : '#e8f5e8'}
                  >
                    <Box display="flex" alignItems="center" mb={1}>
                      {rec.priority === 'HIGH' ? 
                        <Warning color="warning" sx={{ mr: 1 }} /> : 
                        <CheckCircle color="success" sx={{ mr: 1 }} />
                      }
                      <Chip 
                        label={rec.priority} 
                        size="small" 
                        color={rec.priority === 'HIGH' ? 'error' : rec.priority === 'MEDIUM' ? 'warning' : 'success'}
                      />
                    </Box>
                    <Typography variant="subtitle2" fontWeight="bold">
                      {rec.type?.replace(/_/g, ' ')}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" mb={1}>
                      {rec.message}
                    </Typography>
                    {rec.actions && (
                      <Box>
                        <Typography variant="caption" fontWeight="bold">
                          Recommended Actions:
                        </Typography>
                        <ul style={{ margin: '4px 0', paddingLeft: '20px' }}>
                          {rec.actions.map((action: string, actionIndex: number) => (
                            <li key={actionIndex}>
                              <Typography variant="caption">{action}</Typography>
                            </li>
                          ))}
                        </ul>
                      </Box>
                    )}
                  </Box>
                </Grid>
              ))}
            </Grid>
          ) : (
            <Typography variant="body2" color="text.secondary">
              No specific recommendations at this time. All metrics are within normal ranges.
            </Typography>
          )}
        </CardContent>
      </Card>

      {/* Footer Info */}
      <Box mt={2} textAlign="center">
        <Typography variant="caption" color="text.secondary">
          Analytics generated at: {new Date(analyticsData.generatedAt).toLocaleString()} • 
          Model: ML Ensemble • Confidence: {analyticsData.confidence.toFixed(1)}%
        </Typography>
      </Box>
    </Box>
  );
};

export default PredictiveAnalyticsDashboard;