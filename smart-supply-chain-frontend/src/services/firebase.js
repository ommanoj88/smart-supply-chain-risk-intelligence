// Import the functions you need from the SDKs you need
import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider } from 'firebase/auth';
import { getAnalytics } from 'firebase/analytics';

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyCZAd5WmJE7SrUYj4yNZDG5sltaEnrUQgk",
  authDomain: "rentvat.firebaseapp.com",
  projectId: "rentvat",
  storageBucket: "rentvat.firebasestorage.app",
  messagingSenderId: "59823572862",
  appId: "1:59823572862:web:0e8e7f53f82ac3b2a7152f",
  measurementId: "G-6QHKKBTFYN"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firebase Analytics
const analytics = getAnalytics(app);

// Initialize Firebase Authentication and get a reference to the service
export const auth = getAuth(app);
export const googleProvider = new GoogleAuthProvider();

export { analytics };
export default app;