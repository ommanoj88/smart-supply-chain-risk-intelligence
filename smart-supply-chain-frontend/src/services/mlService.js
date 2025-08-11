import api from './api';

/**
 * ML Service API integration
 * Handles communication with ML prediction endpoints
 */
class MLService {
  
  /**
   * Predict shipment delay
   */
  async predictShipmentDelay(shipmentData) {
    try {
      const response = await api.post('/ml/predict/delay', shipmentData);
      return response.data;
    } catch (error) {
      console.error('Error predicting shipment delay:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Calculate risk score
   */
  async calculateRiskScore(riskData) {
    try {
      const response = await api.post('/ml/analyze/risk-score', riskData);
      return response.data;
    } catch (error) {
      console.error('Error calculating risk score:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Detect supplier anomalies
   */
  async detectSupplierAnomalies(supplierData) {
    try {
      const response = await api.post('/ml/analyze/anomalies', supplierData);
      return response.data;
    } catch (error) {
      console.error('Error detecting supplier anomalies:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Train delay prediction model
   */
  async trainDelayModel(trainingData) {
    try {
      const response = await api.post('/ml/train/delay-model', trainingData);
      return response.data;
    } catch (error) {
      console.error('Error training delay model:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get ML service health
   */
  async getMLServiceHealth() {
    try {
      const response = await api.get('/ml/health');
      return response.data;
    } catch (error) {
      console.error('Error checking ML service health:', error);
      return { ml_service_healthy: false, status: 'DOWN', error: error.message };
    }
  }

  /**
   * Get ML features and capabilities
   */
  async getMLFeatures() {
    try {
      const response = await api.get('/ml/features');
      return response.data;
    } catch (error) {
      console.error('Error getting ML features:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Batch predict delays for multiple shipments
   */
  async batchPredictDelays(shipmentsData) {
    try {
      const predictions = await Promise.all(
        shipmentsData.map(shipment => this.predictShipmentDelay(shipment))
      );
      return predictions;
    } catch (error) {
      console.error('Error in batch delay prediction:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get predictive analytics for dashboard
   */
  async getPredictiveAnalytics(filters = {}) {
    try {
      // This could be a dedicated endpoint for dashboard analytics
      const response = await api.get('/ml/analytics/dashboard', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Error getting predictive analytics:', error);
      // Return fallback data for dashboard
      return this.getFallbackAnalytics();
    }
  }

  /**
   * Get risk trends analysis
   */
  async getRiskTrends(timeRange = '30d') {
    try {
      const response = await api.get(`/ml/analytics/risk-trends`, {
        params: { timeRange }
      });
      return response.data;
    } catch (error) {
      console.error('Error getting risk trends:', error);
      return this.getFallbackRiskTrends();
    }
  }

  /**
   * Get supplier performance predictions
   */
  async getSupplierPerformancePredictions(supplierId) {
    try {
      const response = await api.get(`/ml/predict/supplier-performance/${supplierId}`);
      return response.data;
    } catch (error) {
      console.error('Error getting supplier performance predictions:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get route optimization suggestions
   */
  async getRouteOptimization(routeData) {
    try {
      const response = await api.post('/ml/optimize/route', routeData);
      return response.data;
    } catch (error) {
      console.error('Error getting route optimization:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Get demand forecasting
   */
  async getDemandForecast(productData, timeHorizon = 90) {
    try {
      const response = await api.post('/ml/forecast/demand', {
        ...productData,
        timeHorizon
      });
      return response.data;
    } catch (error) {
      console.error('Error getting demand forecast:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Real-time risk monitoring
   */
  async startRiskMonitoring(callback) {
    try {
      // This would typically use WebSocket or Server-Sent Events
      const eventSource = new EventSource('/api/ml/monitor/risks');
      
      eventSource.onmessage = (event) => {
        const riskData = JSON.parse(event.data);
        callback(riskData);
      };

      eventSource.onerror = (error) => {
        console.error('Risk monitoring connection error:', error);
        // Attempt to reconnect after 5 seconds
        setTimeout(() => this.startRiskMonitoring(callback), 5000);
      };

      return eventSource;
    } catch (error) {
      console.error('Error starting risk monitoring:', error);
      throw this.handleError(error);
    }
  }

  /**
   * Handle API errors
   */
  handleError(error) {
    if (error.response) {
      // Server responded with error status
      const { status, data } = error.response;
      return {
        status,
        message: data.message || data.error || 'Unknown server error',
        details: data
      };
    } else if (error.request) {
      // Request made but no response received
      return {
        status: 0,
        message: 'Network error - unable to reach ML service',
        details: error.request
      };
    } else {
      // Something else happened
      return {
        status: -1,
        message: error.message || 'Unknown error occurred',
        details: error
      };
    }
  }

  /**
   * Fallback analytics data
   */
  getFallbackAnalytics() {
    return {
      totalShipments: 0,
      riskDistribution: {
        low: 0,
        medium: 0,
        high: 0,
        critical: 0
      },
      avgDelayPrediction: 0,
      supplierAnomalies: 0,
      predictions: [],
      fallback: true
    };
  }

  /**
   * Fallback risk trends data
   */
  getFallbackRiskTrends() {
    return {
      trends: [],
      summary: {
        avgRisk: 50,
        riskDirection: 'stable',
        volatility: 'low'
      },
      fallback: true
    };
  }

  /**
   * Format prediction data for display
   */
  formatPredictionForDisplay(prediction) {
    if (!prediction) return null;

    return {
      ...prediction,
      formattedDelay: this.formatDelay(prediction.predictedDelayHours),
      riskColor: this.getRiskColor(prediction.riskLevel),
      confidencePercentage: Math.round(prediction.confidenceScore * 100)
    };
  }

  /**
   * Format delay hours to human readable format
   */
  formatDelay(hours) {
    if (!hours) return 'No delay';
    
    if (hours < 1) {
      return `${Math.round(hours * 60)} minutes`;
    } else if (hours < 24) {
      return `${Math.round(hours * 10) / 10} hours`;
    } else {
      const days = Math.floor(hours / 24);
      const remainingHours = Math.round(hours % 24);
      return `${days} day${days > 1 ? 's' : ''}${remainingHours > 0 ? ` ${remainingHours}h` : ''}`;
    }
  }

  /**
   * Get color for risk level
   */
  getRiskColor(riskLevel) {
    const colors = {
      LOW: '#4caf50',
      MEDIUM: '#ff9800',
      HIGH: '#f44336',
      CRITICAL: '#d32f2f'
    };
    return colors[riskLevel] || '#9e9e9e';
  }

  /**
   * Get risk level priority
   */
  getRiskPriority(riskLevel) {
    const priorities = {
      LOW: 1,
      MEDIUM: 2,
      HIGH: 3,
      CRITICAL: 4
    };
    return priorities[riskLevel] || 0;
  }

  /**
   * Validate prediction request data
   */
  validatePredictionRequest(data) {
    const required = ['shipmentId', 'carrier'];
    const missing = required.filter(field => !data[field]);
    
    if (missing.length > 0) {
      throw new Error(`Missing required fields: ${missing.join(', ')}`);
    }

    // Validate numeric fields
    if (data.distanceKm && (isNaN(data.distanceKm) || data.distanceKm < 0)) {
      throw new Error('Distance must be a positive number');
    }

    if (data.routeComplexity && (data.routeComplexity < 1 || data.routeComplexity > 5)) {
      throw new Error('Route complexity must be between 1 and 5');
    }

    return true;
  }

  /**
   * Create prediction request from shipment data
   */
  createPredictionRequest(shipment) {
    return {
      shipmentId: shipment.id || shipment.shipmentId,
      carrier: shipment.carrier,
      distanceKm: shipment.distance || shipment.distanceKm,
      routeComplexity: shipment.routeComplexity || 3,
      weatherRisk: shipment.weatherRisk || 2,
      priorityLevel: shipment.priority === 'HIGH' ? 3 : shipment.priority === 'MEDIUM' ? 2 : 1,
      supplierRiskScore: shipment.supplier?.riskScore || 50,
      createdDate: shipment.createdDate || shipment.shipmentDate,
      geopoliticalRisk: shipment.geopoliticalRisk || 2
    };
  }
}

export default new MLService();