# Webpack Performance Optimization Guide

This document outlines the webpack optimizations that should be implemented for the React frontend to achieve better performance and smaller bundle sizes.

## Bundle Analysis

First, add bundle analyzer to analyze current bundle size:

```bash
npm install --save-dev webpack-bundle-analyzer
```

Add to package.json:
```json
{
  "scripts": {
    "analyze": "npm run build && npx webpack-bundle-analyzer build/static/js/*.js"
  }
}
```

## Webpack Optimization Configuration

Since this is a Create React App project, we need to use CRACO (Create React App Configuration Override) to customize webpack without ejecting:

```bash
npm install --save-dev @craco/craco craco-webpack-bundle-analyzer
```

Create `craco.config.js`:

```javascript
const { whenProd, whenDev } = require('@craco/craco');
const WebpackBundleAnalyzer = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

module.exports = {
  webpack: {
    optimization: {
      splitChunks: {
        chunks: 'all',
        cacheGroups: {
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: 'vendors',
            priority: 10,
            enforce: true,
          },
          common: {
            name: 'common',
            minChunks: 2,
            priority: 5,
            reuseExistingChunk: true,
          },
          // Separate chunk for large libraries
          mui: {
            test: /[\\/]node_modules[\\/]@mui[\\/]/,
            name: 'mui',
            priority: 20,
          },
          charts: {
            test: /[\\/]node_modules[\\/](recharts|d3)[\\/]/,
            name: 'charts',
            priority: 15,
          },
          utils: {
            test: /[\\/]node_modules[\\/](lodash|moment|date-fns)[\\/]/,
            name: 'utils',
            priority: 15,
          }
        }
      },
      usedExports: true,
      sideEffects: false,
    },
    plugins: [
      ...whenProd(() => [
        new WebpackBundleAnalyzer({
          analyzerMode: 'static',
          openAnalyzer: false,
        }),
      ], []),
    ],
    configure: (webpackConfig) => {
      // Production optimizations
      if (process.env.NODE_ENV === 'production') {
        // Tree shaking optimization
        webpackConfig.optimization.usedExports = true;
        webpackConfig.optimization.sideEffects = false;
        
        // Compression
        webpackConfig.optimization.minimize = true;
        
        // Module concatenation
        webpackConfig.optimization.concatenateModules = true;
        
        // Remove console logs in production
        if (webpackConfig.optimization.minimizer) {
          const terserPlugin = webpackConfig.optimization.minimizer.find(
            plugin => plugin.constructor.name === 'TerserPlugin'
          );
          if (terserPlugin) {
            terserPlugin.options.terserOptions.compress.drop_console = true;
            terserPlugin.options.terserOptions.compress.drop_debugger = true;
          }
        }
      }

      // Resolve optimization
      webpackConfig.resolve.alias = {
        ...webpackConfig.resolve.alias,
        // Use production builds
        'react': 'react/index.js',
        'react-dom': 'react-dom/index.js',
      };

      return webpackConfig;
    },
  },
  babel: {
    plugins: [
      ...whenProd(() => [
        // Remove PropTypes in production
        ['babel-plugin-transform-react-remove-prop-types', { removeImport: true }],
        // Remove dev tools
        ['babel-plugin-transform-remove-console', { exclude: ['error', 'warn'] }],
      ], []),
      ...whenDev(() => [
        // Development optimizations
        'react-refresh/babel',
      ], []),
    ],
    presets: [
      [
        '@babel/preset-env',
        {
          modules: false,
          useBuiltIns: 'usage',
          corejs: 3,
          targets: {
            browsers: [
              '>0.2%',
              'not dead',
              'not ie <= 11',
              'not op_mini all'
            ]
          }
        }
      ],
      '@babel/preset-react',
      '@babel/preset-typescript'
    ],
  },
};
```

## Code Splitting Strategy

### 1. Route-based Code Splitting

```typescript
// src/routes/LazyRoutes.tsx
import React, { Suspense } from 'react';
import { createLazyComponent } from '../components/LazyLoader';
import LoadingSpinner from '../components/LoadingSpinner';

// Lazy load route components
export const Dashboard = createLazyComponent(
  () => import('../pages/Dashboard'),
  'Dashboard',
  <LoadingSpinner />
);

export const Suppliers = createLazyComponent(
  () => import('../pages/Suppliers'),
  'Suppliers',
  <LoadingSpinner />
);

export const Shipments = createLazyComponent(
  () => import('../pages/Shipments'),
  'Shipments',
  <LoadingSpinner />
);

export const Analytics = createLazyComponent(
  () => import('../pages/Analytics'),
  'Analytics',
  <LoadingSpinner />
);
```

### 2. Component-based Code Splitting

```typescript
// Split large components
export const SupplierDataGrid = createLazyComponent(
  () => import('../components/suppliers/SupplierDataGrid'),
  'SupplierDataGrid'
);

export const ShipmentMap = createLazyComponent(
  () => import('../components/shipments/ShipmentMap'),
  'ShipmentMap'
);

export const AnalyticsCharts = createLazyComponent(
  () => import('../components/analytics/AnalyticsCharts'),
  'AnalyticsCharts'
);
```

## Library Optimizations

### 1. Tree Shaking for Lodash

```typescript
// Instead of
import _ from 'lodash';

// Use specific imports
import { debounce, throttle } from 'lodash';

// Or better yet, use alternatives
import debounce from 'lodash/debounce';
```

### 2. MUI Optimization

```typescript
// Use specific imports
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';

// Instead of
import { Button, TextField } from '@mui/material';
```

### 3. Chart Library Optimization

```typescript
// For recharts, import only what you need
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

// For D3, use specific modules
import { select } from 'd3-selection';
import { scaleLinear } from 'd3-scale';
```

## Service Worker and Caching

Create `public/sw.js`:

```javascript
const CACHE_NAME = 'supply-chain-v1';
const urlsToCache = [
  '/',
  '/static/js/bundle.js',
  '/static/css/main.css',
  '/manifest.json'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => cache.addAll(urlsToCache))
  );
});

self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request)
      .then((response) => {
        // Return cached version or fetch from network
        return response || fetch(event.request);
      })
  );
});
```

Register in `src/index.tsx`:

```typescript
// Register service worker
if ('serviceWorker' in navigator && process.env.NODE_ENV === 'production') {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js')
      .then((registration) => {
        console.log('SW registered: ', registration);
      })
      .catch((registrationError) => {
        console.log('SW registration failed: ', registrationError);
      });
  });
}
```

## Image Optimization

### 1. Responsive Images

```typescript
// Use picture element with multiple formats
<picture>
  <source srcSet="image.webp" type="image/webp" />
  <source srcSet="image.avif" type="image/avif" />
  <img src="image.jpg" alt="Description" loading="lazy" />
</picture>
```

### 2. Image Compression

Add to build process:

```bash
npm install --save-dev imagemin imagemin-webp imagemin-avif
```

Create `scripts/optimize-images.js`:

```javascript
const imagemin = require('imagemin');
const imageminWebp = require('imagemin-webp');
const imageminAvif = require('imagemin-avif');

(async () => {
  await imagemin(['src/assets/images/*.{jpg,png}'], {
    destination: 'build/static/media',
    plugins: [
      imageminWebp({ quality: 80 }),
      imageminAvif({ quality: 80 })
    ]
  });
})();
```

## Performance Monitoring

### 1. Web Vitals

```typescript
// src/utils/webVitals.ts
import { getCLS, getFID, getFCP, getLCP, getTTFB } from 'web-vitals';

const vitalsUrl = process.env.REACT_APP_VITALS_URL;

function sendToAnalytics(metric: any) {
  if (vitalsUrl) {
    navigator.sendBeacon(vitalsUrl, JSON.stringify(metric));
  }
  console.log(metric);
}

export function reportWebVitals() {
  getCLS(sendToAnalytics);
  getFID(sendToAnalytics);
  getFCP(sendToAnalytics);
  getLCP(sendToAnalytics);
  getTTFB(sendToAnalytics);
}
```

### 2. Performance Observer

```typescript
// Monitor long tasks
if ('PerformanceObserver' in window) {
  const observer = new PerformanceObserver((list) => {
    for (const entry of list.getEntries()) {
      if (entry.duration > 50) {
        console.warn('Long task detected:', entry);
      }
    }
  });
  observer.observe({ entryTypes: ['longtask'] });
}
```

## Build Optimizations

### 1. Environment Variables

```bash
# .env.production
GENERATE_SOURCEMAP=false
INLINE_RUNTIME_CHUNK=false
IMAGE_INLINE_SIZE_LIMIT=0
```

### 2. Package.json Scripts

```json
{
  "scripts": {
    "build": "craco build",
    "build:analyze": "npm run build && npx webpack-bundle-analyzer build/static/js/*.js",
    "build:stats": "npm run build -- --stats",
    "precommit": "npm run build && npm run test",
    "optimize-images": "node scripts/optimize-images.js"
  }
}
```

### 3. Performance Budget

Add to package.json:

```json
{
  "bundlewatch": {
    "files": [
      {
        "path": "build/static/js/*.js",
        "maxSize": "250kb"
      },
      {
        "path": "build/static/css/*.css",
        "maxSize": "50kb"
      }
    ]
  }
}
```

## Implementation Checklist

- [ ] Install CRACO and configure webpack optimizations
- [ ] Implement route-based code splitting
- [ ] Optimize library imports (tree shaking)
- [ ] Add service worker for caching
- [ ] Implement image optimization
- [ ] Set up performance monitoring
- [ ] Configure performance budgets
- [ ] Add bundle analysis to CI/CD

## Expected Results

After implementing these optimizations:

- Bundle size reduction: 40-60%
- First Contentful Paint: < 1.5s
- Largest Contentful Paint: < 2.5s
- Cumulative Layout Shift: < 0.1
- First Input Delay: < 100ms
- Time to Interactive: < 3.5s

## Monitoring

Set up monitoring to track:
- Bundle sizes over time
- Core Web Vitals metrics
- Error rates and performance regressions
- User experience metrics

Use tools like:
- Lighthouse CI
- WebPageTest
- Google Analytics
- Custom performance tracking