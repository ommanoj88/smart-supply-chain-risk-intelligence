// Performance monitoring service for React application
class PerformanceMonitor {
  private metrics: Map<string, number> = new Map();
  private navigationStartTime: number;

  constructor() {
    this.navigationStartTime = performance.timeOrigin || performance.timing?.navigationStart || Date.now();
    this.setupPerformanceObserver();
  }

  // Mark a performance point
  mark(name: string): void {
    try {
      performance.mark(name);
      this.metrics.set(name, performance.now());
    } catch (error) {
      console.warn('Performance mark failed:', error);
    }
  }

  // Measure time between two points
  measure(name: string, startMark: string, endMark?: string): number {
    try {
      performance.measure(name, startMark, endMark);
      const measures = performance.getEntriesByName(name, 'measure');
      const latestMeasure = measures[measures.length - 1];
      return latestMeasure?.duration || 0;
    } catch (error) {
      console.warn('Performance measure failed:', error);
      return 0;
    }
  }

  // Get Core Web Vitals
  getCoreWebVitals(): Promise<{
    fcp?: number;
    lcp?: number;
    fid?: number;
    cls?: number;
  }> {
    return new Promise((resolve) => {
      const vitals: any = {};

      // First Contentful Paint
      try {
        const fcpEntry = performance.getEntriesByName('first-contentful-paint')[0];
        if (fcpEntry) {
          vitals.fcp = fcpEntry.startTime;
        }
      } catch (error) {
        console.warn('FCP measurement failed:', error);
      }

      // Largest Contentful Paint
      if ('PerformanceObserver' in window) {
        try {
          const lcpObserver = new PerformanceObserver((list) => {
            const entries = list.getEntries();
            const lastEntry = entries[entries.length - 1];
            vitals.lcp = lastEntry.startTime;
          });
          lcpObserver.observe({ entryTypes: ['largest-contentful-paint'] });
        } catch (error) {
          console.warn('LCP observer failed:', error);
        }
      }

      // Cumulative Layout Shift
      if ('PerformanceObserver' in window) {
        try {
          let clsValue = 0;
          const clsObserver = new PerformanceObserver((list) => {
            for (const entry of list.getEntries()) {
              if (!(entry as any).hadRecentInput) {
                clsValue += (entry as any).value;
              }
            }
            vitals.cls = clsValue;
          });
          clsObserver.observe({ entryTypes: ['layout-shift'] });
        } catch (error) {
          console.warn('CLS observer failed:', error);
        }
      }

      // Return metrics after a delay to collect data
      setTimeout(() => resolve(vitals), 2000);
    });
  }

  // Monitor bundle loading performance
  measureBundleLoad(bundleName: string, loadStart: number): void {
    const loadTime = performance.now() - loadStart;
    console.log(`Bundle ${bundleName} loaded in ${loadTime.toFixed(2)}ms`);
    
    // Track slow bundles
    if (loadTime > 1000) {
      console.warn(`Slow bundle load detected: ${bundleName} took ${loadTime.toFixed(2)}ms`);
    }
  }

  // Monitor API call performance
  measureApiCall(endpoint: string, startTime: number, endTime: number, status: number): void {
    const duration = endTime - startTime;
    
    console.log(`API ${endpoint}: ${duration.toFixed(2)}ms (${status})`);
    
    // Track slow API calls
    if (duration > 1000) {
      console.warn(`Slow API call: ${endpoint} took ${duration.toFixed(2)}ms`);
    }
    
    // Track failed API calls
    if (status >= 400) {
      console.error(`API error: ${endpoint} returned ${status}`);
    }
  }

  // Monitor memory usage
  getMemoryUsage(): {
    used: number;
    total: number;
    percentage: number;
  } | null {
    if ('memory' in performance) {
      const memory = (performance as any).memory;
      return {
        used: Math.round(memory.usedJSHeapSize / 1024 / 1024),
        total: Math.round(memory.totalJSHeapSize / 1024 / 1024),
        percentage: Math.round((memory.usedJSHeapSize / memory.totalJSHeapSize) * 100)
      };
    }
    return null;
  }

  // Monitor React render performance
  measureRender(componentName: string, renderTime: number): void {
    if (renderTime > 16) { // Slower than 60fps
      console.warn(`Slow render detected: ${componentName} took ${renderTime.toFixed(2)}ms`);
    }
  }

  // Setup performance observer for resource loading
  private setupPerformanceObserver(): void {
    if ('PerformanceObserver' in window) {
      try {
        const observer = new PerformanceObserver((list) => {
          for (const entry of list.getEntries()) {
            if (entry.entryType === 'navigation') {
              const nav = entry as PerformanceNavigationTiming;
              console.log('Navigation timing:', {
                domContentLoaded: nav.domContentLoadedEventEnd - nav.domContentLoadedEventStart,
                loadComplete: nav.loadEventEnd - nav.loadEventStart,
                firstByte: nav.responseStart - nav.requestStart
              });
            }
            
            if (entry.entryType === 'resource') {
              const resource = entry as PerformanceResourceTiming;
              if (resource.duration > 1000) {
                console.warn(`Slow resource: ${resource.name} took ${resource.duration.toFixed(2)}ms`);
              }
            }
          }
        });
        
        observer.observe({ entryTypes: ['navigation', 'resource'] });
      } catch (error) {
        console.warn('Performance observer setup failed:', error);
      }
    }
  }

  // Get performance report
  getPerformanceReport(): {
    coreWebVitals: Promise<any>;
    memoryUsage: any;
    markTimings: Record<string, number>;
  } {
    return {
      coreWebVitals: this.getCoreWebVitals(),
      memoryUsage: this.getMemoryUsage(),
      markTimings: Object.fromEntries(this.metrics)
    };
  }
}

// Create singleton instance
export const performanceMonitor = new PerformanceMonitor();

// React hook for performance monitoring
export const usePerformanceMonitor = () => {
  const [renderStart] = React.useState(() => performance.now());
  
  React.useEffect(() => {
    const renderEnd = performance.now();
    const renderTime = renderEnd - renderStart;
    performanceMonitor.measureRender('Component', renderTime);
  });

  return {
    mark: performanceMonitor.mark.bind(performanceMonitor),
    measure: performanceMonitor.measure.bind(performanceMonitor),
    getCoreWebVitals: performanceMonitor.getCoreWebVitals.bind(performanceMonitor),
    getMemoryUsage: performanceMonitor.getMemoryUsage.bind(performanceMonitor)
  };
};

export default performanceMonitor;