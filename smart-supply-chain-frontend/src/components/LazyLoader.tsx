import React, { Suspense, lazy } from 'react';
import { performanceMonitor } from '../services/performanceMonitor';

interface LazyComponentWrapperProps {
  children: React.ReactNode;
  fallback?: React.ReactNode;
  name?: string;
}

// Generic loading spinner component
const DefaultLoadingSpinner: React.FC = () => (
  <div className="lazy-loading-spinner">
    <div className="spinner"></div>
    <p>Loading...</p>
  </div>
);

// Enhanced lazy loading wrapper with performance monitoring
export const LazyComponentWrapper: React.FC<LazyComponentWrapperProps> = ({
  children,
  fallback = <DefaultLoadingSpinner />,
  name = 'LazyComponent'
}) => {
  React.useEffect(() => {
    performanceMonitor.mark(`${name}-load-start`);
    
    return () => {
      performanceMonitor.mark(`${name}-load-end`);
      performanceMonitor.measure(`${name}-load-time`, `${name}-load-start`, `${name}-load-end`);
    };
  }, [name]);

  return (
    <Suspense fallback={fallback}>
      {children}
    </Suspense>
  );
};

// Hook for creating lazy components with performance monitoring
export const useLazyComponent = <T extends React.ComponentType<any>>(
  importFn: () => Promise<{ default: T }>,
  componentName: string
) => {
  return React.useMemo(() => {
    const loadStart = performance.now();
    
    return lazy(() => {
      return importFn().then(module => {
        const loadEnd = performance.now();
        performanceMonitor.measureBundleLoad(componentName, loadEnd - loadStart);
        return module;
      });
    });
  }, [importFn, componentName]);
};

// Lazy component factory with error boundary
export const createLazyComponent = <T extends React.ComponentType<any>>(
  importFn: () => Promise<{ default: T }>,
  componentName: string,
  fallback?: React.ReactNode
) => {
  const LazyComponent = useLazyComponent(importFn, componentName);
  
  return React.forwardRef<any, React.ComponentProps<T>>((props, ref) => (
    <LazyComponentWrapper name={componentName} fallback={fallback}>
      <LazyComponent {...(props as any)} ref={ref} />
    </LazyComponentWrapper>
  ));
};

// Preload utility for critical components
export const preloadComponent = <T extends React.ComponentType<any>>(
  importFn: () => Promise<{ default: T }>
): void => {
  // Preload during idle time
  if ('requestIdleCallback' in window) {
    requestIdleCallback(() => {
      importFn().catch(console.error);
    });
  } else {
    // Fallback for browsers without requestIdleCallback
    setTimeout(() => {
      importFn().catch(console.error);
    }, 100);
  }
};

// Intersection Observer based lazy loading for components
export const useInViewLazy = <T extends React.ComponentType<any>>(
  importFn: () => Promise<{ default: T }>,
  componentName: string,
  options: IntersectionObserverInit = {}
) => {
  const [isVisible, setIsVisible] = React.useState(false);
  const [LazyComponent, setLazyComponent] = React.useState<React.ComponentType<any> | null>(null);
  const ref = React.useRef<HTMLDivElement>(null);

  React.useEffect(() => {
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting && !LazyComponent) {
          setIsVisible(true);
          performanceMonitor.mark(`${componentName}-in-view`);
          
          // Load component when it comes into view
          importFn()
            .then(module => {
              setLazyComponent(() => module.default);
              performanceMonitor.mark(`${componentName}-loaded`);
              performanceMonitor.measure(
                `${componentName}-load-on-view`,
                `${componentName}-in-view`,
                `${componentName}-loaded`
              );
            })
            .catch(error => {
              console.error(`Failed to load ${componentName}:`, error);
            });
        }
      },
      {
        rootMargin: '50px',
        threshold: 0.1,
        ...options
      }
    );

    if (ref.current) {
      observer.observe(ref.current);
    }

    return () => {
      if (ref.current) {
        observer.unobserve(ref.current);
      }
      observer.disconnect();
    };
  }, [importFn, componentName, LazyComponent, options]);

  return { ref, isVisible, LazyComponent };
};

// Component for lazy loading on scroll
export const LazyOnScroll: React.FC<{
  importFn: () => Promise<{ default: React.ComponentType<any> }>;
  componentName: string;
  fallback?: React.ReactNode;
  height?: string;
  className?: string;
  [key: string]: any;
}> = ({ 
  importFn, 
  componentName, 
  fallback = <DefaultLoadingSpinner />, 
  height = '200px',
  className = '',
  ...props 
}) => {
  const { ref, isVisible, LazyComponent } = useInViewLazy(importFn, componentName);

  return (
    <div 
      ref={ref} 
      className={`lazy-on-scroll ${className}`}
      style={{ minHeight: height }}
    >
      {LazyComponent ? (
        React.createElement(LazyComponent as React.ComponentType<any>, props)
      ) : isVisible ? (
        fallback as React.ReactNode
      ) : (
        <div className="lazy-placeholder">Content will load when visible</div>
      )}
    </div>
  );
};

// Batch lazy loading for multiple components
export const useBatchLazyLoad = (
  components: Array<{
    importFn: () => Promise<{ default: React.ComponentType<any> }>;
    name: string;
  }>,
  batchSize: number = 2
) => {
  const [loadedComponents, setLoadedComponents] = React.useState<Map<string, React.ComponentType<any>>>(new Map());
  const [loading, setLoading] = React.useState(false);

  const loadBatch = React.useCallback(async (startIndex: number) => {
    if (loading) return;
    
    setLoading(true);
    const batch = components.slice(startIndex, startIndex + batchSize);
    
    try {
      const results = await Promise.allSettled(
        batch.map(async ({ importFn, name }) => {
          const module = await importFn();
          return { name, component: module.default };
        })
      );

      setLoadedComponents(prev => {
        const updated = new Map(prev);
        results.forEach(result => {
          if (result.status === 'fulfilled') {
            updated.set(result.value.name, result.value.component);
          }
        });
        return updated;
      });
    } catch (error) {
      console.error('Batch loading failed:', error);
    } finally {
      setLoading(false);
    }
  }, [components, batchSize, loading]);

  return { loadedComponents, loadBatch, loading };
};

export default LazyComponentWrapper;

// CSS for lazy loading components
export const lazyLoadingStyles = `
.lazy-loading-spinner {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.lazy-on-scroll {
  transition: opacity 0.3s ease-in-out;
}

.lazy-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background-color: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 4px;
  color: #6c757d;
  font-style: italic;
  min-height: 100px;
}

.lazy-error {
  padding: 20px;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  color: #721c24;
  text-align: center;
}
`;