import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

// Performance monitoring
if ('performance' in window) {
  window.performance.mark('app-render-start');
}

const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement);

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// Performance monitoring
if ('performance' in window) {
  window.performance.mark('app-render-end');
  window.performance.measure('app-render', 'app-render-start', 'app-render-end');
}