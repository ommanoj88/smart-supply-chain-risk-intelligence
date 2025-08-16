import React, { useState } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  TextField,
  Chip,
  Box,
  LinearProgress,
  Alert,
  Divider,
} from '@mui/material';
import {
  PlayArrow,
  Stop,
  Refresh,
  Business,
  LocalShipping,
  Timeline,
} from '@mui/icons-material';
import { motion } from 'framer-motion';

interface MockDataConfig {
  suppliers: {
    count: number;
    regions: string[];
    industries: string[];
    riskLevels: string[];
  };
  shipments: {
    count: number;
    timeRange: string;
    routes: string[];
    carriers: string[];
  };
  scenarios: {
    type: string;
    severity: string;
    duration: number;
  };
}

const MockDataGenerator: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [generationResults, setGenerationResults] = useState<any>(null);
  const [lastGenerated, setLastGenerated] = useState<Date | null>(null);

  const [config, setConfig] = useState<MockDataConfig>({
    suppliers: {
      count: 50,
      regions: ['North America', 'Europe'],
      industries: ['Manufacturing', 'Electronics'],
      riskLevels: ['Low', 'Medium']
    },
    shipments: {
      count: 200,
      timeRange: '6months',
      routes: ['Asia-US', 'Europe-US'],
      carriers: ['DHL', 'FedEx', 'UPS']
    },
    scenarios: {
      type: 'normal',
      severity: 'medium',
      duration: 7
    }
  });

  const regions = ['North America', 'Europe', 'Asia', 'Latin America', 'Africa', 'Oceania'];
  const industries = ['Manufacturing', 'Electronics', 'Automotive', 'Textiles', 'Chemicals', 'Food & Beverage'];
  const routes = ['Asia-US', 'Europe-US', 'Intra-Asia', 'Intra-Europe', 'US-Latin America'];

  const generateMockData = async () => {
    setLoading(true);
    setProgress(0);
    
    try {
      // Simulate progress
      const progressInterval = setInterval(() => {
        setProgress(prev => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return prev;
          }
          return prev + Math.random() * 10;
        });
      }, 200);

      // Generate suppliers
      const supplierResponse = await fetch('/api/admin/testing/generate-mock-data', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          dataType: 'suppliers',
          config: config.suppliers
        })
      });

      // Generate shipments
      const shipmentResponse = await fetch('/api/admin/testing/generate-mock-data', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          dataType: 'shipments',
          config: config.shipments
        })
      });

      // Generate scenario data
      const scenarioResponse = await fetch('/api/admin/testing/generate-mock-data', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          dataType: 'scenarios',
          config: config.scenarios
        })
      });

      clearInterval(progressInterval);
      setProgress(100);

      if (supplierResponse.ok && shipmentResponse.ok && scenarioResponse.ok) {
        const supplierData = await supplierResponse.json();
        const shipmentData = await shipmentResponse.json();
        const scenarioData = await scenarioResponse.json();

        setGenerationResults({
          suppliers: supplierData,
          shipments: shipmentData,
          scenarios: scenarioData,
          totalGenerated: config.suppliers.count + config.shipments.count
        });

        setLastGenerated(new Date());
      }

    } catch (error) {
      console.error('Failed to generate mock data:', error);
    } finally {
      setLoading(false);
      setTimeout(() => setProgress(0), 2000);
    }
  };

  const stopGeneration = () => {
    setLoading(false);
    setProgress(0);
  };

  const clearData = async () => {
    try {
      await fetch('/api/admin/testing/clear-mock-data', {
        method: 'POST'
      });
      setGenerationResults(null);
      setLastGenerated(null);
    } catch (error) {
      console.error('Failed to clear data:', error);
    }
  };

  return (
    <Grid container spacing={3}>
      {/* Supplier Data Generation */}
      <Grid item xs={12} md={6}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Card elevation={2} sx={{ height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Business color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Supplier Generation</Typography>
              </Box>

              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Number of Suppliers"
                    type="number"
                    value={config.suppliers.count}
                    onChange={(e) => setConfig(prev => ({
                      ...prev,
                      suppliers: { ...prev.suppliers, count: parseInt(e.target.value) }
                    }))}
                    InputProps={{ inputProps: { min: 1, max: 1000 } }}
                  />
                </Grid>

                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Regions</InputLabel>
                    <Select
                      multiple
                      value={config.suppliers.regions}
                      onChange={(e) => setConfig(prev => ({
                        ...prev,
                        suppliers: { ...prev.suppliers, regions: e.target.value as string[] }
                      }))}
                      renderValue={(selected) => (
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                          {selected.map((value) => (
                            <Chip key={value} label={value} size="small" />
                          ))}
                        </Box>
                      )}
                    >
                      {regions.map((region) => (
                        <MenuItem key={region} value={region}>
                          {region}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Industries</InputLabel>
                    <Select
                      multiple
                      value={config.suppliers.industries}
                      onChange={(e) => setConfig(prev => ({
                        ...prev,
                        suppliers: { ...prev.suppliers, industries: e.target.value as string[] }
                      }))}
                      renderValue={(selected) => (
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                          {selected.map((value) => (
                            <Chip key={value} label={value} size="small" />
                          ))}
                        </Box>
                      )}
                    >
                      {industries.map((industry) => (
                        <MenuItem key={industry} value={industry}>
                          {industry}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </motion.div>
      </Grid>

      {/* Shipment Data Generation */}
      <Grid item xs={12} md={6}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
        >
          <Card elevation={2} sx={{ height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <LocalShipping color="primary" sx={{ mr: 1 }} />
                <Typography variant="h6">Shipment Generation</Typography>
              </Box>

              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <TextField
                    fullWidth
                    label="Number of Shipments"
                    type="number"
                    value={config.shipments.count}
                    onChange={(e) => setConfig(prev => ({
                      ...prev,
                      shipments: { ...prev.shipments, count: parseInt(e.target.value) }
                    }))}
                    InputProps={{ inputProps: { min: 1, max: 10000 } }}
                  />
                </Grid>

                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Time Range</InputLabel>
                    <Select
                      value={config.shipments.timeRange}
                      onChange={(e) => setConfig(prev => ({
                        ...prev,
                        shipments: { ...prev.shipments, timeRange: e.target.value }
                      }))}
                    >
                      <MenuItem value="1month">Last 1 Month</MenuItem>
                      <MenuItem value="3months">Last 3 Months</MenuItem>
                      <MenuItem value="6months">Last 6 Months</MenuItem>
                      <MenuItem value="1year">Last 1 Year</MenuItem>
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12}>
                  <FormControl fullWidth>
                    <InputLabel>Routes</InputLabel>
                    <Select
                      multiple
                      value={config.shipments.routes}
                      onChange={(e) => setConfig(prev => ({
                        ...prev,
                        shipments: { ...prev.shipments, routes: e.target.value as string[] }
                      }))}
                      renderValue={(selected) => (
                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                          {selected.map((value) => (
                            <Chip key={value} label={value} size="small" />
                          ))}
                        </Box>
                      )}
                    >
                      {routes.map((route) => (
                        <MenuItem key={route} value={route}>
                          {route}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </motion.div>
      </Grid>

      {/* Generation Controls */}
      <Grid item xs={12}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <Card elevation={2}>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6">Generation Controls</Typography>
                {lastGenerated && (
                  <Typography variant="body2" color="text.secondary">
                    Last generated: {lastGenerated.toLocaleString()}
                  </Typography>
                )}
              </Box>

              {loading && (
                <Box sx={{ mb: 2 }}>
                  <Typography variant="body2" gutterBottom>
                    Generating mock data... {Math.round(progress)}%
                  </Typography>
                  <LinearProgress variant="determinate" value={progress} />
                </Box>
              )}

              <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                <Button
                  variant="contained"
                  startIcon={loading ? <Stop /> : <PlayArrow />}
                  onClick={loading ? stopGeneration : generateMockData}
                  color={loading ? "error" : "primary"}
                  disabled={false}
                >
                  {loading ? 'Stop Generation' : 'Generate Mock Data'}
                </Button>

                <Button
                  variant="outlined"
                  startIcon={<Refresh />}
                  onClick={clearData}
                  disabled={loading || !generationResults}
                >
                  Clear Data
                </Button>
              </Box>

              {generationResults && !loading && (
                <Box sx={{ mt: 3 }}>
                  <Divider sx={{ mb: 2 }} />
                  <Alert severity="success" sx={{ mb: 2 }}>
                    Successfully generated {generationResults.totalGenerated} records
                  </Alert>
                  
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={4}>
                      <Card variant="outlined">
                        <CardContent sx={{ textAlign: 'center' }}>
                          <Business color="primary" sx={{ fontSize: 40, mb: 1 }} />
                          <Typography variant="h6">{config.suppliers.count}</Typography>
                          <Typography variant="body2" color="text.secondary">Suppliers</Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                    <Grid item xs={12} sm={4}>
                      <Card variant="outlined">
                        <CardContent sx={{ textAlign: 'center' }}>
                          <LocalShipping color="primary" sx={{ fontSize: 40, mb: 1 }} />
                          <Typography variant="h6">{config.shipments.count}</Typography>
                          <Typography variant="body2" color="text.secondary">Shipments</Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                    <Grid item xs={12} sm={4}>
                      <Card variant="outlined">
                        <CardContent sx={{ textAlign: 'center' }}>
                          <Timeline color="primary" sx={{ fontSize: 40, mb: 1 }} />
                          <Typography variant="h6">{generationResults.scenarios?.generated || 0}</Typography>
                          <Typography variant="body2" color="text.secondary">Events</Typography>
                        </CardContent>
                      </Card>
                    </Grid>
                  </Grid>
                </Box>
              )}
            </CardContent>
          </Card>
        </motion.div>
      </Grid>
    </Grid>
  );
};

export default MockDataGenerator;