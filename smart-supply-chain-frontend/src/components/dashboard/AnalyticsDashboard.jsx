import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import KPIWidget from '../ui/KPIWidget';
import Card from '../ui/Card';
import Button from '../ui/Button';
import { useAuth } from '../../context/AuthContext';

// Icons
const TruckIcon = () => (
  <svg viewBox="0 0 24 24" fill="currentColor" className="w-6 h-6">
    <path d="M19 7h-3V6a1 1 0 0 0-1-1H5a1 1 0 0 0-1 1v11a3 3 0 0 0 6 0h4a3 3 0 0 0 6 0v-7a1 1 0 0 0-1-1zM6 17a1 1 0 1 1 1-1 1 1 0 0 1-1 1zm12 0a1 1 0 1 1 1-1 1 1 0 0 1-1 1zm1-3h-1.18A3 3 0 0 0 15 12H9a3 3 0 0 0-2.82 2H5V7h9v2H6v2h12z"/>
  </svg>
);

const UsersIcon = () => (
  <svg viewBox="0 0 24 24" fill="currentColor" className="w-6 h-6">
    <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
  </svg>
);

const WarningIcon = () => (
  <svg viewBox="0 0 24 24" fill="currentColor" className="w-6 h-6">
    <path d="M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z"/>
  </svg>
);

const ChartIcon = () => (
  <svg viewBox="0 0 24 24" fill="currentColor" className="w-6 h-6">
    <path d="M3.5 18.49l6-6.01 4 4L22 6.92l-1.41-1.41-7.09 7.97-4-4L2 16.99z"/>
  </svg>
);

const AnalyticsDashboard = () => {
  const { user } = useAuth();
  const [dashboardData, setDashboardData] = useState({
    totalShipments: 0,
    activeShipments: 0,
    totalSuppliers: 0,
    highRiskAlerts: 0,
    onTimeDelivery: 0,
    avgRiskScore: 0,
    recentShipments: [],
    riskAlerts: []
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);
        
        // Simulate API calls for dashboard data
        const [shipmentsRes, suppliersRes, alertsRes] = await Promise.all([
          fetch('/api/shipments/dashboard-stats'),
          fetch('/api/suppliers/dashboard-stats'), 
          fetch('/api/alerts/dashboard-stats')
        ]).catch(() => {
          // Fallback to mock data if API isn't available
          return [
            { json: () => Promise.resolve({ total: 2847, active: 324, onTimeDelivery: 94.2 }) },
            { json: () => Promise.resolve({ total: 156, highRisk: 8 }) },
            { json: () => Promise.resolve({ alerts: 12, avgRiskScore: 2.3 }) }
          ];
        });

        // Mock data for development
        const mockData = {
          totalShipments: 2847,
          activeShipments: 324,
          totalSuppliers: 156,
          highRiskAlerts: 12,
          onTimeDelivery: 94.2,
          avgRiskScore: 2.3,
          recentShipments: [
            { id: 1, trackingNumber: 'TRK001', status: 'In Transit', supplier: 'Acme Corp', destination: 'New York' },
            { id: 2, trackingNumber: 'TRK002', status: 'Delivered', supplier: 'TechFlow Inc', destination: 'Los Angeles' },
            { id: 3, trackingNumber: 'TRK003', status: 'Delayed', supplier: 'Global Supply', destination: 'Chicago' }
          ],
          riskAlerts: [
            { id: 1, message: 'High delay risk for shipment TRK003', severity: 'high', timestamp: '2 hours ago' },
            { id: 2, message: 'Supplier compliance issue detected', severity: 'medium', timestamp: '4 hours ago' }
          ]
        };

        setDashboardData(mockData);
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
    
    // Set up real-time updates (every 30 seconds)
    const interval = setInterval(fetchDashboardData, 30000);
    return () => clearInterval(interval);
  }, []);

  const getRiskColor = (score) => {
    if (score <= 1) return 'green';
    if (score <= 2) return 'blue';
    if (score <= 3) return 'orange';
    return 'red';
  };

  const getStatusColor = (status) => {
    switch (status.toLowerCase()) {
      case 'delivered': return 'text-green-600 bg-green-100';
      case 'in transit': return 'text-blue-600 bg-blue-100';
      case 'delayed': return 'text-red-600 bg-red-100';
      default: return 'text-gray-600 bg-gray-100';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 p-6">
        <div className="animate-pulse space-y-6">
          <div className="h-8 bg-gray-200 rounded w-1/4"></div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {[...Array(4)].map((_, i) => (
              <div key={i} className="h-32 bg-gray-200 rounded-lg"></div>
            ))}
          </div>
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {[...Array(2)].map((_, i) => (
              <div key={i} className="h-96 bg-gray-200 rounded-lg"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <motion.h1 
            className="text-3xl font-bold text-gray-900 mb-2"
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            Supply Chain Dashboard
          </motion.h1>
          <p className="text-gray-600">
            Welcome back, {user?.displayName || user?.username || 'User'}! Here's what's happening with your supply chain.
          </p>
        </div>

        {/* KPI Widgets */}
        <motion.div 
          className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <KPIWidget
            title="Total Shipments"
            value={dashboardData.totalShipments}
            subtitle="All time"
            trend="up"
            trendValue="+12%"
            icon={<TruckIcon />}
            color="blue"
          />
          <KPIWidget
            title="Active Shipments"
            value={dashboardData.activeShipments}
            subtitle="Currently in transit"
            trend="up"
            trendValue="+8%"
            icon={<ChartIcon />}
            color="orange"
          />
          <KPIWidget
            title="Total Suppliers"
            value={dashboardData.totalSuppliers}
            subtitle="Registered partners"
            trend="up"
            trendValue="+3%"
            icon={<UsersIcon />}
            color="green"
          />
          <KPIWidget
            title="Risk Alerts"
            value={dashboardData.highRiskAlerts}
            subtitle="Requiring attention"
            trend="down"
            trendValue="-5%"
            icon={<WarningIcon />}
            color="red"
          />
        </motion.div>

        {/* Performance Metrics */}
        <motion.div 
          className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.4 }}
        >
          <Card variant="elevated" className="col-span-1 lg:col-span-2">
            <Card.Header>
              <Card.Title>Performance Overview</Card.Title>
              <Card.Description>Key supply chain metrics</Card.Description>
            </Card.Header>
            <Card.Content>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="text-center p-4 bg-green-50 rounded-lg">
                  <div className="text-3xl font-bold text-green-600">
                    {dashboardData.onTimeDelivery}%
                  </div>
                  <div className="text-sm text-gray-600">On-Time Delivery</div>
                </div>
                <div className="text-center p-4 bg-blue-50 rounded-lg">
                  <div className="text-3xl font-bold text-blue-600">
                    {dashboardData.avgRiskScore.toFixed(1)}
                  </div>
                  <div className="text-sm text-gray-600">Average Risk Score</div>
                </div>
              </div>
            </Card.Content>
          </Card>

          <Card variant="elevated">
            <Card.Header>
              <Card.Title>Quick Actions</Card.Title>
            </Card.Header>
            <Card.Content className="space-y-3">
              <Button variant="primary" className="w-full" size="sm">
                Add New Shipment
              </Button>
              <Button variant="outline" className="w-full" size="sm">
                Register Supplier
              </Button>
              <Button variant="secondary" className="w-full" size="sm">
                Generate Report
              </Button>
            </Card.Content>
          </Card>
        </motion.div>

        {/* Recent Activity */}
        <motion.div 
          className="grid grid-cols-1 lg:grid-cols-2 gap-6"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.6 }}
        >
          {/* Recent Shipments */}
          <Card variant="elevated">
            <Card.Header>
              <Card.Title>Recent Shipments</Card.Title>
              <Card.Description>Latest shipment updates</Card.Description>
            </Card.Header>
            <Card.Content>
              <div className="space-y-4">
                {dashboardData.recentShipments.map((shipment, index) => (
                  <motion.div 
                    key={shipment.id}
                    className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.3, delay: index * 0.1 }}
                  >
                    <div>
                      <div className="font-medium text-sm">{shipment.trackingNumber}</div>
                      <div className="text-xs text-gray-600">
                        {shipment.supplier} → {shipment.destination}
                      </div>
                    </div>
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(shipment.status)}`}>
                      {shipment.status}
                    </span>
                  </motion.div>
                ))}
              </div>
            </Card.Content>
            <Card.Footer>
              <Button variant="ghost" size="sm" className="w-full">
                View All Shipments
              </Button>
            </Card.Footer>
          </Card>

          {/* Risk Alerts */}
          <Card variant="elevated">
            <Card.Header>
              <Card.Title>Risk Alerts</Card.Title>
              <Card.Description>Active risk notifications</Card.Description>
            </Card.Header>
            <Card.Content>
              <div className="space-y-4">
                {dashboardData.riskAlerts.map((alert, index) => (
                  <motion.div 
                    key={alert.id}
                    className="flex items-start space-x-3 p-3 bg-red-50 rounded-lg border-l-4 border-red-400"
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ duration: 0.3, delay: index * 0.1 }}
                  >
                    <WarningIcon />
                    <div className="flex-1">
                      <div className="text-sm font-medium text-red-800">
                        {alert.message}
                      </div>
                      <div className="text-xs text-red-600">
                        {alert.timestamp}
                      </div>
                    </div>
                  </motion.div>
                ))}
              </div>
            </Card.Content>
            <Card.Footer>
              <Button variant="ghost" size="sm" className="w-full">
                View All Alerts
              </Button>
            </Card.Footer>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};

export default AnalyticsDashboard;