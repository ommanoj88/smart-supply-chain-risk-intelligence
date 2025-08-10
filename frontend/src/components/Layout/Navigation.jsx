import React from 'react';
import { useAuth } from '../../context/AuthContext';

const Navigation = () => {
  const { isAdmin, isSupplyManager } = useAuth();

  return (
    <nav className="navigation">
      <div className="nav-section">
        <h3>Dashboard</h3>
        <ul>
          <li><a href="#overview" className="nav-link active">Overview</a></li>
          <li><a href="#profile" className="nav-link">Profile</a></li>
        </ul>
      </div>
      
      {(isAdmin || isSupplyManager) && (
        <div className="nav-section">
          <h3>Supply Chain</h3>
          <ul>
            <li><a href="#suppliers" className="nav-link">Suppliers</a></li>
            <li><a href="#risk-assessment" className="nav-link">Risk Assessment</a></li>
            <li><a href="#analytics" className="nav-link">Analytics</a></li>
          </ul>
        </div>
      )}
      
      {isAdmin && (
        <div className="nav-section">
          <h3>Administration</h3>
          <ul>
            <li><a href="#users" className="nav-link">User Management</a></li>
            <li><a href="#settings" className="nav-link">Settings</a></li>
            <li><a href="#reports" className="nav-link">Reports</a></li>
          </ul>
        </div>
      )}
      
      <div className="nav-section">
        <h3>Help</h3>
        <ul>
          <li><a href="#documentation" className="nav-link">Documentation</a></li>
          <li><a href="#support" className="nav-link">Support</a></li>
        </ul>
      </div>
    </nav>
  );
};

export default Navigation;