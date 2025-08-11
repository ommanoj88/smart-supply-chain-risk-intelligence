import React, { createContext, useContext, useState, useEffect } from 'react';
import { 
  signInWithPopup, 
  signOut, 
  onAuthStateChanged 
} from 'firebase/auth';
import { auth, googleProvider } from '../services/firebase';
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
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
      setLoading(true);
      setError(null);

      if (firebaseUser) {
        try {
          // Get the ID token
          const idToken = await firebaseUser.getIdToken();
          
          // Store token in localStorage
          localStorage.setItem('authToken', idToken);
          
          // Verify token with backend and get/create user
          const response = await authAPI.verifyToken(idToken);
          
          if (response.data.success) {
            setUser(response.data.user);
            localStorage.setItem('user', JSON.stringify(response.data.user));
          } else {
            throw new Error('Token verification failed');
          }
        } catch (error) {
          console.error('Authentication error:', error);
          setError('Authentication failed. Please try again.');
          setUser(null);
          localStorage.removeItem('authToken');
          localStorage.removeItem('user');
        }
      } else {
        setUser(null);
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
      }
      
      setLoading(false);
    });

    // Check for stored user data on initial load
    const storedUser = localStorage.getItem('user');
    const storedToken = localStorage.getItem('authToken');
    
    if (storedUser && storedToken && !user) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (error) {
        console.error('Error parsing stored user data:', error);
        localStorage.removeItem('user');
        localStorage.removeItem('authToken');
      }
    }

    return () => unsubscribe();
  }, [user]);

  const signInWithGoogle = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const result = await signInWithPopup(auth, googleProvider);
      // The onAuthStateChanged listener will handle the rest
      return result;
    } catch (error) {
      console.error('Google sign-in error:', error);
      setError(error.message);
      setLoading(false);
      throw error;
    }
  };

  const logout = async () => {
    setLoading(true);
    setError(null);
    
    try {
      await signOut(auth);
      setUser(null);
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
    } catch (error) {
      console.error('Logout error:', error);
      setError(error.message);
    }
    
    setLoading(false);
  };

  const refreshUser = async () => {
    if (auth.currentUser) {
      try {
        const response = await authAPI.getCurrentUser();
        setUser(response.data);
        localStorage.setItem('user', JSON.stringify(response.data));
      } catch (error) {
        console.error('Error refreshing user data:', error);
        setError('Failed to refresh user data');
      }
    }
  };

  const value = {
    user,
    loading,
    error,
    signInWithGoogle,
    logout,
    refreshUser,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN',
    isSupplyManager: user?.role === 'SUPPLY_MANAGER' || user?.role === 'ADMIN',
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};