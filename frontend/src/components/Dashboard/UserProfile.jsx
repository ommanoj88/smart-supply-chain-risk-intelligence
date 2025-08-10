import React from 'react';
import { useAuth } from '../../context/AuthContext';

const UserProfile = () => {
  const { userProfile, user } = useAuth();

  if (!userProfile) {
    return (
      <div className="user-profile">
        <div className="loading">Loading profile...</div>
      </div>
    );
  }

  return (
    <div className="user-profile">
      <div className="profile-header">
        <div className="profile-avatar">
          {userProfile.photoURL ? (
            <img src={userProfile.photoURL} alt="Profile" />
          ) : (
            <div className="avatar-placeholder">
              {userProfile.displayName?.charAt(0)?.toUpperCase() || 'U'}
            </div>
          )}
        </div>
        <div className="profile-info">
          <h2>{userProfile.displayName}</h2>
          <p className="profile-email">{userProfile.email}</p>
          <span className="profile-role">{userProfile.role?.replace('_', ' ')}</span>
        </div>
      </div>
      
      <div className="profile-details">
        <div className="detail-section">
          <h3>Account Information</h3>
          <div className="detail-grid">
            <div className="detail-item">
              <label>User ID</label>
              <span>{userProfile.uid}</span>
            </div>
            <div className="detail-item">
              <label>Email</label>
              <span>{userProfile.email}</span>
            </div>
            <div className="detail-item">
              <label>Role</label>
              <span className="role-badge">{userProfile.role?.replace('_', ' ')}</span>
            </div>
            <div className="detail-item">
              <label>Member Since</label>
              <span>{new Date(userProfile.createdAt).toLocaleDateString()}</span>
            </div>
            <div className="detail-item">
              <label>Last Updated</label>
              <span>{new Date(userProfile.updatedAt).toLocaleDateString()}</span>
            </div>
            <div className="detail-item">
              <label>Email Verified</label>
              <span className={user?.emailVerified ? 'verified' : 'unverified'}>
                {user?.emailVerified ? 'Yes' : 'No'}
              </span>
            </div>
          </div>
        </div>
        
        <div className="detail-section">
          <h3>Role Permissions</h3>
          <div className="permissions">
            {userProfile.role === 'ADMIN' && (
              <ul>
                <li>Full system access</li>
                <li>User management</li>
                <li>System configuration</li>
                <li>All supply chain features</li>
              </ul>
            )}
            {userProfile.role === 'SUPPLY_MANAGER' && (
              <ul>
                <li>Supply chain management</li>
                <li>Risk assessment tools</li>
                <li>Analytics and reporting</li>
                <li>Supplier management</li>
              </ul>
            )}
            {userProfile.role === 'VIEWER' && (
              <ul>
                <li>View-only access</li>
                <li>Basic dashboard</li>
                <li>Read reports</li>
                <li>Limited analytics</li>
              </ul>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserProfile;