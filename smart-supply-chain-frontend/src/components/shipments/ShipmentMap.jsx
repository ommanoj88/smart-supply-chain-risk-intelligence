import React, { useState, useEffect } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline } from 'react-leaflet';
import { motion } from 'framer-motion';
import Card from '../ui/Card';
import StatusBadge from '../ui/StatusBadge';
import 'leaflet/dist/leaflet.css';
import L from 'leaflet';

// Fix for default markers in react-leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

// Custom icons for different shipment statuses
const createCustomIcon = (color, type = 'shipment') => {
  const svgIcon = `
    <svg width="25" height="25" viewBox="0 0 25 25" xmlns="http://www.w3.org/2000/svg">
      <circle cx="12.5" cy="12.5" r="10" fill="${color}" stroke="white" stroke-width="2"/>
      <circle cx="12.5" cy="12.5" r="6" fill="white"/>
      ${type === 'truck' ? 
        '<path d="M8 15h1.5c.275 0 .5-.225.5-.5s-.225-.5-.5-.5H8v-2h4v2h-.5c-.275 0-.5.225-.5.5s.225.5.5.5H13v1c0 .275.225.5.5.5s.5-.225.5-.5v-1h1.5c.275 0 .5-.225.5-.5s-.225-.5-.5-.5H14v-2c0-.275-.225-.5-.5-.5H8.5c-.275 0-.5.225-.5.5v3c0 .275.225.5.5.5z" fill="' + color + '"/>' : 
        '<circle cx="12.5" cy="12.5" r="3" fill="' + color + '"/>'
      }
    </svg>
  `;
  
  return new L.DivIcon({
    html: svgIcon,
    className: 'custom-marker',
    iconSize: [25, 25],
    iconAnchor: [12.5, 12.5],
    popupAnchor: [0, -10]
  });
};

const ShipmentMap = ({ shipments = [], selectedShipment, onShipmentSelect, className = '' }) => {
  const [mapCenter, setMapCenter] = useState([20, 0]); // Default center
  const [mapZoom, setMapZoom] = useState(2);
  const [realTimePositions, setRealTimePositions] = useState(new Map());

  // Mock real-time position updates
  useEffect(() => {
    const interval = setInterval(() => {
      setRealTimePositions(prev => {
        const newPositions = new Map(prev);
        
        shipments.forEach(shipment => {
          if (shipment.status === 'IN_TRANSIT' && shipment.currentLocation) {
            // Simulate movement by slightly adjusting coordinates
            const currentPos = newPositions.get(shipment.id) || shipment.currentLocation;
            const newLat = currentPos.lat + (Math.random() - 0.5) * 0.01;
            const newLng = currentPos.lng + (Math.random() - 0.5) * 0.01;
            
            newPositions.set(shipment.id, { lat: newLat, lng: newLng });
          }
        });
        
        return newPositions;
      });
    }, 5000); // Update every 5 seconds

    return () => clearInterval(interval);
  }, [shipments]);

  // Focus map on selected shipment
  useEffect(() => {
    if (selectedShipment && selectedShipment.currentLocation) {
      setMapCenter([selectedShipment.currentLocation.lat, selectedShipment.currentLocation.lng]);
      setMapZoom(8);
    }
  }, [selectedShipment]);

  const getMarkerIcon = (shipment) => {
    const colors = {
      CREATED: '#3B82F6',
      PICKED_UP: '#8B5CF6',
      IN_TRANSIT: '#F59E0B',
      OUT_FOR_DELIVERY: '#F97316',
      DELIVERED: '#10B981',
      EXCEPTION: '#EF4444'
    };
    
    const color = colors[shipment.status] || '#6B7280';
    const isSelected = selectedShipment?.id === shipment.id;
    
    return createCustomIcon(color, shipment.status === 'IN_TRANSIT' ? 'truck' : 'shipment');
  };

  const getRoutePolyline = (shipment) => {
    if (!shipment.origin || !shipment.destination) return null;
    
    const positions = [
      [shipment.origin.latitude, shipment.origin.longitude],
      [shipment.destination.latitude, shipment.destination.longitude]
    ];
    
    // Add current position if available
    const currentPos = realTimePositions.get(shipment.id) || shipment.currentLocation;
    if (currentPos && shipment.status === 'IN_TRANSIT') {
      positions.splice(1, 0, [currentPos.lat, currentPos.lng]);
    }
    
    return positions;
  };

  const handleMarkerClick = (shipment) => {
    onShipmentSelect?.(shipment);
  };

  return (
    <div className={`relative ${className}`}>
      <Card className="h-full">
        <Card.Header>
          <div className="flex items-center justify-between">
            <div>
              <Card.Title>Real-Time Shipment Map</Card.Title>
              <Card.Description>
                Track {shipments.length} active shipments globally
              </Card.Description>
            </div>
            <div className="flex items-center space-x-4">
              <div className="flex items-center space-x-2 text-sm">
                <div className="w-3 h-3 bg-orange-400 rounded-full"></div>
                <span>In Transit</span>
              </div>
              <div className="flex items-center space-x-2 text-sm">
                <div className="w-3 h-3 bg-green-500 rounded-full"></div>
                <span>Delivered</span>
              </div>
              <div className="flex items-center space-x-2 text-sm">
                <div className="w-3 h-3 bg-red-500 rounded-full"></div>
                <span>Exception</span>
              </div>
            </div>
          </div>
        </Card.Header>
        <Card.Content className="p-0 h-96 relative">
          <MapContainer
            center={mapCenter}
            zoom={mapZoom}
            style={{ height: '100%', width: '100%' }}
            className="rounded-b-lg"
          >
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            
            {shipments.map((shipment) => {
              // Origin marker
              if (shipment.origin?.latitude && shipment.origin?.longitude) {
                return (
                  <React.Fragment key={`origin-${shipment.id}`}>
                    <Marker
                      position={[shipment.origin.latitude, shipment.origin.longitude]}
                      icon={createCustomIcon('#10B981', 'origin')}
                    >
                      <Popup>
                        <div className="p-2">
                          <h4 className="font-medium text-gray-900">Origin</h4>
                          <p className="text-sm text-gray-600">{shipment.origin.name}</p>
                          <p className="text-sm text-gray-600">{shipment.origin.city}, {shipment.origin.country}</p>
                        </div>
                      </Popup>
                    </Marker>
                  </React.Fragment>
                );
              }
              return null;
            })}

            {shipments.map((shipment) => {
              // Destination marker
              if (shipment.destination?.latitude && shipment.destination?.longitude) {
                return (
                  <Marker
                    key={`destination-${shipment.id}`}
                    position={[shipment.destination.latitude, shipment.destination.longitude]}
                    icon={createCustomIcon('#EF4444', 'destination')}
                  >
                    <Popup>
                      <div className="p-2">
                        <h4 className="font-medium text-gray-900">Destination</h4>
                        <p className="text-sm text-gray-600">{shipment.destination.name}</p>
                        <p className="text-sm text-gray-600">{shipment.destination.city}, {shipment.destination.country}</p>
                      </div>
                    </Popup>
                  </Marker>
                );
              }
              return null;
            })}

            {shipments.map((shipment) => {
              // Current position marker (for in-transit shipments)
              const currentPos = realTimePositions.get(shipment.id) || shipment.currentLocation;
              if (currentPos && shipment.status === 'IN_TRANSIT') {
                return (
                  <motion.div key={`current-${shipment.id}`}>
                    <Marker
                      position={[currentPos.lat, currentPos.lng]}
                      icon={getMarkerIcon(shipment)}
                      eventHandlers={{
                        click: () => handleMarkerClick(shipment)
                      }}
                    >
                      <Popup>
                        <div className="p-3 min-w-[200px]">
                          <div className="flex items-center justify-between mb-2">
                            <h4 className="font-medium text-gray-900">
                              {shipment.trackingNumber}
                            </h4>
                            <StatusBadge status={shipment.status} size="sm" />
                          </div>
                          <div className="space-y-1 text-sm">
                            <p><strong>Supplier:</strong> {shipment.supplier}</p>
                            <p><strong>Carrier:</strong> {shipment.carrier}</p>
                            <p><strong>Value:</strong> ${shipment.value?.toLocaleString()}</p>
                            {shipment.estimatedDelivery && (
                              <p><strong>ETA:</strong> {new Date(shipment.estimatedDelivery).toLocaleDateString()}</p>
                            )}
                          </div>
                          <div className="mt-3 pt-2 border-t">
                            <button
                              onClick={() => handleMarkerClick(shipment)}
                              className="text-blue-600 hover:text-blue-800 text-sm font-medium"
                            >
                              View Details →
                            </button>
                          </div>
                        </div>
                      </Popup>
                    </Marker>
                  </motion.div>
                );
              }
              return null;
            })}

            {shipments.map((shipment) => {
              // Route polyline
              const routePositions = getRoutePolyline(shipment);
              if (routePositions && routePositions.length >= 2) {
                const isSelected = selectedShipment?.id === shipment.id;
                return (
                  <Polyline
                    key={`route-${shipment.id}`}
                    positions={routePositions}
                    color={isSelected ? '#3B82F6' : '#9CA3AF'}
                    weight={isSelected ? 4 : 2}
                    opacity={isSelected ? 0.8 : 0.5}
                    dashArray={shipment.status === 'DELIVERED' ? '5, 5' : undefined}
                  />
                );
              }
              return null;
            })}
          </MapContainer>

          {/* Real-time indicator */}
          <div className="absolute top-4 right-4 bg-white rounded-lg shadow-lg p-3 z-[1000]">
            <div className="flex items-center space-x-2">
              <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
              <span className="text-sm font-medium text-gray-700">Live Tracking</span>
            </div>
          </div>

          {/* Selected shipment info */}
          {selectedShipment && (
            <motion.div
              className="absolute bottom-4 left-4 bg-white rounded-lg shadow-lg p-4 z-[1000] max-w-xs"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 20 }}
            >
              <div className="flex items-center justify-between mb-2">
                <h4 className="font-medium text-gray-900">
                  {selectedShipment.trackingNumber}
                </h4>
                <button
                  onClick={() => onShipmentSelect?.(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ✕
                </button>
              </div>
              <div className="space-y-1 text-sm">
                <div className="flex items-center space-x-2">
                  <StatusBadge status={selectedShipment.status} size="sm" />
                  <span className="text-gray-600">{selectedShipment.carrier}</span>
                </div>
                <p className="text-gray-600">
                  <strong>From:</strong> {selectedShipment.origin?.city}, {selectedShipment.origin?.country}
                </p>
                <p className="text-gray-600">
                  <strong>To:</strong> {selectedShipment.destination?.city}, {selectedShipment.destination?.country}
                </p>
              </div>
            </motion.div>
          )}
        </Card.Content>
      </Card>
    </div>
  );
};

export default ShipmentMap;