import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import Card from '../ui/Card';
import Button from '../ui/Button';
import StatusBadge from '../ui/StatusBadge';

// Icons
const TruckIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
  </svg>
);

const ClockIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
  </svg>
);

const LocationIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
  </svg>
);

const AlertIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.996-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
  </svg>
);

const CheckIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
  </svg>
);

const PackageIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
  </svg>
);

const TimelineEvent = ({ event, isLast, isActive }) => {
  const getStatusIcon = (status) => {
    switch (status.toLowerCase()) {
      case 'completed':
      case 'delivered':
        return <CheckIcon />;
      case 'delayed':
      case 'exception':
        return <AlertIcon />;
      case 'in_transit':
      case 'out_for_delivery':
        return <TruckIcon />;
      default:
        return <PackageIcon />;
    }
  };

  const getStatusColor = (status) => {
    switch (status.toLowerCase()) {
      case 'completed':
      case 'delivered':
        return 'bg-green-500 text-white';
      case 'delayed':
      case 'exception':
        return 'bg-red-500 text-white';
      case 'in_transit':
      case 'out_for_delivery':
        return 'bg-blue-500 text-white';
      default:
        return 'bg-gray-400 text-white';
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, x: -20 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.3 }}
      className="relative flex items-start space-x-4 pb-6"
    >
      {/* Timeline line */}
      {!isLast && (
        <div className="absolute left-5 top-10 w-0.5 h-full bg-gray-200"></div>
      )}
      
      {/* Status icon */}
      <div className={`
        flex items-center justify-center w-10 h-10 rounded-full z-10
        ${getStatusColor(event.status)}
        ${isActive ? 'ring-4 ring-blue-100' : ''}
      `}>
        {getStatusIcon(event.status)}
      </div>
      
      {/* Event details */}
      <div className="flex-1 min-w-0">
        <div className="flex items-center justify-between">
          <h3 className="text-sm font-medium text-gray-900">
            {event.description}
          </h3>
          <span className="text-xs text-gray-500">{event.timestamp}</span>
        </div>
        <div className="mt-1 flex items-center space-x-4 text-xs text-gray-500">
          {event.location && (
            <div className="flex items-center">
              <LocationIcon />
              <span className="ml-1">{event.location}</span>
            </div>
          )}
          {event.carrier && (
            <span className="font-medium">{event.carrier}</span>
          )}
        </div>
        {event.notes && (
          <p className="mt-2 text-sm text-gray-600">{event.notes}</p>
        )}
      </div>
    </motion.div>
  );
};

const EnhancedShipmentTracking = ({ shipmentId }) => {
  const [shipment, setShipment] = useState(null);
  const [timeline, setTimeline] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentStatus, setCurrentStatus] = useState('');

  useEffect(() => {
    const fetchShipmentData = async () => {
      try {
        setLoading(true);
        
        // Mock shipment data
        const mockShipment = {
          id: shipmentId || 'TRK001',
          trackingNumber: 'TRK-2024-001-US',
          status: 'In Transit',
          carrier: 'DHL Express',
          service: 'Express Worldwide',
          origin: {
            address: '123 Manufacturing St, Shenzhen, China',
            contact: 'Global Tech Manufacturing',
            phone: '+86 755 1234 5678'
          },
          destination: {
            address: '456 Business Ave, New York, NY 10001, USA',
            contact: 'TechCorp Inc.',
            phone: '+1 212 555 0123'
          },
          estimatedDelivery: '2024-01-25 15:00',
          actualDelivery: null,
          priority: 'High',
          value: '$15,250.00',
          weight: '45.2 kg',
          dimensions: '60x40x30 cm',
          packageCount: 3,
          riskScore: 2.1,
          supplier: 'Global Tech Manufacturing'
        };

        const mockTimeline = [
          {
            id: 1,
            status: 'completed',
            description: 'Shipment created',
            location: 'Shenzhen, China',
            timestamp: '2024-01-20 09:00',
            carrier: 'DHL Express',
            notes: 'Package prepared and ready for collection'
          },
          {
            id: 2,
            status: 'completed',
            description: 'Picked up from supplier',
            location: 'Shenzhen, China',
            timestamp: '2024-01-20 14:30',
            carrier: 'DHL Express',
            notes: 'Package collected from Global Tech Manufacturing'
          },
          {
            id: 3,
            status: 'completed',
            description: 'Arrived at DHL facility',
            location: 'Shenzhen Sorting Center',
            timestamp: '2024-01-20 18:45',
            carrier: 'DHL Express',
            notes: 'Package processed and sorted for international shipping'
          },
          {
            id: 4,
            status: 'completed',
            description: 'Departed origin facility',
            location: 'Shenzhen International Airport',
            timestamp: '2024-01-21 02:15',
            carrier: 'DHL Express',
            notes: 'Package loaded on flight DHL8901 to Cincinnati'
          },
          {
            id: 5,
            status: 'completed',
            description: 'In transit',
            location: 'Cincinnati Hub, USA',
            timestamp: '2024-01-21 16:30',
            carrier: 'DHL Express',
            notes: 'Package arrived at DHL Cincinnati Hub and processed'
          },
          {
            id: 6,
            status: 'in_transit',
            description: 'Out for delivery',
            location: 'New York, NY',
            timestamp: '2024-01-22 08:00',
            carrier: 'DHL Express',
            notes: 'Package loaded on delivery vehicle'
          },
          {
            id: 7,
            status: 'pending',
            description: 'Delivery scheduled',
            location: 'New York, NY 10001',
            timestamp: '2024-01-22 15:00 (Estimated)',
            carrier: 'DHL Express',
            notes: 'Scheduled for delivery between 9:00 AM - 6:00 PM'
          }
        ];

        setShipment(mockShipment);
        setTimeline(mockTimeline);
        setCurrentStatus(mockShipment.status);
      } catch (error) {
        console.error('Error fetching shipment data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchShipmentData();
    
    // Set up real-time updates (every 30 seconds)
    const interval = setInterval(fetchShipmentData, 30000);
    return () => clearInterval(interval);
  }, [shipmentId]);

  const getStatusColor = (status) => {
    switch (status.toLowerCase()) {
      case 'delivered': return 'green';
      case 'in transit': return 'blue';
      case 'delayed': return 'red';
      case 'pending': return 'orange';
      default: return 'gray';
    }
  };

  const getRiskColor = (score) => {
    if (score <= 1.5) return 'green';
    if (score <= 2.5) return 'orange';
    return 'red';
  };

  if (loading) {
    return (
      <div className="p-6">
        <div className="animate-pulse space-y-6">
          <div className="h-8 bg-gray-200 rounded w-1/3"></div>
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 space-y-4">
              {[...Array(6)].map((_, i) => (
                <div key={i} className="h-20 bg-gray-200 rounded"></div>
              ))}
            </div>
            <div className="space-y-4">
              {[...Array(3)].map((_, i) => (
                <div key={i} className="h-32 bg-gray-200 rounded"></div>
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!shipment) {
    return (
      <div className="p-6 text-center">
        <h2 className="text-xl font-semibold text-gray-900">Shipment not found</h2>
        <p className="text-gray-600">Please check the tracking number and try again.</p>
      </div>
    );
  }

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            Shipment Tracking
          </h1>
          <p className="text-gray-600">Track your shipment in real-time</p>
        </div>
        <div className="flex space-x-3">
          <Button variant="outline" size="sm">
            Share Tracking
          </Button>
          <Button variant="outline" size="sm">
            Download Report
          </Button>
        </div>
      </div>

      {/* Shipment Overview */}
      <Card variant="elevated">
        <Card.Content className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Tracking Information
              </h3>
              <div className="space-y-3">
                <div>
                  <span className="text-sm text-gray-600">Tracking Number:</span>
                  <div className="font-mono font-medium">{shipment.trackingNumber}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Status:</span>
                  <div className="mt-1">
                    <StatusBadge 
                      status={currentStatus} 
                      color={getStatusColor(currentStatus)} 
                    />
                  </div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Carrier:</span>
                  <div className="font-medium">{shipment.carrier}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Service:</span>
                  <div className="font-medium">{shipment.service}</div>
                </div>
              </div>
            </div>

            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Delivery Information
              </h3>
              <div className="space-y-3">
                <div>
                  <span className="text-sm text-gray-600">Estimated Delivery:</span>
                  <div className="font-medium flex items-center">
                    <ClockIcon />
                    <span className="ml-2">{shipment.estimatedDelivery}</span>
                  </div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Destination:</span>
                  <div className="font-medium">{shipment.destination.address}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Contact:</span>
                  <div className="font-medium">{shipment.destination.contact}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Priority:</span>
                  <div className="mt-1">
                    <StatusBadge 
                      status={shipment.priority} 
                      color={shipment.priority === 'High' ? 'red' : 'orange'} 
                    />
                  </div>
                </div>
              </div>
            </div>

            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-4">
                Package Details
              </h3>
              <div className="space-y-3">
                <div>
                  <span className="text-sm text-gray-600">Value:</span>
                  <div className="font-medium text-green-600">{shipment.value}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Weight:</span>
                  <div className="font-medium">{shipment.weight}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Dimensions:</span>
                  <div className="font-medium">{shipment.dimensions}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Risk Score:</span>
                  <div className="mt-1">
                    <StatusBadge 
                      status={shipment.riskScore.toFixed(1)} 
                      color={getRiskColor(shipment.riskScore)} 
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </Card.Content>
      </Card>

      {/* Timeline */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card variant="elevated">
            <Card.Header>
              <Card.Title>Shipment Timeline</Card.Title>
              <Card.Description>Track your package journey in real-time</Card.Description>
            </Card.Header>
            <Card.Content className="p-6">
              <div className="space-y-4">
                {timeline.map((event, index) => (
                  <TimelineEvent
                    key={event.id}
                    event={event}
                    isLast={index === timeline.length - 1}
                    isActive={event.status === 'in_transit'}
                  />
                ))}
              </div>
            </Card.Content>
          </Card>
        </div>

        <div className="space-y-6">
          {/* Quick Stats */}
          <Card variant="elevated">
            <Card.Header>
              <Card.Title>Quick Stats</Card.Title>
            </Card.Header>
            <Card.Content className="p-4">
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <span className="text-sm text-gray-600">Progress</span>
                  <span className="font-medium">75%</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div className="bg-blue-600 h-2 rounded-full" style={{width: '75%'}}></div>
                </div>
                <div className="grid grid-cols-2 gap-4 text-center">
                  <div>
                    <div className="text-lg font-bold text-gray-900">6</div>
                    <div className="text-xs text-gray-600">Completed</div>
                  </div>
                  <div>
                    <div className="text-lg font-bold text-blue-600">1</div>
                    <div className="text-xs text-gray-600">In Progress</div>
                  </div>
                </div>
              </div>
            </Card.Content>
          </Card>

          {/* Supplier Info */}
          <Card variant="elevated">
            <Card.Header>
              <Card.Title>Supplier Details</Card.Title>
            </Card.Header>
            <Card.Content className="p-4">
              <div className="space-y-3">
                <div>
                  <span className="text-sm text-gray-600">Supplier:</span>
                  <div className="font-medium">{shipment.supplier}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Origin:</span>
                  <div className="text-sm">{shipment.origin.address}</div>
                </div>
                <div>
                  <span className="text-sm text-gray-600">Contact:</span>
                  <div className="text-sm">{shipment.origin.phone}</div>
                </div>
              </div>
            </Card.Content>
          </Card>

          {/* Actions */}
          <Card variant="elevated">
            <Card.Header>
              <Card.Title>Actions</Card.Title>
            </Card.Header>
            <Card.Content className="p-4 space-y-3">
              <Button variant="outline" className="w-full" size="sm">
                Update Delivery Instructions
              </Button>
              <Button variant="outline" className="w-full" size="sm">
                Contact Carrier
              </Button>
              <Button variant="outline" className="w-full" size="sm">
                Report Issue
              </Button>
            </Card.Content>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default EnhancedShipmentTracking;