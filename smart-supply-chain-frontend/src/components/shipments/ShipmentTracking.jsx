import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import Card from '../ui/Card';
import StatusBadge from '../ui/StatusBadge';
import ProgressTimeline from '../ui/ProgressTimeline';
import Button from '../ui/Button';
import { format } from 'date-fns';

const ShipmentTracking = ({ trackingNumber, onClose }) => {
  const [shipment, setShipment] = useState(null);
  const [trackingEvents, setTrackingEvents] = useState([]);
  const [loading, setLoading] = useState(true);

  // Mock data for demonstration
  useEffect(() => {
    if (trackingNumber) {
      setTimeout(() => {
        setShipment({
          id: 1,
          trackingNumber: 'TRK001234567',
          referenceNumber: 'REF-2024-001',
          supplier: {
            name: 'Acme Manufacturing Corp',
            id: 1
          },
          carrier: 'DHL Express',
          status: 'IN_TRANSIT',
          substatus: 'Package in transit to destination',
          shipmentType: 'EXPRESS',
          serviceLevel: 'NEXT_DAY',
          
          // Origin and destination
          origin: {
            name: 'Acme Manufacturing Facility',
            address: '123 Industrial St',
            city: 'Shanghai',
            country: 'China',
            postalCode: '200001',
            latitude: 31.2304,
            longitude: 121.4737
          },
          destination: {
            name: 'ABC Distribution Center',
            address: '456 Commerce Ave',
            city: 'New York',
            state: 'NY',
            country: 'United States',
            postalCode: '10001',
            latitude: 40.7128,
            longitude: -74.0060
          },

          // Dates
          shipDate: '2024-01-10T08:30:00Z',
          estimatedDeliveryDate: '2024-01-15T17:00:00Z',
          
          // Package details
          weightKg: 15.5,
          dimensionsLengthCm: 40,
          dimensionsWidthCm: 30,
          dimensionsHeightCm: 20,
          declaredValue: 15000,
          currency: 'USD',
          
          // Risk and cost
          riskScore: 25,
          delayRiskProbability: 12.5,
          shippingCost: 250.00,
          totalCost: 275.00,
          carbonFootprintKg: 45.2,
          
          // Performance
          onTimePerformance: true,
          transitDays: 5
        });

        setTrackingEvents([
          {
            id: 1,
            eventCode: 'CREATED',
            eventDescription: 'Shipment created and booking confirmed',
            eventTimestamp: '2024-01-10T08:30:00Z',
            eventType: 'PICKUP',
            locationName: 'Shanghai Distribution Center',
            locationCity: 'Shanghai',
            locationCountry: 'China',
            isException: false
          },
          {
            id: 2,
            eventCode: 'PICKED_UP',
            eventDescription: 'Package picked up by carrier',
            eventTimestamp: '2024-01-10T14:15:00Z',
            eventType: 'PICKUP',
            locationName: 'DHL Service Point Shanghai',
            locationCity: 'Shanghai',
            locationCountry: 'China',
            isException: false
          },
          {
            id: 3,
            eventCode: 'DEPARTED',
            eventDescription: 'Package departed origin facility',
            eventTimestamp: '2024-01-10T22:45:00Z',
            eventType: 'TRANSIT',
            locationName: 'Shanghai International Airport',
            locationCity: 'Shanghai',
            locationCountry: 'China',
            isException: false
          },
          {
            id: 4,
            eventCode: 'IN_TRANSIT',
            eventDescription: 'Package in transit to destination country',
            eventTimestamp: '2024-01-11T02:30:00Z',
            eventType: 'TRANSIT',
            locationName: 'In Flight',
            locationCity: '',
            locationCountry: '',
            isException: false
          },
          {
            id: 5,
            eventCode: 'ARRIVED',
            eventDescription: 'Package arrived at destination facility',
            eventTimestamp: '2024-01-12T09:20:00Z',
            eventType: 'TRANSIT',
            locationName: 'DHL Hub New York',
            locationCity: 'New York',
            locationState: 'NY',
            locationCountry: 'United States',
            isException: false
          },
          {
            id: 6,
            eventCode: 'PROCESSING',
            eventDescription: 'Package being processed for final delivery',
            eventTimestamp: '2024-01-13T11:00:00Z',
            eventType: 'TRANSIT',
            locationName: 'DHL Delivery Station Manhattan',
            locationCity: 'New York',
            locationState: 'NY',
            locationCountry: 'United States',
            isException: false
          }
        ]);

        setLoading(false);
      }, 800);
    }
  }, [trackingNumber]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 p-6">
        <div className="max-w-4xl mx-auto">
          <div className="animate-pulse space-y-6">
            <div className="h-8 bg-gray-200 rounded w-1/3"></div>
            <div className="h-32 bg-gray-200 rounded"></div>
            <div className="h-64 bg-gray-200 rounded"></div>
          </div>
        </div>
      </div>
    );
  }

  if (!shipment) {
    return (
      <div className="min-h-screen bg-gray-50 p-6">
        <div className="max-w-4xl mx-auto text-center py-12">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Shipment Not Found</h2>
          <p className="text-gray-600">Please check your tracking number and try again.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <motion.div
          className="mb-8"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 mb-2">
                Shipment Tracking
              </h1>
              <p className="text-gray-600">
                Real-time tracking for {shipment.trackingNumber}
              </p>
            </div>
            {onClose && (
              <Button variant="secondary" onClick={onClose}>
                ← Back to Dashboard
              </Button>
            )}
          </div>
        </motion.div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Main Tracking Information */}
          <div className="lg:col-span-2 space-y-6">
            {/* Shipment Overview */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3, delay: 0.1 }}
            >
              <Card>
                <Card.Header>
                  <Card.Title>Shipment Overview</Card.Title>
                </Card.Header>
                <Card.Content>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <h4 className="font-medium text-gray-900 mb-3">Basic Information</h4>
                      <div className="space-y-2 text-sm">
                        <div className="flex justify-between">
                          <span className="text-gray-600">Tracking Number:</span>
                          <span className="font-mono font-medium">{shipment.trackingNumber}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Reference:</span>
                          <span className="font-medium">{shipment.referenceNumber}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Status:</span>
                          <StatusBadge status={shipment.status} />
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Carrier:</span>
                          <span className="font-medium">{shipment.carrier}</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Service Level:</span>
                          <span className="font-medium">{shipment.serviceLevel}</span>
                        </div>
                      </div>
                    </div>

                    <div>
                      <h4 className="font-medium text-gray-900 mb-3">Delivery Information</h4>
                      <div className="space-y-2 text-sm">
                        <div className="flex justify-between">
                          <span className="text-gray-600">Ship Date:</span>
                          <span className="font-medium">
                            {format(new Date(shipment.shipDate), 'MMM d, yyyy HH:mm')}
                          </span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Est. Delivery:</span>
                          <span className="font-medium">
                            {format(new Date(shipment.estimatedDeliveryDate), 'MMM d, yyyy HH:mm')}
                          </span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Transit Days:</span>
                          <span className="font-medium">{shipment.transitDays} days</span>
                        </div>
                        <div className="flex justify-between">
                          <span className="text-gray-600">Risk Score:</span>
                          <StatusBadge 
                            status={shipment.riskScore > 70 ? 'HIGH' : shipment.riskScore > 30 ? 'MEDIUM' : 'LOW'} 
                          />
                        </div>
                      </div>
                    </div>
                  </div>
                </Card.Content>
              </Card>
            </motion.div>

            {/* Tracking Timeline */}
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3, delay: 0.2 }}
            >
              <Card>
                <Card.Header>
                  <Card.Title>Tracking Timeline</Card.Title>
                  <Card.Description>
                    Complete journey of your shipment
                  </Card.Description>
                </Card.Header>
                <Card.Content>
                  <ProgressTimeline 
                    events={trackingEvents}
                    currentStatus={shipment.status}
                    animated={true}
                  />
                </Card.Content>
              </Card>
            </motion.div>
          </div>

          {/* Sidebar Information */}
          <div className="space-y-6">
            {/* Origin & Destination */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3, delay: 0.3 }}
            >
              <Card>
                <Card.Header>
                  <Card.Title>Route Information</Card.Title>
                </Card.Header>
                <Card.Content>
                  <div className="space-y-4">
                    <div>
                      <h4 className="font-medium text-gray-900 mb-2">📍 Origin</h4>
                      <div className="text-sm space-y-1">
                        <p className="font-medium">{shipment.origin.name}</p>
                        <p className="text-gray-600">{shipment.origin.address}</p>
                        <p className="text-gray-600">
                          {shipment.origin.city}, {shipment.origin.country} {shipment.origin.postalCode}
                        </p>
                      </div>
                    </div>

                    <div className="border-t pt-4">
                      <h4 className="font-medium text-gray-900 mb-2">🎯 Destination</h4>
                      <div className="text-sm space-y-1">
                        <p className="font-medium">{shipment.destination.name}</p>
                        <p className="text-gray-600">{shipment.destination.address}</p>
                        <p className="text-gray-600">
                          {shipment.destination.city}, {shipment.destination.state} {shipment.destination.country} {shipment.destination.postalCode}
                        </p>
                      </div>
                    </div>
                  </div>
                </Card.Content>
              </Card>
            </motion.div>

            {/* Package Details */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3, delay: 0.4 }}
            >
              <Card>
                <Card.Header>
                  <Card.Title>Package Details</Card.Title>
                </Card.Header>
                <Card.Content>
                  <div className="space-y-3 text-sm">
                    <div className="flex justify-between">
                      <span className="text-gray-600">Weight:</span>
                      <span className="font-medium">{shipment.weightKg} kg</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Dimensions:</span>
                      <span className="font-medium">
                        {shipment.dimensionsLengthCm} × {shipment.dimensionsWidthCm} × {shipment.dimensionsHeightCm} cm
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Declared Value:</span>
                      <span className="font-medium">
                        {shipment.currency} {shipment.declaredValue?.toLocaleString()}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">Shipping Cost:</span>
                      <span className="font-medium">
                        {shipment.currency} {shipment.shippingCost?.toFixed(2)}
                      </span>
                    </div>
                    <div className="flex justify-between border-t pt-2">
                      <span className="text-gray-600">Total Cost:</span>
                      <span className="font-medium">
                        {shipment.currency} {shipment.totalCost?.toFixed(2)}
                      </span>
                    </div>
                  </div>
                </Card.Content>
              </Card>
            </motion.div>

            {/* Environmental Impact */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.3, delay: 0.5 }}
            >
              <Card>
                <Card.Header>
                  <Card.Title>🌱 Environmental Impact</Card.Title>
                </Card.Header>
                <Card.Content>
                  <div className="text-center">
                    <div className="text-2xl font-bold text-green-600 mb-1">
                      {shipment.carbonFootprintKg} kg
                    </div>
                    <div className="text-sm text-gray-600">
                      Carbon footprint for this shipment
                    </div>
                  </div>
                </Card.Content>
              </Card>
            </motion.div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ShipmentTracking;