import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import Header from '../Layout/Header';
import Navigation from '../Layout/Navigation';
import UserProfile from './UserProfile';

const Dashboard = () => {
  const { userProfile, isAdmin, isSupplyManager, isViewer } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');

  const renderContent = () => {
    switch (activeTab) {
      case 'profile':
        return <UserProfile />;
      case 'overview':
      default:
        return (
          <div className="dashboard-overview">
            <div className="welcome-section">
              <h2>Welcome back, {userProfile?.displayName}!</h2>
              <p>You are logged in as <strong>{userProfile?.role?.replace('_', ' ')}</strong></p>
            </div>
            
            <div className="dashboard-cards">
              <div className="dashboard-card">
                <div className="card-header">
                  <h3>System Status</h3>
                  <div className="status-indicator online"></div>
                </div>
                <div className="card-content">
                  <p>All systems operational</p>
                  <small>Last updated: {new Date().toLocaleString()}</small>
                </div>
              </div>
              
              {(isAdmin || isSupplyManager) && (
                <>
                  <div className="dashboard-card">
                    <div className="card-header">
                      <h3>Supply Chain Risk</h3>
                      <div className="status-indicator warning"></div>
                    </div>
                    <div className="card-content">
                      <p>3 suppliers require attention</p>
                      <small>Medium risk level detected</small>
                    </div>
                  </div>
                  
                  <div className="dashboard-card">
                    <div className="card-header">
                      <h3>Active Suppliers</h3>
                    </div>
                    <div className="card-content">
                      <div className="metric">
                        <span className="metric-value">127</span>
                        <span className="metric-label">Total Suppliers</span>
                      </div>
                    </div>
                  </div>
                </>
              )}
              
              {isAdmin && (
                <div className="dashboard-card">
                  <div className="card-header">
                    <h3>User Management</h3>
                  </div>
                  <div className="card-content">
                    <div className="metric">
                      <span className="metric-value">24</span>
                      <span className="metric-label">Active Users</span>
                    </div>
                  </div>
                </div>
              )}
              
              {isViewer && (
                <div className="dashboard-card">
                  <div className="card-header">
                    <h3>Your Access Level</h3>
                  </div>
                  <div className="card-content">
                    <p>You have viewer access to the platform</p>
                    <small>Contact admin for additional permissions</small>
                  </div>
                </div>
              )}
            </div>
            
            <div className="recent-activity">
              <h3>Recent Activity</h3>
              <div className="activity-list">
                <div className="activity-item">
                  <div className="activity-icon">📊</div>
                  <div className="activity-content">
                    <p>System health check completed</p>
                    <small>2 minutes ago</small>
                  </div>
                </div>
                {(isAdmin || isSupplyManager) && (
                  <>
                    <div className="activity-item">
                      <div className="activity-icon">⚠️</div>
                      <div className="activity-content">
                        <p>Risk assessment updated for Supplier ABC Corp</p>
                        <small>1 hour ago</small>
                      </div>
                    </div>
                    <div className="activity-item">
                      <div className="activity-icon">✅</div>
                      <div className="activity-content">
                        <p>New supplier onboarding completed</p>
                        <small>3 hours ago</small>
                      </div>
                    </div>
                  </>
                )}
              </div>
            </div>
          </div>
        );
    }
  };

  return (
    <div className="dashboard">
      <Header />
      
      <div className="dashboard-body">
        <aside className="sidebar">
          <Navigation />
        </aside>
        
        <main className="main-content">
          <div className="content-tabs">
            <button 
              className={`tab ${activeTab === 'overview' ? 'active' : ''}`}
              onClick={() => setActiveTab('overview')}
            >
              Overview
            </button>
            <button 
              className={`tab ${activeTab === 'profile' ? 'active' : ''}`}
              onClick={() => setActiveTab('profile')}
            >
              Profile
            </button>
          </div>
          
          <div className="content-area">
            {renderContent()}
          </div>
        </main>
      </div>
    </div>
  );
};

export default Dashboard;