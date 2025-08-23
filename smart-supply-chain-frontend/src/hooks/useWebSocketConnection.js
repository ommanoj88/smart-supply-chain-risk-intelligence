import { useState, useEffect, useRef, useCallback } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export const useWebSocketConnection = (options = {}) => {
  const {
    url = 'http://localhost:8080/ws',
    debug = false,
    autoConnect = true,
    reconnectDelay = 5000,
    maxReconnectAttempts = 5
  } = options;

  const [connectionStatus, setConnectionStatus] = useState('DISCONNECTED');
  const [error, setError] = useState(null);
  const [reconnectAttempts, setReconnectAttempts] = useState(0);
  
  const clientRef = useRef(null);
  const subscriptionsRef = useRef(new Map());
  const reconnectTimeoutRef = useRef(null);

  const connect = useCallback(() => {
    if (clientRef.current?.connected) {
      return;
    }

    setConnectionStatus('CONNECTING');
    setError(null);

    try {
      const client = new Client({
        webSocketFactory: () => new SockJS(url),
        connectHeaders: {},
        debug: debug ? (str) => console.log('STOMP:', str) : undefined,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        reconnectDelay: reconnectDelay,
        onConnect: () => {
          console.log('WebSocket connected successfully');
          setConnectionStatus('CONNECTED');
          setReconnectAttempts(0);
          setError(null);
          
          // Resubscribe to all topics
          subscriptionsRef.current.forEach((callback, topic) => {
            client.subscribe(topic, callback);
          });
        },
        onDisconnect: () => {
          console.log('WebSocket disconnected');
          setConnectionStatus('DISCONNECTED');
          
          // Attempt reconnection if not at max attempts
          if (reconnectAttempts < maxReconnectAttempts) {
            reconnectTimeoutRef.current = setTimeout(() => {
              setReconnectAttempts(prev => prev + 1);
              connect();
            }, reconnectDelay);
          }
        },
        onStompError: (frame) => {
          console.error('STOMP error:', frame);
          setConnectionStatus('ERROR');
          setError(frame.headers?.message || 'WebSocket connection error');
        }
      });

      client.activate();
      clientRef.current = client;

    } catch (err) {
      console.error('WebSocket connection failed:', err);
      setConnectionStatus('ERROR');
      setError(err.message);
    }
  }, [url, debug, reconnectDelay, reconnectAttempts, maxReconnectAttempts]);

  const disconnect = useCallback(() => {
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
    }
    
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
    }
    
    setConnectionStatus('DISCONNECTED');
    subscriptionsRef.current.clear();
  }, []);

  const subscribe = useCallback((topic, callback) => {
    if (!topic || !callback) {
      console.warn('Invalid topic or callback for subscription');
      return null;
    }

    // Store subscription for reconnection
    subscriptionsRef.current.set(topic, callback);

    // Subscribe if connected
    if (clientRef.current?.connected) {
      return clientRef.current.subscribe(topic, callback);
    }

    return null;
  }, []);

  const unsubscribe = useCallback((topic) => {
    subscriptionsRef.current.delete(topic);
    
    if (clientRef.current?.connected) {
      // Find and unsubscribe from the topic
      // Note: In a real implementation, you'd need to track subscription objects
      console.log(`Unsubscribing from ${topic}`);
    }
  }, []);

  const publish = useCallback((destination, body, headers = {}) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination,
        body: typeof body === 'string' ? body : JSON.stringify(body),
        headers
      });
    } else {
      console.warn('Cannot publish message - WebSocket not connected');
    }
  }, []);

  const sendMessage = useCallback((destination, message) => {
    publish(destination, message);
  }, [publish]);

  // Auto-connect on mount if enabled
  useEffect(() => {
    if (autoConnect) {
      connect();
    }

    return () => {
      disconnect();
    };
  }, [autoConnect, connect, disconnect]);

  return {
    connectionStatus,
    error,
    reconnectAttempts,
    connect,
    disconnect,
    subscribe,
    unsubscribe,
    publish,
    sendMessage,
    isConnected: connectionStatus === 'CONNECTED',
    isConnecting: connectionStatus === 'CONNECTING',
    hasError: connectionStatus === 'ERROR'
  };
};

// Higher-order component for analytics WebSocket subscriptions
export const useAnalyticsWebSocket = (options = {}) => {
  const webSocket = useWebSocketConnection(options);
  const [analyticsData, setAnalyticsData] = useState({});
  const [realTimeUpdates, setRealTimeUpdates] = useState([]);
  const [riskUpdates, setRiskUpdates] = useState({});
  const [recommendations, setRecommendations] = useState([]);

  useEffect(() => {
    if (webSocket.isConnected) {
      // Subscribe to analytics updates
      webSocket.subscribe('/topic/analytics', (message) => {
        try {
          const update = JSON.parse(message.body);
          if (update.type === 'ANALYTICS_SUMMARY') {
            setAnalyticsData(prev => ({ ...prev, ...update.summary }));
          }
          setRealTimeUpdates(prev => [update, ...prev.slice(0, 19)]);
        } catch (error) {
          console.error('Error parsing analytics message:', error);
        }
      });

      // Subscribe to risk updates
      webSocket.subscribe('/topic/risk-updates', (message) => {
        try {
          const update = JSON.parse(message.body);
          setRiskUpdates(prev => ({
            ...prev,
            [update.supplierId]: update
          }));
          setRealTimeUpdates(prev => [update, ...prev.slice(0, 19)]);
        } catch (error) {
          console.error('Error parsing risk update:', error);
        }
      });

      // Subscribe to predictions
      webSocket.subscribe('/topic/predictions', (message) => {
        try {
          const update = JSON.parse(message.body);
          setRealTimeUpdates(prev => [update, ...prev.slice(0, 19)]);
        } catch (error) {
          console.error('Error parsing prediction update:', error);
        }
      });

      // Subscribe to recommendations
      webSocket.subscribe('/topic/recommendations', (message) => {
        try {
          const update = JSON.parse(message.body);
          if (update.type === 'RECOMMENDATION_UPDATE') {
            setRecommendations(update.recommendations);
          }
          setRealTimeUpdates(prev => [update, ...prev.slice(0, 19)]);
        } catch (error) {
          console.error('Error parsing recommendation update:', error);
        }
      });

      // Subscribe to alerts
      webSocket.subscribe('/topic/alerts', (message) => {
        try {
          const alert = JSON.parse(message.body);
          setRealTimeUpdates(prev => [alert, ...prev.slice(0, 19)]);
        } catch (error) {
          console.error('Error parsing alert:', error);
        }
      });

      // Subscribe to performance metrics
      webSocket.subscribe('/topic/performance', (message) => {
        try {
          const metrics = JSON.parse(message.body);
          setAnalyticsData(prev => ({ 
            ...prev, 
            performanceMetrics: metrics.metrics 
          }));
        } catch (error) {
          console.error('Error parsing performance metrics:', error);
        }
      });
    }
  }, [webSocket.isConnected]); // eslint-disable-line react-hooks/exhaustive-deps

  return {
    ...webSocket,
    analyticsData,
    realTimeUpdates,
    riskUpdates,
    recommendations,
    clearUpdates: () => setRealTimeUpdates([]),
    getRiskForSupplier: (supplierId) => riskUpdates[supplierId]
  };
};

export default useWebSocketConnection;