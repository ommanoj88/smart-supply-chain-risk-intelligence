import React from 'react';
import { useAuth } from '../../context/AuthContext';

const Header = () => {
  const { userProfile, logout } = useAuth();

  const handleLogout = async () => {
    try {
      await logout();
    } catch (err) {
      console.error('Logout failed:', err);
    }
  };

  return (
    <header className="header">
      <div className="header-content">
        <div className="header-left">
          <h1 className="header-title">Smart Supply Chain</h1>
          <span className="header-subtitle">Risk Intelligence Platform</span>
        </div>
        
        <div className="header-right">
          {userProfile && (
            <div className="user-info">
              <div className="user-details">
                <span className="user-name">{userProfile.displayName}</span>
                <span className="user-role">{userProfile.role?.replace('_', ' ')}</span>
              </div>
              
              {userProfile.photoURL && (
                <img 
                  src={userProfile.photoURL} 
                  alt="Profile" 
                  className="user-avatar"
                />
              )}
              
              <button 
                onClick={handleLogout}
                className="logout-btn"
                title="Logout"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.59L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
                </svg>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default Header;