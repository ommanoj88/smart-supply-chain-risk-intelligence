import api from './api';

class AnalyticsService {
  constructor() {
    this.baseUrl = '/api/analytics';
  }

  /**
   * Generate predictive analytics with ML predictions
   */
  async generatePredictiveAnalytics(request) {
    try {
      const response = await api.post(`${this.baseUrl}/predictive-analytics`, request);
      return response.data;
    } catch (error) {
      console.error('Error generating predictive analytics:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get real-time risk assessment for a supplier
   */
  async getRealTimeRisk(supplierId, context = {}) {
    try {
      const params = new URLSearchParams(context).toString();
      const url = `${this.baseUrl}/real-time-risk/${supplierId}${params ? `?${params}` : ''}`;
      const response = await api.get(url);
      return response.data;
    } catch (error) {
      console.error('Error getting real-time risk:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get supplier recommendations
   */
  async getSupplierRecommendations(request) {
    try {
      const response = await api.post(`${this.baseUrl}/recommendations/suppliers`, request);
      return response.data;
    } catch (error) {
      console.error('Error getting supplier recommendations:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get route recommendations
   */
  async getRouteRecommendations(request) {
    try {
      const response = await api.post(`${this.baseUrl}/recommendations/routes`, request);
      return response.data;
    } catch (error) {
      console.error('Error getting route recommendations:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Optimize inventory levels
   */
  async optimizeInventory(request) {
    try {
      const response = await api.post(`${this.baseUrl}/optimize-inventory`, request);
      return response.data;
    } catch (error) {
      console.error('Error optimizing inventory:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get analytics performance metrics
   */
  async getPerformanceMetrics() {
    try {
      const response = await api.get(`${this.baseUrl}/performance-metrics`);
      return response.data;
    } catch (error) {
      console.error('Error getting performance metrics:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get analytics summary for dashboard
   */
  async getAnalyticsSummary() {
    try {
      const response = await api.get(`${this.baseUrl}/summary`);
      return response.data;
    } catch (error) {
      console.error('Error getting analytics summary:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get risk predictions for multiple suppliers
   */
  async getRiskPredictions(supplierIds, timeHorizonDays = 30) {
    try {
      const response = await api.post(
        `${this.baseUrl}/risk-predictions?timeHorizonDays=${timeHorizonDays}`,
        supplierIds
      );
      return response.data;
    } catch (error) {
      console.error('Error getting risk predictions:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Check analytics service health
   */
  async getHealthStatus() {
    try {
      const response = await api.get(`${this.baseUrl}/health`);
      return response.data;
    } catch (error) {
      console.error('Error checking analytics health:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get advanced analytics for executive dashboard
   */
  async getExecutiveAnalytics(timeRange = '30d') {
    try {
      const request = {
        analysisType: 'EXECUTIVE',
        timeHorizonDays: this.parseTimeRange(timeRange),
        businessContext: {
          focus: 'EXECUTIVE_SUMMARY',
          includeForecasts: true,
          includeTrends: true,
          includeRecommendations: true
        }
      };
      
      return await this.generatePredictiveAnalytics(request);
    } catch (error) {
      console.error('Error getting executive analytics:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get supplier performance analytics
   */
  async getSupplierAnalytics(supplierIds, timeRange = '90d') {
    try {
      const request = {
        analysisType: 'SUPPLIER_PERFORMANCE',
        timeHorizonDays: this.parseTimeRange(timeRange),
        supplierIds: supplierIds,
        businessContext: {
          focus: 'SUPPLIER_OPTIMIZATION'
        }
      };
      
      return await this.generatePredictiveAnalytics(request);
    } catch (error) {
      console.error('Error getting supplier analytics:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get route optimization analytics
   */
  async getRouteAnalytics(origins, destinations, timeRange = '30d') {
    try {
      const request = {
        analysisType: 'ROUTE_OPTIMIZATION',
        timeHorizonDays: this.parseTimeRange(timeRange),
        businessContext: {
          focus: 'LOGISTICS_OPTIMIZATION',
          origins: origins,
          destinations: destinations
        }
      };
      
      return await this.generatePredictiveAnalytics(request);
    } catch (error) {
      console.error('Error getting route analytics:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get market impact analytics
   */
  async getMarketAnalytics(regions, sectors, timeRange = '90d') {
    try {
      const request = {
        analysisType: 'MARKET_ANALYSIS',
        timeHorizonDays: this.parseTimeRange(timeRange),
        businessContext: {
          focus: 'MARKET_INTELLIGENCE',
          regions: regions,
          sectors: sectors
        }
      };
      
      return await this.generatePredictiveAnalytics(request);
    } catch (error) {
      console.error('Error getting market analytics:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Save user feedback on analytics insights
   */
  async submitInsightFeedback(insightId, feedback) {
    try {
      // This would be implemented with a backend endpoint
      console.log('Submitting feedback for insight:', insightId, feedback);
      return { success: true };
    } catch (error) {
      console.error('Error submitting insight feedback:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get analytics export data
   */
  async exportAnalytics(format = 'json', filters = {}) {
    try {
      const params = new URLSearchParams({ format, ...filters }).toString();
      const response = await api.get(`${this.baseUrl}/export?${params}`, {
        responseType: format === 'csv' ? 'blob' : 'json'
      });
      return response.data;
    } catch (error) {
      console.error('Error exporting analytics:', error);
      throw this.handleError(error);
    }
  }

  // Utility methods

  /**
   * Parse time range string to days
   */
  parseTimeRange(timeRange) {
    const unit = timeRange.slice(-1);
    const value = parseInt(timeRange.slice(0, -1));
    
    switch (unit) {
      case 'd': return value;
      case 'w': return value * 7;
      case 'm': return value * 30;
      case 'y': return value * 365;
      default: return 30;
    }
  }

  /**
   * Handle API errors consistently
   */
  handleError(error) {
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      return {
        message: data?.message || `Server error (${status})`,
        status: status,
        code: data?.code || 'SERVER_ERROR'
      };
    } else if (error.request) {
      // Network error
      return {
        message: 'Network error - please check your connection',
        status: 0,
        code: 'NETWORK_ERROR'
      };
    } else {
      // Other error
      return {
        message: error.message || 'An unexpected error occurred',
        status: 0,
        code: 'UNKNOWN_ERROR'
      };
    }
  }

  /**
   * Create analytics request object
   */
  createAnalyticsRequest(options = {}) {
    const {
      analysisType = 'PREDICTIVE',
      timeHorizonDays = 30,
      supplierIds = [],
      businessContext = {},
      includeRecommendations = true,
      confidenceThreshold = 0.7
    } = options;

    return {
      analysisType,
      timeHorizonDays,
      supplierIds,
      businessContext: {
        includeRecommendations,
        confidenceThreshold,
        ...businessContext
      }
    };
  }

  /**
   * Create supplier recommendation request
   */
  createSupplierRecommendationRequest(currentSupplierId, options = {}) {
    const {
      maxRecommendations = 5,
      businessPriority = 'RISK_MINIMIZATION',
      requiredCertifications = [],
      preferredCountries = [],
      excludedCountries = [],
      maxRiskThreshold = null,
      minQualityThreshold = null
    } = options;

    return {
      currentSupplierId,
      criteria: {
        maxRecommendations,
        businessPriority,
        requiredCertifications,
        preferredCountries,
        excludedCountries,
        maxRiskThreshold,
        minQualityThreshold
      }
    };
  }

  /**
   * Create route optimization request
   */
  createRouteOptimizationRequest(origin, destination, options = {}) {
    const {
      maxRecommendations = 5,
      urgencyLevel = 'MEDIUM',
      maxBudget = null,
      requiredDeliveryDate = null,
      preferredCarriers = [],
      excludedCarriers = [],
      cargoDetails = {}
    } = options;

    return {
      origin,
      destination,
      maxRecommendations,
      urgencyLevel,
      maxBudget,
      requiredDeliveryDate,
      preferredCarriers,
      excludedCarriers,
      cargoDetails,
      criteria: {
        businessPriority: 'COST_OPTIMIZATION'
      }
    };
  }

  /**
   * Create inventory optimization request
   */
  createInventoryOptimizationRequest(productId, options = {}) {
    const {
      supplierIds = [],
      optimizationType = 'BALANCED',
      maxBudget = null,
      maxLeadTimeDays = null,
      currentInventoryLevel = null,
      minStockLevel = null,
      maxStockLevel = null,
      timeRange = null
    } = options;

    return {
      productId,
      supplierIds,
      optimizationType,
      maxBudget,
      maxLeadTimeDays,
      currentInventoryLevel,
      minStockLevel,
      maxStockLevel,
      timeRange
    };
  }
}

// Create singleton instance
const analyticsService = new AnalyticsService();
export default analyticsService;