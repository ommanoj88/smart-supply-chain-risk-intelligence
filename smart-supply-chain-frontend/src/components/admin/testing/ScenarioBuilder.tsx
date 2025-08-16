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
  Slider,
  Box,
  Chip,
  Alert,
  Stepper,
  Step,
  StepLabel,
  StepContent,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  IconButton,
  useTheme,
  alpha,
} from '@mui/material';
import {
  PlayArrow,
  Stop,
  Add,
  Delete,
  Warning,
  Tsunami,
  Business,
  AttachMoney,
  Public,
} from '@mui/icons-material';
import { motion } from 'framer-motion';

interface ScenarioEvent {
  id: string;
  type: string;
  description: string;
  impact: number;
  delay: number;
  duration: number;
}

interface CrisisScenario {
  id: string;
  name: string;
  description: string;
  type: string;
  severity: number;
  duration: number;
  affectedRegions: string[];
  events: ScenarioEvent[];
}

const ScenarioBuilder: React.FC = () => {
  const theme = useTheme();
  const [activeStep, setActiveStep] = useState(0);
  const [executingScenario, setExecutingScenario] = useState<string | null>(null);
  const [scenarioResults, setScenarioResults] = useState<any>(null);

  const [currentScenario, setCurrentScenario] = useState<CrisisScenario>({
    id: '',
    name: '',
    description: '',
    type: 'hurricane',
    severity: 3,
    duration: 72,
    affectedRegions: [],
    events: []
  });

  const [prebuiltScenarios] = useState<CrisisScenario[]>([
    {
      id: 'hurricane-cat4',
      name: 'Category 4 Hurricane',
      description: 'Major hurricane affecting Gulf Coast ports and shipping routes',
      type: 'hurricane',
      severity: 4,
      duration: 96,
      affectedRegions: ['North America'],
      events: [
        {
          id: '1',
          type: 'PORT_CLOSURE',
          description: 'Major ports close due to hurricane warning',
          impact: 8,
          delay: 0,
          duration: 48
        },
        {
          id: '2',
          type: 'SUPPLY_DISRUPTION',
          description: 'Manufacturing facilities shut down',
          impact: 7,
          delay: 12,
          duration: 72
        }
      ]
    },
    {
      id: 'trade-war',
      name: 'Trade War Escalation',
      description: 'Sudden tariff increases and trade restrictions',
      type: 'trade_war',
      severity: 5,
      duration: 2160, // 3 months
      affectedRegions: ['Asia', 'North America'],
      events: [
        {
          id: '1',
          type: 'TARIFF_INCREASE',
          description: '25% tariff increase on electronics',
          impact: 9,
          delay: 0,
          duration: 2160
        },
        {
          id: '2',
          type: 'ROUTE_DISRUPTION',
          description: 'Alternative shipping routes required',
          impact: 6,
          delay: 24,
          duration: 2160
        }
      ]
    },
    {
      id: 'supplier-bankruptcy',
      name: 'Major Supplier Bankruptcy',
      description: 'Key supplier files for bankruptcy unexpectedly',
      type: 'supplier_failure',
      severity: 6,
      duration: 720, // 1 month
      affectedRegions: ['Asia'],
      events: [
        {
          id: '1',
          type: 'SUPPLIER_SHUTDOWN',
          description: 'Supplier operations cease immediately',
          impact: 10,
          delay: 0,
          duration: 720
        },
        {
          id: '2',
          type: 'CAPACITY_SHORTAGE',
          description: 'Market capacity shortage drives prices up',
          impact: 7,
          delay: 48,
          duration: 672
        }
      ]
    }
  ]);

  const scenarioTypes = [
    { value: 'hurricane', label: 'Hurricane/Typhoon', icon: <Tsunami /> },
    { value: 'pandemic', label: 'Pandemic', icon: <Warning /> },
    { value: 'trade_war', label: 'Trade War', icon: <Public /> },
    { value: 'supplier_failure', label: 'Supplier Failure', icon: <Business /> },
    { value: 'cyberattack', label: 'Cyber Attack', icon: <Warning /> },
    { value: 'economic_crisis', label: 'Economic Crisis', icon: <AttachMoney /> }
  ];

  const eventTypes = [
    'PORT_CLOSURE',
    'SUPPLY_DISRUPTION',
    'TARIFF_INCREASE',
    'ROUTE_DISRUPTION',
    'SUPPLIER_SHUTDOWN',
    'CAPACITY_SHORTAGE',
    'PRICE_INCREASE',
    'DELAY_INCREASE',
    'QUALITY_ISSUE'
  ];

  const loadPrebuiltScenario = (scenario: CrisisScenario) => {
    setCurrentScenario({ ...scenario });
    setActiveStep(1);
  };

  const addEvent = () => {
    const newEvent: ScenarioEvent = {
      id: Date.now().toString(),
      type: 'SUPPLY_DISRUPTION',
      description: '',
      impact: 5,
      delay: 0,
      duration: 24
    };
    setCurrentScenario(prev => ({
      ...prev,
      events: [...prev.events, newEvent]
    }));
  };

  const removeEvent = (eventId: string) => {
    setCurrentScenario(prev => ({
      ...prev,
      events: prev.events.filter(e => e.id !== eventId)
    }));
  };

  const updateEvent = (eventId: string, field: string, value: any) => {
    setCurrentScenario(prev => ({
      ...prev,
      events: prev.events.map(e => 
        e.id === eventId ? { ...e, [field]: value } : e
      )
    }));
  };

  const executeScenario = async () => {
    setExecutingScenario(currentScenario.name);
    
    try {
      const response = await fetch('/api/admin/testing/simulate-crisis', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          crisisType: currentScenario.type,
          scenario: currentScenario
        })
      });

      if (response.ok) {
        const result = await response.json();
        setScenarioResults(result);
        setActiveStep(3);
      }
    } catch (error) {
      console.error('Failed to execute scenario:', error);
    } finally {
      setExecutingScenario(null);
    }
  };

  const getSeverityColor = (severity: number) => {
    if (severity <= 2) return 'success';
    if (severity <= 4) return 'warning';
    return 'error';
  };

  return (
    <Grid container spacing={3}>
      {/* Prebuilt Scenarios */}
      <Grid item xs={12} md={4}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
        >
          <Card elevation={2}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Prebuilt Scenarios
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                Quick start with proven crisis scenarios
              </Typography>

              <List dense>
                {prebuiltScenarios.map((scenario) => (
                  <ListItem
                    key={scenario.id}
                    sx={{
                      border: 1,
                      borderColor: 'divider',
                      borderRadius: 1,
                      mb: 1,
                      cursor: 'pointer',
                      '&:hover': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.05)
                      }
                    }}
                    onClick={() => loadPrebuiltScenario(scenario)}
                  >
                    <ListItemIcon>
                      {scenarioTypes.find(t => t.value === scenario.type)?.icon}
                    </ListItemIcon>
                    <ListItemText
                      primary={scenario.name}
                      secondary={
                        <Box sx={{ mt: 1 }}>
                          <Typography variant="caption" display="block">
                            {scenario.description}
                          </Typography>
                          <Box sx={{ mt: 1, display: 'flex', gap: 1 }}>
                            <Chip
                              label={`Severity ${scenario.severity}`}
                              size="small"
                              color={getSeverityColor(scenario.severity)}
                            />
                            <Chip
                              label={`${scenario.duration}h`}
                              size="small"
                              variant="outlined"
                            />
                          </Box>
                        </Box>
                      }
                    />
                  </ListItem>
                ))}
              </List>
            </CardContent>
          </Card>
        </motion.div>
      </Grid>

      {/* Scenario Builder */}
      <Grid item xs={12} md={8}>
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
        >
          <Card elevation={2}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Crisis Scenario Builder
              </Typography>

              <Stepper activeStep={activeStep} orientation="vertical">
                {/* Step 1: Scenario Type */}
                <Step>
                  <StepLabel>Configure Scenario</StepLabel>
                  <StepContent>
                    <Grid container spacing={2}>
                      <Grid item xs={12}>
                        <TextField
                          fullWidth
                          label="Scenario Name"
                          value={currentScenario.name}
                          onChange={(e) => setCurrentScenario(prev => ({ ...prev, name: e.target.value }))}
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <TextField
                          fullWidth
                          multiline
                          rows={2}
                          label="Description"
                          value={currentScenario.description}
                          onChange={(e) => setCurrentScenario(prev => ({ ...prev, description: e.target.value }))}
                        />
                      </Grid>
                      <Grid item xs={12} sm={6}>
                        <FormControl fullWidth>
                          <InputLabel>Scenario Type</InputLabel>
                          <Select
                            value={currentScenario.type}
                            onChange={(e) => setCurrentScenario(prev => ({ ...prev, type: e.target.value }))}
                          >
                            {scenarioTypes.map((type) => (
                              <MenuItem key={type.value} value={type.value}>
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                  {type.icon}
                                  {type.label}
                                </Box>
                              </MenuItem>
                            ))}
                          </Select>
                        </FormControl>
                      </Grid>
                      <Grid item xs={12} sm={6}>
                        <TextField
                          fullWidth
                          label="Duration (hours)"
                          type="number"
                          value={currentScenario.duration}
                          onChange={(e) => setCurrentScenario(prev => ({ ...prev, duration: parseInt(e.target.value) }))}
                        />
                      </Grid>
                      <Grid item xs={12}>
                        <Typography variant="body2" gutterBottom>
                          Severity Level: {currentScenario.severity}
                        </Typography>
                        <Slider
                          value={currentScenario.severity}
                          onChange={(_, value) => setCurrentScenario(prev => ({ ...prev, severity: value as number }))}
                          min={1}
                          max={10}
                          step={1}
                          marks={[
                            { value: 1, label: 'Low' },
                            { value: 5, label: 'Medium' },
                            { value: 10, label: 'Critical' }
                          ]}
                        />
                      </Grid>
                    </Grid>
                    <Box sx={{ mt: 2 }}>
                      <Button
                        variant="contained"
                        onClick={() => setActiveStep(1)}
                        disabled={!currentScenario.name}
                      >
                        Next
                      </Button>
                    </Box>
                  </StepContent>
                </Step>

                {/* Step 2: Events */}
                <Step>
                  <StepLabel>Configure Events</StepLabel>
                  <StepContent>
                    <Box sx={{ mb: 2 }}>
                      <Button
                        variant="outlined"
                        startIcon={<Add />}
                        onClick={addEvent}
                        sx={{ mb: 2 }}
                      >
                        Add Event
                      </Button>
                    </Box>

                    {currentScenario.events.map((event, index) => (
                      <Paper key={event.id} variant="outlined" sx={{ p: 2, mb: 2 }}>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                          <Typography variant="subtitle2">
                            Event {index + 1}
                          </Typography>
                          <IconButton
                            size="small"
                            onClick={() => removeEvent(event.id)}
                            color="error"
                          >
                            <Delete />
                          </IconButton>
                        </Box>

                        <Grid container spacing={2}>
                          <Grid item xs={12} sm={6}>
                            <FormControl fullWidth size="small">
                              <InputLabel>Event Type</InputLabel>
                              <Select
                                value={event.type}
                                onChange={(e) => updateEvent(event.id, 'type', e.target.value)}
                              >
                                {eventTypes.map((type) => (
                                  <MenuItem key={type} value={type}>
                                    {type.replace('_', ' ')}
                                  </MenuItem>
                                ))}
                              </Select>
                            </FormControl>
                          </Grid>
                          <Grid item xs={12} sm={6}>
                            <TextField
                              fullWidth
                              size="small"
                              label="Impact (1-10)"
                              type="number"
                              value={event.impact}
                              onChange={(e) => updateEvent(event.id, 'impact', parseInt(e.target.value))}
                              inputProps={{ min: 1, max: 10 }}
                            />
                          </Grid>
                          <Grid item xs={12}>
                            <TextField
                              fullWidth
                              size="small"
                              label="Description"
                              value={event.description}
                              onChange={(e) => updateEvent(event.id, 'description', e.target.value)}
                            />
                          </Grid>
                          <Grid item xs={6}>
                            <TextField
                              fullWidth
                              size="small"
                              label="Delay (hours)"
                              type="number"
                              value={event.delay}
                              onChange={(e) => updateEvent(event.id, 'delay', parseInt(e.target.value))}
                            />
                          </Grid>
                          <Grid item xs={6}>
                            <TextField
                              fullWidth
                              size="small"
                              label="Duration (hours)"
                              type="number"
                              value={event.duration}
                              onChange={(e) => updateEvent(event.id, 'duration', parseInt(e.target.value))}
                            />
                          </Grid>
                        </Grid>
                      </Paper>
                    ))}

                    <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
                      <Button onClick={() => setActiveStep(0)}>
                        Back
                      </Button>
                      <Button
                        variant="contained"
                        onClick={() => setActiveStep(2)}
                        disabled={currentScenario.events.length === 0}
                      >
                        Next
                      </Button>
                    </Box>
                  </StepContent>
                </Step>

                {/* Step 3: Execute */}
                <Step>
                  <StepLabel>Review & Execute</StepLabel>
                  <StepContent>
                    <Alert severity="info" sx={{ mb: 2 }}>
                      Review your scenario configuration before execution
                    </Alert>

                    <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
                      <Typography variant="subtitle1" gutterBottom>
                        {currentScenario.name}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        {currentScenario.description}
                      </Typography>
                      <Box sx={{ display: 'flex', gap: 1, mt: 1 }}>
                        <Chip
                          label={`Severity ${currentScenario.severity}`}
                          color={getSeverityColor(currentScenario.severity)}
                          size="small"
                        />
                        <Chip
                          label={`${currentScenario.duration} hours`}
                          variant="outlined"
                          size="small"
                        />
                        <Chip
                          label={`${currentScenario.events.length} events`}
                          variant="outlined"
                          size="small"
                        />
                      </Box>
                    </Paper>

                    <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
                      <Button onClick={() => setActiveStep(1)}>
                        Back
                      </Button>
                      <Button
                        variant="contained"
                        color="warning"
                        startIcon={executingScenario ? <Stop /> : <PlayArrow />}
                        onClick={executeScenario}
                        disabled={!!executingScenario}
                      >
                        {executingScenario ? 'Executing...' : 'Execute Scenario'}
                      </Button>
                    </Box>
                  </StepContent>
                </Step>

                {/* Step 4: Results */}
                <Step>
                  <StepLabel>Monitor Results</StepLabel>
                  <StepContent>
                    {scenarioResults && (
                      <Box>
                        <Alert severity="success" sx={{ mb: 2 }}>
                          Scenario executed successfully! Monitor the impact in real-time.
                        </Alert>
                        
                        <Paper variant="outlined" sx={{ p: 2 }}>
                          <Typography variant="subtitle2" gutterBottom>
                            Execution Summary
                          </Typography>
                          <Typography variant="body2">
                            Scenario ID: {scenarioResults.simulation?.scenarioId}
                          </Typography>
                          <Typography variant="body2">
                            Started: {new Date().toLocaleString()}
                          </Typography>
                        </Paper>
                      </Box>
                    )}
                    
                    <Box sx={{ mt: 2 }}>
                      <Button
                        variant="outlined"
                        onClick={() => {
                          setActiveStep(0);
                          setScenarioResults(null);
                          setCurrentScenario({
                            id: '',
                            name: '',
                            description: '',
                            type: 'hurricane',
                            severity: 3,
                            duration: 72,
                            affectedRegions: [],
                            events: []
                          });
                        }}
                      >
                        Create New Scenario
                      </Button>
                    </Box>
                  </StepContent>
                </Step>
              </Stepper>
            </CardContent>
          </Card>
        </motion.div>
      </Grid>
    </Grid>
  );
};

export default ScenarioBuilder;