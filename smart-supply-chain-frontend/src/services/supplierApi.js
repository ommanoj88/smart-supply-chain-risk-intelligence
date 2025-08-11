import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Supplier API endpoints
export const supplierApi = {
  // Get all suppliers with pagination and sorting
  getAllSuppliers: (params = {}) => {
    const { page = 0, size = 20, sortBy = 'name', sortDirection = 'asc' } = params;
    return api.get('/api/suppliers', {
      params: { page, size, sortBy, sortDirection }
    });
  },

  // Get supplier by ID
  getSupplierById: (id) => {
    return api.get(`/api/suppliers/${id}`);
  },

  // Get supplier by code
  getSupplierByCode: (supplierCode) => {
    return api.get(`/api/suppliers/code/${supplierCode}`);
  },

  // Create new supplier
  createSupplier: (supplierData) => {
    return api.post('/api/suppliers', supplierData);
  },

  // Update supplier
  updateSupplier: (id, supplierData) => {
    return api.put(`/api/suppliers/${id}`, supplierData);
  },

  // Delete supplier
  deleteSupplier: (id) => {
    return api.delete(`/api/suppliers/${id}`);
  },

  // Search suppliers with advanced filtering
  searchSuppliers: (params = {}) => {
    const {
      searchTerm,
      status,
      tier,
      country,
      industry,
      minRiskScore,
      maxRiskScore,
      page = 0,
      size = 20,
      sortBy = 'name',
      sortDirection = 'asc'
    } = params;

    return api.get('/api/suppliers/search', {
      params: {
        searchTerm,
        status,
        tier,
        country,
        industry,
        minRiskScore,
        maxRiskScore,
        page,
        size,
        sortBy,
        sortDirection
      }
    });
  },

  // Get suppliers by status
  getSuppliersByStatus: (status, params = {}) => {
    const { page = 0, size = 20 } = params;
    return api.get(`/api/suppliers/status/${status}`, {
      params: { page, size }
    });
  },

  // Get suppliers by tier
  getSuppliersByTier: (tier, params = {}) => {
    const { page = 0, size = 20 } = params;
    return api.get(`/api/suppliers/tier/${tier}`, {
      params: { page, size }
    });
  },

  // Get preferred suppliers
  getPreferredSuppliers: (params = {}) => {
    const { page = 0, size = 20 } = params;
    return api.get('/api/suppliers/preferred', {
      params: { page, size }
    });
  },

  // Get strategic suppliers
  getStrategicSuppliers: (params = {}) => {
    const { page = 0, size = 20 } = params;
    return api.get('/api/suppliers/strategic', {
      params: { page, size }
    });
  },

  // Get suppliers requiring audit
  getSuppliersRequiringAudit: () => {
    return api.get('/api/suppliers/audit-required');
  },

  // Get supplier statistics
  getSupplierStatistics: () => {
    return api.get('/api/suppliers/statistics');
  },

  // Get distinct countries
  getDistinctCountries: () => {
    return api.get('/api/suppliers/countries');
  },

  // Get distinct industries
  getDistinctIndustries: () => {
    return api.get('/api/suppliers/industries');
  },

  // Recalculate risk scores
  recalculateRiskScores: (id) => {
    return api.post(`/api/suppliers/${id}/recalculate-risk`);
  },

  // Get risk assessment
  getRiskAssessment: (id) => {
    return api.get(`/api/suppliers/${id}/risk-assessment`);
  },

  // Get recommendations
  getRecommendations: (id) => {
    return api.get(`/api/suppliers/${id}/recommendations`);
  },
};

// Utility functions for data transformation
export const supplierUtils = {
  // Format risk score with color
  formatRiskScore: (score) => {
    if (score <= 20) return { level: 'Very Low', color: 'text-green-600', bgColor: 'bg-green-100' };
    if (score <= 40) return { level: 'Low', color: 'text-green-600', bgColor: 'bg-green-100' };
    if (score <= 60) return { level: 'Medium', color: 'text-yellow-600', bgColor: 'bg-yellow-100' };
    if (score <= 80) return { level: 'High', color: 'text-orange-600', bgColor: 'bg-orange-100' };
    return { level: 'Very High', color: 'text-red-600', bgColor: 'bg-red-100' };
  },

  // Format supplier tier
  formatTier: (tier) => {
    const tierMap = {
      PRIMARY: { label: 'Primary', color: 'text-blue-600', bgColor: 'bg-blue-100' },
      SECONDARY: { label: 'Secondary', color: 'text-gray-600', bgColor: 'bg-gray-100' },
      BACKUP: { label: 'Backup', color: 'text-purple-600', bgColor: 'bg-purple-100' },
    };
    return tierMap[tier] || { label: tier, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  },

  // Format supplier status
  formatStatus: (status) => {
    const statusMap = {
      ACTIVE: { label: 'Active', color: 'text-green-600', bgColor: 'bg-green-100' },
      INACTIVE: { label: 'Inactive', color: 'text-gray-600', bgColor: 'bg-gray-100' },
      PENDING: { label: 'Pending', color: 'text-yellow-600', bgColor: 'bg-yellow-100' },
      BLOCKED: { label: 'Blocked', color: 'text-red-600', bgColor: 'bg-red-100' },
    };
    return statusMap[status] || { label: status, color: 'text-gray-600', bgColor: 'bg-gray-100' };
  },

  // Format currency amount
  formatCurrency: (amount, currency = 'USD') => {
    if (!amount) return 'N/A';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency,
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(amount);
  },

  // Format percentage
  formatPercentage: (value) => {
    if (value === null || value === undefined) return 'N/A';
    return `${value.toFixed(1)}%`;
  },

  // Calculate days since/until date
  daysSince: (date) => {
    if (!date) return null;
    const today = new Date();
    const targetDate = new Date(date);
    const diffTime = today - targetDate;
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  },

  daysUntil: (date) => {
    if (!date) return null;
    const today = new Date();
    const targetDate = new Date(date);
    const diffTime = targetDate - today;
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  },
};

// Error handling utility
export const handleApiError = (error) => {
  if (error.response) {
    // The request was made and the server responded with a status code
    // that falls out of the range of 2xx
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        return data.error || 'Bad request';
      case 401:
        return 'Unauthorized - Please log in';
      case 403:
        return 'Forbidden - You do not have permission';
      case 404:
        return 'Not found';
      case 409:
        return data.error || 'Conflict - Resource already exists';
      case 500:
        return 'Internal server error - Please try again later';
      default:
        return data.error || `Server error (${status})`;
    }
  } else if (error.request) {
    // The request was made but no response was received
    return 'Network error - Please check your connection';
  } else {
    // Something happened in setting up the request that triggered an Error
    return error.message || 'An unexpected error occurred';
  }
};

export default api;