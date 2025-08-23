import React, { useState, useEffect } from 'react';
import {
  Card, CardContent, CardHeader, Typography, Box,
  Button, Alert, Chip, List, ListItem, ListItemText
} from '@mui/material';
import { useAnalyticsWebSocket } from '../../hooks/useWebSocketConnection';

const WebSocketTestComponent = () => {
  const [testMessages, setTestMessages] = useState([]);
  
  const {
    connectionStatus,
    analyticsData,
    realTimeUpdates,
    isConnected,
    error,
    connect,
    disconnect
  } = useAnalyticsWebSocket({
    autoConnect: true,
    debug: true
  });

  // Add test messages when updates are received
  useEffect(() => {
    if (realTimeUpdates.length > 0) {
      const latestUpdate = realTimeUpdates[0];
      setTestMessages(prev => [
        {
          timestamp: new Date(),
          type: latestUpdate.type,
          data: latestUpdate
        },
        ...prev.slice(0, 9)
      ]);
    }
  }, [realTimeUpdates]);

  const sendTestMessage = () => {
    // This would trigger a test message from the backend
    console.log('Test message would be sent to backend');
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        WebSocket Connection Test
      </Typography>
      
      {/* Connection Status */}
      <Card sx={{ mb: 3 }}>
        <CardHeader title="Connection Status" />
        <CardContent>
          <Box display="flex" alignItems="center" gap={2} mb={2}>
            <Typography variant="body1">Status:</Typography>
            <Chip 
              label={connectionStatus} 
              color={isConnected ? 'success' : 'error'}
            />
          </Box>
          
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          
          <Box display="flex" gap={2}>
            <Button 
              variant="contained" 
              onClick={connect}
              disabled={isConnected}
            >
              Connect
            </Button>
            <Button 
              variant="outlined" 
              onClick={disconnect}
              disabled={!isConnected}
            >
              Disconnect
            </Button>
            <Button 
              variant="outlined" 
              onClick={sendTestMessage}
            >
              Send Test Message
            </Button>
          </Box>
        </CardContent>
      </Card>

      {/* Analytics Data */}
      <Card sx={{ mb: 3 }}>
        <CardHeader title="Analytics Data" />
        <CardContent>
          {Object.keys(analyticsData).length > 0 ? (
            <pre style={{ backgroundColor: '#f5f5f5', padding: '10px', borderRadius: '4px' }}>
              {JSON.stringify(analyticsData, null, 2)}
            </pre>
          ) : (
            <Typography variant="body2" color="text.secondary">
              No analytics data received yet
            </Typography>
          )}
        </CardContent>
      </Card>

      {/* Real-time Updates */}
      <Card>
        <CardHeader title={`Real-time Updates (${realTimeUpdates.length})`} />
        <CardContent>
          <List sx={{ maxHeight: 400, overflow: 'auto' }}>
            {realTimeUpdates.slice(0, 10).map((update, index) => (
              <ListItem key={index} divider>
                <ListItemText
                  primary={`${update.type}: ${update.message || 'Update received'}`}
                  secondary={new Date(update.timestamp).toLocaleString()}
                />
              </ListItem>
            ))}
            {realTimeUpdates.length === 0 && (
              <Typography variant="body2" color="text.secondary" textAlign="center">
                No real-time updates received yet
              </Typography>
            )}
          </List>
        </CardContent>
      </Card>

      {/* Test Messages */}
      {testMessages.length > 0 && (
        <Card sx={{ mt: 3 }}>
          <CardHeader title="Test Messages" />
          <CardContent>
            <List>
              {testMessages.map((message, index) => (
                <ListItem key={index} divider>
                  <ListItemText
                    primary={`${message.type} at ${message.timestamp.toLocaleTimeString()}`}
                    secondary={JSON.stringify(message.data, null, 2)}
                  />
                </ListItem>
              ))}
            </List>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default WebSocketTestComponent;