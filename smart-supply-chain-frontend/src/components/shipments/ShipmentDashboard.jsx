import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import Card from '../ui/Card';
import KPIWidget from '../ui/KPIWidget';
import DataTable from '../ui/DataTable';
import StatusBadge from '../ui/StatusBadge';
import Button from '../ui/Button';

const ShipmentDashboard = () => {
  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [metrics, setMetrics] = useState({
    totalShipments: 0,
    inTransit: 0,
    delivered: 0,
    exceptions: 0,
    onTimePerformance: 0
  });

  // Mock data for demonstration
  useEffect(() => {
    // Simulate API call
    setTimeout(() => {
      setShipments([
        {
          id: 1,
          trackingNumber: 'TRK001234567',
          supplier: 'Acme Corp',
          carrier: 'DHL Express',
          status: 'IN_TRANSIT',
          origin: 'Shanghai, CN',
          destination: 'New York, US',
          estimatedDelivery: '2024-01-15T10:30:00Z',
          value: 15000,
          riskScore: 25
        },
        {
          id: 2,
          trackingNumber: 'TRK001234568',
          supplier: 'Global Supplies',
          carrier: 'FedEx',
          status: 'DELIVERED',
          origin: 'Mumbai, IN',
          destination: 'London, UK',
          estimatedDelivery: '2024-01-10T14:00:00Z',
          value: 8500,
          riskScore: 10
        },
        {
          id: 3,
          trackingNumber: 'TRK001234569',
          supplier: 'Tech Solutions',
          carrier: 'UPS',
          status: 'EXCEPTION',
          origin: 'Frankfurt, DE',
          destination: 'Toronto, CA',
          estimatedDelivery: '2024-01-12T09:15:00Z',
          value: 22000,
          riskScore: 85
        }
      ]);

      setMetrics({
        totalShipments: 156,
        inTransit: 42,
        delivered: 98,
        exceptions: 3,
        onTimePerformance: 94.2
      });

      setLoading(false);
    }, 1000);
  }, []);

  const columns = [
    {
      accessor: 'trackingNumber',
      header: 'Tracking Number',
      render: (value) => (
        <span className="font-mono text-sm font-medium text-blue-600">
          {value}
        </span>
      )
    },
    {
      accessor: 'supplier',
      header: 'Supplier'
    },
    {
      accessor: 'carrier',
      header: 'Carrier'
    },
    {
      accessor: 'status',
      header: 'Status',
      render: (value) => <StatusBadge status={value} />
    },
    {
      accessor: 'origin',
      header: 'Origin'
    },
    {
      accessor: 'destination',
      header: 'Destination'
    },
    {
      accessor: 'value',
      header: 'Value',
      render: (value) => (
        <span className="font-medium">
          ${value?.toLocaleString()}
        </span>
      )
    },
    {
      accessor: 'riskScore',
      header: 'Risk',
      render: (value) => (
        <StatusBadge 
          status={value > 70 ? 'HIGH' : value > 30 ? 'MEDIUM' : 'LOW'} 
          variant={value > 70 ? 'error' : value > 30 ? 'warning' : 'success'}
        />
      )
    }
  ];

  const handleRowClick = (shipment) => {
    console.log('Clicked shipment:', shipment);
    // Navigate to shipment details
  };

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <motion.div
          className="mb-8"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Shipment Tracking Dashboard
          </h1>
          <p className="text-gray-600">
            Monitor and manage your shipments in real-time with advanced tracking capabilities
          </p>
        </motion.div>

        {/* KPI Metrics */}
        <motion.div
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-6 mb-8"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.1 }}
        >
          <KPIWidget
            title="Total Shipments"
            value={metrics.totalShipments}
            trend="up"
            trendValue="+12%"
            color="blue"
            icon={
              <svg fill="currentColor" viewBox="0 0 20 20" className="w-full h-full">
                <path d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z" />
              </svg>
            }
          />

          <KPIWidget
            title="In Transit"
            value={metrics.inTransit}
            trend="up"
            trendValue="+8%"
            color="orange"
            icon={
              <svg fill="currentColor" viewBox="0 0 20 20" className="w-full h-full">
                <path d="M8 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM15 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
                <path d="M3 4a1 1 0 00-1 1v10a1 1 0 001 1h1.05a2.5 2.5 0 014.9 0H10a1 1 0 001-1V5a1 1 0 00-1-1H3zM14 7a1 1 0 00-1 1v6.05A2.5 2.5 0 0115.95 16H17a1 1 0 001-1v-5a1 1 0 00-.293-.707L16 7.586A1 1 0 0015.414 7H14z" />
              </svg>
            }
          />

          <KPIWidget
            title="Delivered"
            value={metrics.delivered}
            trend="up"
            trendValue="+15%"
            color="green"
            icon={
              <svg fill="currentColor" viewBox="0 0 20 20" className="w-full h-full">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
              </svg>
            }
          />

          <KPIWidget
            title="Exceptions"
            value={metrics.exceptions}
            trend="down"
            trendValue="-5%"
            color="red"
            icon={
              <svg fill="currentColor" viewBox="0 0 20 20" className="w-full h-full">
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
              </svg>
            }
          />

          <KPIWidget
            title="On-Time Performance"
            value={`${metrics.onTimePerformance}%`}
            trend="up"
            trendValue="+2.1%"
            color="indigo"
            icon={
              <svg fill="currentColor" viewBox="0 0 20 20" className="w-full h-full">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clipRule="evenodd" />
              </svg>
            }
          />
        </motion.div>

        {/* Quick Actions */}
        <motion.div
          className="mb-8"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.2 }}
        >
          <Card>
            <Card.Content>
              <div className="flex flex-wrap gap-4">
                <Button variant="primary" size="lg">
                  Create Shipment
                </Button>
                <Button variant="secondary" size="lg">
                  Track Shipment
                </Button>
                <Button variant="outline" size="lg">
                  Generate Report
                </Button>
                <Button variant="ghost" size="lg">
                  Export Data
                </Button>
              </div>
            </Card.Content>
          </Card>
        </motion.div>

        {/* Recent Shipments Table */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3, delay: 0.3 }}
        >
          <Card>
            <Card.Header>
              <div className="flex items-center justify-between">
                <div>
                  <Card.Title>Recent Shipments</Card.Title>
                  <Card.Description>
                    Latest shipment activities and status updates
                  </Card.Description>
                </div>
                <Button variant="outline" size="sm">
                  View All
                </Button>
              </div>
            </Card.Header>
            <Card.Content className="p-0">
              <DataTable
                data={shipments}
                columns={columns}
                loading={loading}
                onRowClick={handleRowClick}
                rowClassName={(row) => 
                  row.status === 'EXCEPTION' ? 'bg-red-50' : ''
                }
                emptyState={
                  <div className="text-center py-12">
                    <svg className="mx-auto h-12 w-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
                    </svg>
                    <h3 className="text-lg font-medium text-gray-900 mb-2">No shipments found</h3>
                    <p className="text-gray-500 mb-4">Get started by creating your first shipment</p>
                    <Button variant="primary">Create Shipment</Button>
                  </div>
                }
              />
            </Card.Content>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};

export default ShipmentDashboard;