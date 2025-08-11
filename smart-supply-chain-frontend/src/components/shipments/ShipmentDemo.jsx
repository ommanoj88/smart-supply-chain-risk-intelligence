import React, { useState } from 'react';
import { motion } from 'framer-motion';
import ShipmentDashboard from './ShipmentDashboard';
import ShipmentTracking from './ShipmentTracking';
import ShipmentMap from './ShipmentMap';
import Card from '../ui/Card';
import Button from '../ui/Button';

const ShipmentDemo = () => {
  const [activeView, setActiveView] = useState('dashboard');
  const [selectedShipment, setSelectedShipment] = useState(null);

  // Mock shipments data for the map demo
  const mockShipments = [
    {
      id: 1,
      trackingNumber: 'TRK001234567',
      supplier: 'Acme Corp',
      carrier: 'DHL Express',
      status: 'IN_TRANSIT',
      value: 15000,
      origin: { latitude: 31.2304, longitude: 121.4737, city: 'Shanghai', country: 'China', name: 'Acme Factory' },
      destination: { latitude: 40.7128, longitude: -74.0060, city: 'New York', country: 'USA', name: 'Distribution Center' },
      currentLocation: { lat: 35.6762, lng: 139.6503 }, // Tokyo (in transit)
      estimatedDelivery: '2024-01-15T10:30:00Z'
    },
    {
      id: 2,
      trackingNumber: 'TRK001234568',
      supplier: 'Global Supplies',
      carrier: 'FedEx',
      status: 'DELIVERED',
      value: 8500,
      origin: { latitude: 19.0760, longitude: 72.8777, city: 'Mumbai', country: 'India', name: 'Manufacturing Hub' },
      destination: { latitude: 51.5074, longitude: -0.1278, city: 'London', country: 'UK', name: 'UK Warehouse' },
      estimatedDelivery: '2024-01-10T14:00:00Z'
    },
    {
      id: 3,
      trackingNumber: 'TRK001234569',
      supplier: 'Tech Solutions',
      carrier: 'UPS',
      status: 'EXCEPTION',
      value: 22000,
      origin: { latitude: 50.1109, longitude: 8.6821, city: 'Frankfurt', country: 'Germany', name: 'Tech Campus' },
      destination: { latitude: 43.6532, longitude: -79.3832, city: 'Toronto', country: 'Canada', name: 'Office Complex' },
      currentLocation: { lat: 52.5200, lng: 13.4050 }, // Berlin (exception location)
      estimatedDelivery: '2024-01-12T09:15:00Z'
    }
  ];

  const renderView = () => {
    switch (activeView) {
      case 'dashboard':
        return <ShipmentDashboard />;
      case 'tracking':
        return <ShipmentTracking trackingNumber="TRK001234567" onClose={() => setActiveView('dashboard')} />;
      case 'map':
        return (
          <div className="min-h-screen bg-gray-50 p-6">
            <div className="max-w-7xl mx-auto">
              <motion.div
                className="mb-8"
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3 }}
              >
                <h1 className="text-3xl font-bold text-gray-900 mb-2">
                  Global Shipment Map
                </h1>
                <p className="text-gray-600">
                  Real-time tracking of shipments across the globe
                </p>
              </motion.div>
              <ShipmentMap 
                shipments={mockShipments}
                selectedShipment={selectedShipment}
                onShipmentSelect={setSelectedShipment}
                className="h-[600px]"
              />
            </div>
          </div>
        );
      default:
        return <ShipmentDashboard />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <h1 className="text-2xl font-bold text-gray-900">
              Advanced Shipment Tracking System
            </h1>
            <div className="flex space-x-2">
              <Button
                variant={activeView === 'dashboard' ? 'primary' : 'secondary'}
                onClick={() => setActiveView('dashboard')}
                size="sm"
              >
                📊 Dashboard
              </Button>
              <Button
                variant={activeView === 'tracking' ? 'primary' : 'secondary'}
                onClick={() => setActiveView('tracking')}
                size="sm"
              >
                📦 Track Shipment
              </Button>
              <Button
                variant={activeView === 'map' ? 'primary' : 'secondary'}
                onClick={() => setActiveView('map')}
                size="sm"
              >
                🗺️ Global Map
              </Button>
            </div>
          </div>
        </div>
      </div>

      {/* Content */}
      <motion.div
        key={activeView}
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        exit={{ opacity: 0, x: -20 }}
        transition={{ duration: 0.3 }}
      >
        {renderView()}
      </motion.div>

      {/* Feature Showcase */}
      {activeView === 'dashboard' && (
        <div className="bg-white border-t">
          <div className="max-w-7xl mx-auto px-6 py-12">
            <h2 className="text-2xl font-bold text-gray-900 mb-8 text-center">
              Amazon-Inspired Features
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              <Card className="text-center">
                <Card.Content>
                  <div className="text-4xl mb-4">🎨</div>
                  <h3 className="text-lg font-semibold mb-2">Design System</h3>
                  <p className="text-gray-600 text-sm">
                    Amazon Ember fonts, orange accents, clean spacing, and professional card layouts
                  </p>
                </Card.Content>
              </Card>
              
              <Card className="text-center">
                <Card.Content>
                  <div className="text-4xl mb-4">📊</div>
                  <h3 className="text-lg font-semibold mb-2">Data Tables</h3>
                  <p className="text-gray-600 text-sm">
                    Advanced sorting, filtering, pagination, and search with Amazon-style density
                  </p>
                </Card.Content>
              </Card>
              
              <Card className="text-center">
                <Card.Content>
                  <div className="text-4xl mb-4">🗺️</div>
                  <h3 className="text-lg font-semibold mb-2">Real-time Maps</h3>
                  <p className="text-gray-600 text-sm">
                    Interactive shipment tracking with live position updates and route visualization
                  </p>
                </Card.Content>
              </Card>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ShipmentDemo;