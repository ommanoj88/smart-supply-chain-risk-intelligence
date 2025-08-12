import axios from 'axios';
import { performanceMonitor } from './performanceMonitor';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Enhanced API configuration
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000, // 10 second timeout
  headers: {
    'Content-Type': 'application/json',
  },
});

// Rate limiting tracker
const rateLimitTracker = {
  attempts: new Map(),
  isRateLimited: false,
  resetTime: null,
};

// Request interceptor with performance monitoring and security
api.interceptors.request.use(
  (config) => {
    // Add performance tracking
    config.metadata = { startTime: performance.now() };
    
    // Add auth token
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Add CSRF token if available
    const csrfToken = document.querySelector('meta[name="csrf-token"]')?.getAttribute('content');
    if (csrfToken) {
      config.headers['X-XSRF-TOKEN'] = csrfToken;
    }
    
    // Add request ID for tracking
    config.headers['X-Request-ID'] = `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    
    // Check rate limiting
    if (rateLimitTracker.isRateLimited && rateLimitTracker.resetTime && Date.now() < rateLimitTracker.resetTime) {
      return Promise.reject(new Error('Rate limited - please wait before making more requests'));
    }
    
    return config;
  },
  (error) => {
    console.error('Request setup error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor with enhanced error handling and monitoring
api.interceptors.response.use(
  (response) => {
    // Track performance
    const endTime = performance.now();
    const startTime = response.config.metadata?.startTime || endTime;
    const endpoint = `${response.config.method?.toUpperCase()} ${response.config.url}`;
    
    performanceMonitor.measureApiCall(endpoint, startTime, endTime, response.status);
    
    // Handle rate limit headers
    const remaining = response.headers['x-rate-limit-remaining'];
    if (remaining !== undefined && parseInt(remaining) < 5) {
      console.warn(`Rate limit warning: ${remaining} requests remaining`);
    }
    
    // Reset rate limiting if successful
    if (rateLimitTracker.isRateLimited) {
      rateLimitTracker.isRateLimited = false;
      rateLimitTracker.resetTime = null;
    }
    
    return response;
  },
  (error) => {
    // Track performance for failed requests
    const endTime = performance.now();
    const startTime = error.config?.metadata?.startTime || endTime;
    const endpoint = `${error.config?.method?.toUpperCase()} ${error.config?.url}`;
    const status = error.response?.status || 0;
    
    performanceMonitor.measureApiCall(endpoint, startTime, endTime, status);
    
    // Handle different error types
    if (error.response) {
      const { status, data, headers } = error.response;
      
      switch (status) {
        case 401:
          // Unauthorized - clear auth and redirect
          console.error('Authentication failed - redirecting to login');
          localStorage.removeItem('authToken');
          localStorage.removeItem('user');
          // Don't redirect immediately to avoid infinite loops
          setTimeout(() => {
            if (window.location.pathname !== '/login') {
              window.location.href = '/login';
            }
          }, 100);
          break;
          
        case 403:
          // Forbidden - insufficient permissions
          console.error('Access forbidden - insufficient permissions');
          break;
          
        case 429:
          // Rate limited
          const retryAfter = headers['retry-after'];
          rateLimitTracker.isRateLimited = true;
          rateLimitTracker.resetTime = retryAfter ? Date.now() + (parseInt(retryAfter) * 1000) : Date.now() + 60000;
          console.warn(`Rate limited. Retry after: ${retryAfter || 60} seconds`);
          break;
          
        case 500:
        case 502:
        case 503:
        case 504:
          // Server errors
          console.error('Server error occurred:', status, data?.message || 'Unknown server error');
          break;
          
        default:
          console.error('API error:', status, data?.message || error.message);
      }
      
      // Enhanced error object
      const enhancedError = new Error(data?.message || `HTTP ${status} Error`);
      enhancedError.status = status;
      enhancedError.code = data?.code;
      enhancedError.details = data?.details;
      enhancedError.endpoint = endpoint;
      
      return Promise.reject(enhancedError);
    } else if (error.request) {
      // Network error
      console.error('Network error - request was made but no response received');
      const networkError = new Error('Network error - please check your connection');
      networkError.type = 'NETWORK_ERROR';
      return Promise.reject(networkError);
    } else {
      // Request setup error
      console.error('Request configuration error:', error.message);
      return Promise.reject(error);
    }
  }
);

// Enhanced auth API with better error handling
export const authAPI = {
  verifyToken: (idToken) => api.post('/auth/verify', { idToken }),
  getCurrentUser: () => api.get('/auth/user'),
  updateUserRole: (firebaseUid, role) => api.put('/auth/user/role', { firebaseUid, role }),
  
  // JWT authentication endpoints
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  logout: () => api.post('/auth/logout'),
  
  // Password reset
  forgotPassword: (email) => api.post('/auth/forgot-password', { email }),
  resetPassword: (token, password) => api.post('/auth/reset-password', { token, newPassword: password }),
  validateResetToken: (token) => api.post('/auth/validate-reset-token', { token }),
};

// Utility functions for API handling
export const apiUtils = {
  // Retry failed requests with exponential backoff
  retryRequest: async (requestFn, maxRetries = 3, baseDelay = 1000) => {
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        return await requestFn();
      } catch (error) {
        if (attempt === maxRetries || error.status < 500) {
          throw error;
        }
        
        const delay = baseDelay * Math.pow(2, attempt - 1);
        await new Promise(resolve => setTimeout(resolve, delay));
      }
    }
  },
  
  // Batch multiple requests
  batchRequests: async (requests, batchSize = 5) => {
    const results = [];
    for (let i = 0; i < requests.length; i += batchSize) {
      const batch = requests.slice(i, i + batchSize);
      const batchResults = await Promise.allSettled(batch.map(req => req()));
      results.push(...batchResults);
    }
    return results;
  },
  
  // Cache API responses
  cacheResponse: (key, response, ttl = 300000) => { // 5 minute default TTL
    const cacheData = {
      data: response,
      timestamp: Date.now(),
      ttl
    };
    localStorage.setItem(`api_cache_${key}`, JSON.stringify(cacheData));
  },
  
  getCachedResponse: (key) => {
    const cached = localStorage.getItem(`api_cache_${key}`);
    if (!cached) return null;
    
    const { data, timestamp, ttl } = JSON.parse(cached);
    if (Date.now() - timestamp > ttl) {
      localStorage.removeItem(`api_cache_${key}`);
      return null;
    }
    
    return data;
  }
};

export default api;