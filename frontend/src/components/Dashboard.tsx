import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import type { Supplier, Shipment, Alert, ShipmentStatus, AlertSeverity } from '../types';
import { supplierApi, shipmentApi } from '../api';
import './Dashboard.css';

interface DashboardStats {
  totalSuppliers: number;
  activeShipments: number;
  highRiskShipments: number;
  criticalAlerts: number;
  averageRiskScore: number;
}

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const [stats, setStats] = useState<DashboardStats>({
    totalSuppliers: 0,
    activeShipments: 0,
    highRiskShipments: 0,
    criticalAlerts: 0,
    averageRiskScore: 0,
  });
  const [recentShipments, setRecentShipments] = useState<Shipment[]>([]);
  const [highRiskSuppliers, setHighRiskSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      
      // Load suppliers data
      const suppliersResponse = await supplierApi.getActiveSuppliers();
      const suppliers = suppliersResponse.data || [];
      
      // Load shipments data
      const shipmentsResponse = await shipmentApi.getAllShipments();
      const shipments = shipmentsResponse.data || [];
      
      // Load high risk suppliers
      const highRiskResponse = await supplierApi.getHighRiskSuppliers();
      const highRiskSuppliers = highRiskResponse.data || [];
      
      // Calculate stats
      const activeShipments = shipments.filter(s => 
        s.status === ShipmentStatus.IN_TRANSIT || s.status === ShipmentStatus.PENDING
      );
      const delayedShipments = shipments.filter(s => s.status === ShipmentStatus.DELAYED);
      const averageRisk = suppliers.length > 0 
        ? suppliers.reduce((sum, s) => sum + s.riskScore, 0) / suppliers.length 
        : 0;

      setStats({
        totalSuppliers: suppliers.length,
        activeShipments: activeShipments.length,
        highRiskShipments: delayedShipments.length + highRiskSuppliers.length,
        criticalAlerts: delayedShipments.length, // Mock critical alerts
        averageRiskScore: averageRisk,
      });

      // Set recent shipments (last 5)
      setRecentShipments(shipments.slice(0, 5));
      setHighRiskSuppliers(highRiskSuppliers.slice(0, 5));
      
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const getRiskColor = (riskScore: number) => {
    if (riskScore >= 7) return '#e74c3c';
    if (riskScore >= 5) return '#f39c12';
    if (riskScore >= 3) return '#f1c40f';
    return '#27ae60';
  };

  const getStatusColor = (status: ShipmentStatus) => {
    switch (status) {
      case ShipmentStatus.DELIVERED: return '#27ae60';
      case ShipmentStatus.IN_TRANSIT: return '#3498db';
      case ShipmentStatus.DELAYED: return '#e74c3c';
      case ShipmentStatus.CANCELLED: return '#95a5a6';
      default: return '#f39c12';
    }
  };

  if (loading) {
    return (
      <div className="dashboard-loading">
        <div className="loading-spinner"></div>
        <p>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="dashboard">
      <header className="dashboard-header">
        <div className="header-content">
          <div className="header-left">
            <h1>Supply Chain Risk Intelligence</h1>
            <p>Welcome back, {user?.name}</p>
          </div>
          <div className="header-right">
            <span className="user-role">{user?.role}</span>
            <button onClick={handleLogout} className="logout-btn">
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="dashboard-main">
        {/* KPI Cards */}
        <section className="kpi-section">
          <div className="kpi-card">
            <div className="kpi-icon suppliers"></div>
            <div className="kpi-content">
              <h3>Active Suppliers</h3>
              <div className="kpi-value">{stats.totalSuppliers}</div>
            </div>
          </div>
          
          <div className="kpi-card">
            <div className="kpi-icon shipments"></div>
            <div className="kpi-content">
              <h3>Active Shipments</h3>
              <div className="kpi-value">{stats.activeShipments}</div>
            </div>
          </div>
          
          <div className="kpi-card">
            <div className="kpi-icon risk"></div>
            <div className="kpi-content">
              <h3>High Risk Items</h3>
              <div className="kpi-value">{stats.highRiskShipments}</div>
            </div>
          </div>
          
          <div className="kpi-card">
            <div className="kpi-icon alerts"></div>
            <div className="kpi-content">
              <h3>Critical Alerts</h3>
              <div className="kpi-value">{stats.criticalAlerts}</div>
            </div>
          </div>
          
          <div className="kpi-card">
            <div className="kpi-icon average"></div>
            <div className="kpi-content">
              <h3>Avg Risk Score</h3>
              <div className="kpi-value">{stats.averageRiskScore.toFixed(1)}</div>
            </div>
          </div>
        </section>

        {/* Content Grid */}
        <section className="dashboard-grid">
          {/* Recent Shipments */}
          <div className="dashboard-card">
            <div className="card-header">
              <h3>Recent Shipments</h3>
            </div>
            <div className="card-content">
              {recentShipments.length > 0 ? (
                <div className="shipments-list">
                  {recentShipments.map((shipment) => (
                    <div key={shipment.id} className="shipment-item">
                      <div className="shipment-info">
                        <div className="shipment-route">
                          {shipment.origin} → {shipment.destination}
                        </div>
                        <div className="shipment-supplier">{shipment.supplierName}</div>
                      </div>
                      <div className="shipment-status">
                        <span 
                          className="status-badge"
                          style={{ backgroundColor: getStatusColor(shipment.status) }}
                        >
                          {shipment.status}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="no-data">No recent shipments</p>
              )}
            </div>
          </div>

          {/* High Risk Suppliers */}
          <div className="dashboard-card">
            <div className="card-header">
              <h3>High Risk Suppliers</h3>
            </div>
            <div className="card-content">
              {highRiskSuppliers.length > 0 ? (
                <div className="suppliers-list">
                  {highRiskSuppliers.map((supplier) => (
                    <div key={supplier.id} className="supplier-item">
                      <div className="supplier-info">
                        <div className="supplier-name">{supplier.name}</div>
                        <div className="supplier-location">{supplier.location}</div>
                      </div>
                      <div className="supplier-risk">
                        <div 
                          className="risk-score"
                          style={{ color: getRiskColor(supplier.riskScore) }}
                        >
                          {supplier.riskScore.toFixed(1)}
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="no-data">No high risk suppliers</p>
              )}
            </div>
          </div>
        </section>
      </main>
    </div>
  );
};

export default Dashboard;