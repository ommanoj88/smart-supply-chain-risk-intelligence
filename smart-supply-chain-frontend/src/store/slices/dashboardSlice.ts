import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface DashboardMetrics {
  totalSuppliers: number;
  activeShipments: number;
  riskAlerts: number;
  onTimeDeliveryRate: number;
  avgRiskScore: number;
  totalValue: number;
  monthlyGrowth: number;
  criticalSuppliers: number;
}

interface ChartData {
  labels: string[];
  datasets: Array<{
    label: string;
    data: number[];
    backgroundColor?: string | string[];
    borderColor?: string;
    borderWidth?: number;
  }>;
}

interface RiskAlert {
  id: string;
  type: 'supplier' | 'shipment' | 'compliance' | 'financial';
  severity: 'low' | 'medium' | 'high' | 'critical';
  title: string;
  description: string;
  entityId: string;
  entityName: string;
  createdAt: string;
  status: 'active' | 'acknowledged' | 'resolved';
}

interface DashboardState {
  metrics: DashboardMetrics;
  riskAlerts: RiskAlert[];
  chartData: {
    riskTrends: ChartData;
    deliveryPerformance: ChartData;
    supplierDistribution: ChartData;
    shipmentVolume: ChartData;
    costAnalysis: ChartData;
  };
  timeRange: '24h' | '7d' | '30d' | '90d' | '1y';
  selectedRegion: string | null;
  refreshInterval: number;
  lastRefresh: string | null;
  loading: boolean;
  error: string | null;
}

const initialState: DashboardState = {
  metrics: {
    totalSuppliers: 0,
    activeShipments: 0,
    riskAlerts: 0,
    onTimeDeliveryRate: 0,
    avgRiskScore: 0,
    totalValue: 0,
    monthlyGrowth: 0,
    criticalSuppliers: 0,
  },
  riskAlerts: [],
  chartData: {
    riskTrends: { labels: [], datasets: [] },
    deliveryPerformance: { labels: [], datasets: [] },
    supplierDistribution: { labels: [], datasets: [] },
    shipmentVolume: { labels: [], datasets: [] },
    costAnalysis: { labels: [], datasets: [] },
  },
  timeRange: '30d',
  selectedRegion: null,
  refreshInterval: 300000, // 5 minutes
  lastRefresh: null,
  loading: false,
  error: null,
};

/**
 * Enhanced dashboard slice for analytics and real-time metrics
 */
export const dashboardSlice = createSlice({
  name: 'dashboard',
  initialState,
  reducers: {
    setMetrics: (state, action: PayloadAction<DashboardMetrics>) => {
      state.metrics = action.payload;
      state.lastRefresh = new Date().toISOString();
    },
    updateMetric: (state, action: PayloadAction<{ key: keyof DashboardMetrics; value: number }>) => {
      state.metrics[action.payload.key] = action.payload.value;
    },
    setRiskAlerts: (state, action: PayloadAction<RiskAlert[]>) => {
      state.riskAlerts = action.payload;
    },
    addRiskAlert: (state, action: PayloadAction<RiskAlert>) => {
      state.riskAlerts.unshift(action.payload);
    },
    updateRiskAlert: (state, action: PayloadAction<{ id: string; updates: Partial<RiskAlert> }>) => {
      const index = state.riskAlerts.findIndex(alert => alert.id === action.payload.id);
      if (index !== -1) {
        state.riskAlerts[index] = { ...state.riskAlerts[index], ...action.payload.updates };
      }
    },
    removeRiskAlert: (state, action: PayloadAction<string>) => {
      state.riskAlerts = state.riskAlerts.filter(alert => alert.id !== action.payload);
    },
    setChartData: (state, action: PayloadAction<{ 
      chart: keyof DashboardState['chartData']; 
      data: ChartData 
    }>) => {
      state.chartData[action.payload.chart] = action.payload.data;
    },
    setTimeRange: (state, action: PayloadAction<DashboardState['timeRange']>) => {
      state.timeRange = action.payload;
    },
    setSelectedRegion: (state, action: PayloadAction<string | null>) => {
      state.selectedRegion = action.payload;
    },
    setRefreshInterval: (state, action: PayloadAction<number>) => {
      state.refreshInterval = action.payload;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
    refreshDashboard: (state) => {
      state.lastRefresh = new Date().toISOString();
      state.loading = true;
      state.error = null;
    },
  },
});

export const {
  setMetrics,
  updateMetric,
  setRiskAlerts,
  addRiskAlert,
  updateRiskAlert,
  removeRiskAlert,
  setChartData,
  setTimeRange,
  setSelectedRegion,
  setRefreshInterval,
  setLoading,
  setError,
  refreshDashboard,
} = dashboardSlice.actions;

// Selectors
export const selectDashboardMetrics = (state: { dashboard: DashboardState }) => state.dashboard.metrics;
export const selectRiskAlerts = (state: { dashboard: DashboardState }) => state.dashboard.riskAlerts;
export const selectChartData = (state: { dashboard: DashboardState }) => state.dashboard.chartData;
export const selectTimeRange = (state: { dashboard: DashboardState }) => state.dashboard.timeRange;
export const selectSelectedRegion = (state: { dashboard: DashboardState }) => state.dashboard.selectedRegion;
export const selectDashboardLoading = (state: { dashboard: DashboardState }) => state.dashboard.loading;
export const selectDashboardError = (state: { dashboard: DashboardState }) => state.dashboard.error;
export const selectLastRefresh = (state: { dashboard: DashboardState }) => state.dashboard.lastRefresh;