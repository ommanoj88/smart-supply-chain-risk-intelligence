import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import Card from '../ui/Card';
import Button from '../ui/Button';
import StatusBadge from '../ui/StatusBadge';

// Icons
const BellIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-5 5c-1.886-2.114-2-5-2-5zM11 14h4a2 2 0 002-2V7a5 5 0 10-10 0v5a2 2 0 002 2z" />
  </svg>
);

const CheckIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
  </svg>
);

const XIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
  </svg>
);

const WarningIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.996-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
  </svg>
);

const InfoIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
  </svg>
);

const TruckIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
  </svg>
);

const UserIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
  </svg>
);

const FilterIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.414A1 1 0 013 6.707V4z" />
  </svg>
);

const NotificationItem = ({ notification, onAcknowledge, onResolve, onDismiss }) => {
  const getIcon = (type) => {
    switch (type) {
      case 'risk': return <WarningIcon />;
      case 'shipment': return <TruckIcon />;
      case 'supplier': return <UserIcon />;
      case 'system': return <InfoIcon />;
      default: return <BellIcon />;
    }
  };

  const getSeverityColor = (severity) => {
    switch (severity.toLowerCase()) {
      case 'critical': return 'border-red-500 bg-red-50';
      case 'high': return 'border-orange-500 bg-orange-50';
      case 'medium': return 'border-yellow-500 bg-yellow-50';
      case 'low': return 'border-blue-500 bg-blue-50';
      default: return 'border-gray-300 bg-gray-50';
    }
  };

  const getIconColor = (severity) => {
    switch (severity.toLowerCase()) {
      case 'critical': return 'text-red-600';
      case 'high': return 'text-orange-600';
      case 'medium': return 'text-yellow-600';
      case 'low': return 'text-blue-600';
      default: return 'text-gray-600';
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      className={`border-l-4 p-4 ${getSeverityColor(notification.severity)} ${
        notification.isRead ? 'opacity-75' : ''
      }`}
    >
      <div className="flex items-start justify-between">
        <div className="flex items-start space-x-3">
          <div className={`p-2 rounded-lg ${getIconColor(notification.severity)}`}>
            {getIcon(notification.type)}
          </div>
          <div className="flex-1 min-w-0">
            <div className="flex items-center space-x-2">
              <h3 className="text-sm font-semibold text-gray-900">
                {notification.title}
              </h3>
              {!notification.isRead && (
                <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
              )}
            </div>
            <p className="mt-1 text-sm text-gray-700">
              {notification.message}
            </p>
            <div className="mt-2 flex items-center space-x-4 text-xs text-gray-500">
              <span>{notification.timestamp}</span>
              <StatusBadge 
                status={notification.severity} 
                color={notification.severity === 'critical' ? 'red' : 
                       notification.severity === 'high' ? 'orange' : 
                       notification.severity === 'medium' ? 'yellow' : 'blue'} 
              />
              {notification.category && (
                <span className="px-2 py-1 bg-gray-200 rounded-full">
                  {notification.category}
                </span>
              )}
            </div>
            {notification.relatedData && (
              <div className="mt-2 p-2 bg-white rounded border">
                <div className="text-xs text-gray-600">
                  {notification.relatedData.type}: {notification.relatedData.value}
                </div>
              </div>
            )}
          </div>
        </div>
        <div className="flex space-x-1 ml-4">
          {!notification.isAcknowledged && notification.severity !== 'low' && (
            <Button
              variant="ghost"
              size="sm"
              onClick={() => onAcknowledge(notification.id)}
              className="text-green-600 hover:text-green-700"
            >
              <CheckIcon />
            </Button>
          )}
          {notification.canResolve && (
            <Button
              variant="ghost"
              size="sm"
              onClick={() => onResolve(notification.id)}
              className="text-blue-600 hover:text-blue-700"
            >
              Resolve
            </Button>
          )}
          <Button
            variant="ghost"
            size="sm"
            onClick={() => onDismiss(notification.id)}
            className="text-gray-400 hover:text-gray-600"
          >
            <XIcon />
          </Button>
        </div>
      </div>
    </motion.div>
  );
};

const NotificationCenter = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all');
  const [showUnreadOnly, setShowUnreadOnly] = useState(false);

  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        setLoading(true);
        
        // Mock notifications data
        const mockNotifications = [
          {
            id: 1,
            type: 'risk',
            severity: 'critical',
            title: 'High Risk Supplier Alert',
            message: 'Supplier "Pacific Supply Solutions" risk score has increased to 3.8 due to delayed shipments and compliance issues.',
            timestamp: '2 minutes ago',
            category: 'Risk Management',
            isRead: false,
            isAcknowledged: false,
            canResolve: true,
            relatedData: {
              type: 'Supplier',
              value: 'Pacific Supply Solutions'
            }
          },
          {
            id: 2,
            type: 'shipment',
            severity: 'high',
            title: 'Shipment Delay Detected',
            message: 'Shipment TRK-2024-002 is experiencing delays at Shanghai Port. Expected delay: 3-4 days.',
            timestamp: '15 minutes ago',
            category: 'Logistics',
            isRead: false,
            isAcknowledged: false,
            canResolve: true,
            relatedData: {
              type: 'Tracking Number',
              value: 'TRK-2024-002'
            }
          },
          {
            id: 3,
            type: 'supplier',
            severity: 'medium',
            title: 'Certification Expiring Soon',
            message: 'ISO 9001 certification for "European Components Ltd" expires in 30 days. Renewal required.',
            timestamp: '1 hour ago',
            category: 'Compliance',
            isRead: true,
            isAcknowledged: false,
            canResolve: true,
            relatedData: {
              type: 'Certification',
              value: 'ISO 9001'
            }
          },
          {
            id: 4,
            type: 'system',
            severity: 'low',
            title: 'System Maintenance Scheduled',
            message: 'Scheduled maintenance window: January 28, 2024, 2:00 AM - 4:00 AM UTC. Some features may be temporarily unavailable.',
            timestamp: '2 hours ago',
            category: 'System',
            isRead: true,
            isAcknowledged: true,
            canResolve: false
          },
          {
            id: 5,
            type: 'shipment',
            severity: 'low',
            title: 'Shipment Delivered Successfully',
            message: 'Shipment TRK-2024-001 has been delivered successfully to TechCorp Inc. All package contents verified.',
            timestamp: '4 hours ago',
            category: 'Delivery',
            isRead: true,
            isAcknowledged: true,
            canResolve: false,
            relatedData: {
              type: 'Tracking Number',
              value: 'TRK-2024-001'
            }
          }
        ];
        
        setNotifications(mockNotifications);
      } catch (error) {
        console.error('Error fetching notifications:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchNotifications();
    
    // Set up real-time updates (every 30 seconds)
    const interval = setInterval(fetchNotifications, 30000);
    return () => clearInterval(interval);
  }, []);

  const handleAcknowledge = (notificationId) => {
    setNotifications(prev =>
      prev.map(notif =>
        notif.id === notificationId
          ? { ...notif, isAcknowledged: true, isRead: true }
          : notif
      )
    );
  };

  const handleResolve = (notificationId) => {
    setNotifications(prev =>
      prev.filter(notif => notif.id !== notificationId)
    );
  };

  const handleDismiss = (notificationId) => {
    setNotifications(prev =>
      prev.filter(notif => notif.id !== notificationId)
    );
  };

  const handleMarkAllRead = () => {
    setNotifications(prev =>
      prev.map(notif => ({ ...notif, isRead: true }))
    );
  };

  const filteredNotifications = notifications.filter(notif => {
    if (showUnreadOnly && notif.isRead) return false;
    if (filter === 'all') return true;
    return notif.type === filter;
  });

  const unreadCount = notifications.filter(n => !n.isRead).length;

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div className="flex items-center space-x-3">
          <h1 className="text-2xl font-bold text-gray-900">Notifications</h1>
          {unreadCount > 0 && (
            <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
              {unreadCount} unread
            </span>
          )}
        </div>
        <div className="flex space-x-3">
          <Button 
            variant="outline" 
            size="sm"
            onClick={handleMarkAllRead}
            disabled={unreadCount === 0}
          >
            Mark All Read
          </Button>
          <Button variant="primary" size="sm">
            Settings
          </Button>
        </div>
      </div>

      {/* Filters */}
      <Card variant="elevated">
        <Card.Content className="p-4">
          <div className="flex flex-wrap items-center gap-4">
            <div className="flex items-center space-x-2">
              <FilterIcon />
              <span className="text-sm font-medium text-gray-700">Filter by:</span>
            </div>
            
            <div className="flex flex-wrap gap-2">
              {[
                { value: 'all', label: 'All Notifications' },
                { value: 'risk', label: 'Risk Alerts' },
                { value: 'shipment', label: 'Shipments' },
                { value: 'supplier', label: 'Suppliers' },
                { value: 'system', label: 'System' }
              ].map(option => (
                <Button
                  key={option.value}
                  variant={filter === option.value ? 'primary' : 'outline'}
                  size="sm"
                  onClick={() => setFilter(option.value)}
                >
                  {option.label}
                </Button>
              ))}
            </div>

            <div className="flex items-center space-x-2 ml-auto">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  checked={showUnreadOnly}
                  onChange={(e) => setShowUnreadOnly(e.target.checked)}
                  className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                />
                <span className="ml-2 text-sm text-gray-700">Unread only</span>
              </label>
            </div>
          </div>
        </Card.Content>
      </Card>

      {/* Notifications List */}
      <Card variant="elevated">
        <Card.Header>
          <Card.Title>Recent Notifications</Card.Title>
          <Card.Description>
            Stay updated with the latest alerts and system notifications
          </Card.Description>
        </Card.Header>
        <Card.Content className="p-0">
          {loading ? (
            <div className="p-8 text-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600 mx-auto"></div>
              <p className="mt-2 text-gray-600">Loading notifications...</p>
            </div>
          ) : filteredNotifications.length === 0 ? (
            <div className="p-8 text-center">
              <BellIcon />
              <h3 className="mt-2 text-sm font-medium text-gray-900">No notifications</h3>
              <p className="mt-1 text-sm text-gray-500">
                {showUnreadOnly ? 'No unread notifications' : 'All caught up!'}
              </p>
            </div>
          ) : (
            <div className="divide-y divide-gray-200">
              <AnimatePresence>
                {filteredNotifications.map((notification) => (
                  <NotificationItem
                    key={notification.id}
                    notification={notification}
                    onAcknowledge={handleAcknowledge}
                    onResolve={handleResolve}
                    onDismiss={handleDismiss}
                  />
                ))}
              </AnimatePresence>
            </div>
          )}
        </Card.Content>
      </Card>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {[
          {
            label: 'Total Notifications',
            value: notifications.length,
            color: 'blue'
          },
          {
            label: 'Unread',
            value: unreadCount,
            color: 'red'
          },
          {
            label: 'Critical Alerts',
            value: notifications.filter(n => n.severity === 'critical').length,
            color: 'red'
          },
          {
            label: 'Acknowledged',
            value: notifications.filter(n => n.isAcknowledged).length,
            color: 'green'
          }
        ].map((stat, index) => (
          <Card key={index} variant="outlined">
            <Card.Content className="p-4 text-center">
              <div className={`text-2xl font-bold ${
                stat.color === 'red' ? 'text-red-600' :
                stat.color === 'green' ? 'text-green-600' :
                'text-blue-600'
              }`}>
                {stat.value}
              </div>
              <div className="text-sm text-gray-600">{stat.label}</div>
            </Card.Content>
          </Card>
        ))}
      </div>
    </div>
  );
};

export default NotificationCenter;