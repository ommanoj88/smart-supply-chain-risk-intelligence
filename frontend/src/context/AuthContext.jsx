import React, { createContext, useContext, useEffect, useState } from 'react';
import { auth, signInWithGoogle, logOut } from '../services/firebase';
import { onAuthStateChanged } from 'firebase/auth';
import { authAPI } from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [userProfile, setUserProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
      setLoading(true);
      setError(null);
      
      if (firebaseUser) {
        try {
          // User is signed in
          setUser(firebaseUser);
          
          // Login to backend and get user profile
          await authAPI.login();
          const profileResponse = await authAPI.getProfile();
          setUserProfile(profileResponse.data);
        } catch (err) {
          console.error('Error syncing user with backend:', err);
          setError('Failed to sync user profile');
        }
      } else {
        // User is signed out
        setUser(null);
        setUserProfile(null);
      }
      
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  const login = async () => {
    try {
      setError(null);
      const result = await signInWithGoogle();
      return result;
    } catch (err) {
      console.error('Login error:', err);
      setError(err.message);
      throw err;
    }
  };

  const logout = async () => {
    try {
      setError(null);
      await logOut();
      setUser(null);
      setUserProfile(null);
    } catch (err) {
      console.error('Logout error:', err);
      setError(err.message);
      throw err;
    }
  };

  const refreshProfile = async () => {
    if (!user) return;
    
    try {
      const profileResponse = await authAPI.getProfile();
      setUserProfile(profileResponse.data);
    } catch (err) {
      console.error('Error refreshing profile:', err);
      setError('Failed to refresh profile');
    }
  };

  const value = {
    user,
    userProfile,
    loading,
    error,
    login,
    logout,
    refreshProfile,
    isAuthenticated: !!user,
    isAdmin: userProfile?.role === 'ADMIN',
    isSupplyManager: userProfile?.role === 'SUPPLY_MANAGER',
    isViewer: userProfile?.role === 'VIEWER',
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};