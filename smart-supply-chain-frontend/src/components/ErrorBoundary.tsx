import React from 'react';

interface ErrorBoundaryState {
  hasError: boolean;
  error: Error | null;
  errorInfo: any;
}

interface ErrorBoundaryProps {
  children: React.ReactNode;
  fallback?: React.ComponentType<{error: Error; retry: () => void}>;
  onError?: (error: Error, errorInfo: any) => void;
}

class ErrorBoundary extends React.Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null
    };
  }

  static getDerivedStateFromError(error: Error): Partial<ErrorBoundaryState> {
    return {
      hasError: true,
      error
    };
  }

  componentDidCatch(error: Error, errorInfo: any) {
    this.setState({
      error,
      errorInfo
    });

    // Log error details
    console.error('ErrorBoundary caught an error:', error, errorInfo);

    // Send error to monitoring service
    this.logErrorToService(error, errorInfo);

    // Call custom error handler if provided
    if (this.props.onError) {
      this.props.onError(error, errorInfo);
    }
  }

  private logErrorToService(error: Error, errorInfo: any) {
    try {
      // In a real application, you would send this to a service like Sentry
      const errorReport = {
        message: error.message,
        stack: error.stack,
        componentStack: errorInfo.componentStack,
        timestamp: new Date().toISOString(),
        userAgent: navigator.userAgent,
        url: window.location.href,
        userId: localStorage.getItem('userId') || 'anonymous'
      };

      // For now, just log to console (replace with actual error reporting service)
      console.error('Error Report:', errorReport);

      // You could also send to your backend
      // fetch('/api/errors', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(errorReport)
      // }).catch(console.error);
    } catch (loggingError) {
      console.error('Failed to log error:', loggingError);
    }
  }

  retry = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null
    });
  };

  render() {
    if (this.state.hasError) {
      // Custom fallback component
      if (this.props.fallback) {
        const FallbackComponent = this.props.fallback;
        return <FallbackComponent error={this.state.error!} retry={this.retry} />;
      }

      // Default error UI
      return (
        <div className="error-boundary">
          <div className="error-boundary-content">
            <h2>Something went wrong</h2>
            <p>We're sorry, but something unexpected happened.</p>
            
            {process.env.NODE_ENV === 'development' && (
              <details className="error-details">
                <summary>Error Details (Development Only)</summary>
                <pre className="error-stack">
                  {this.state.error?.message}
                  {'\n\n'}
                  {this.state.error?.stack}
                  {this.state.errorInfo?.componentStack && (
                    <>
                      {'\n\nComponent Stack:'}
                      {this.state.errorInfo.componentStack}
                    </>
                  )}
                </pre>
              </details>
            )}
            
            <div className="error-actions">
              <button 
                onClick={this.retry}
                className="retry-button"
              >
                Try Again
              </button>
              <button 
                onClick={() => window.location.reload()}
                className="reload-button"
              >
                Reload Page
              </button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

// Hook for handling async errors in functional components
export const useErrorHandler = () => {
  const [error, setError] = React.useState<Error | null>(null);

  const handleError = React.useCallback((error: Error) => {
    console.error('Async error caught:', error);
    setError(error);
  }, []);

  const clearError = React.useCallback(() => {
    setError(null);
  }, []);

  // Throw error to nearest error boundary
  if (error) {
    throw error;
  }

  return { handleError, clearError };
};

// Specific error boundary for async operations
export const AsyncErrorBoundary: React.FC<{
  children: React.ReactNode;
  onRetry?: () => void;
}> = ({ children, onRetry }) => {
  const handleError = (error: Error) => {
    console.error('Async operation failed:', error);
  };

  const FallbackComponent = ({ error, retry }: { error: Error; retry: () => void }) => (
    <div className="async-error-boundary">
      <h3>Operation Failed</h3>
      <p>An error occurred while loading data.</p>
      <p className="error-message">{error.message}</p>
      <div className="error-actions">
        <button onClick={onRetry || retry}>Retry</button>
      </div>
    </div>
  );

  return (
    <ErrorBoundary fallback={FallbackComponent} onError={handleError}>
      {children}
    </ErrorBoundary>
  );
};

export default ErrorBoundary;

// CSS styles (add to your global CSS or styled components)
export const errorBoundaryStyles = `
.error-boundary {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 20px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 8px;
  margin: 20px;
}

.error-boundary-content {
  text-align: center;
  max-width: 600px;
}

.error-boundary h2 {
  color: #dc3545;
  margin-bottom: 16px;
}

.error-boundary p {
  color: #6c757d;
  margin-bottom: 24px;
}

.error-details {
  text-align: left;
  margin: 20px 0;
  padding: 16px;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.error-stack {
  white-space: pre-wrap;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  color: #495057;
  max-height: 300px;
  overflow-y: auto;
}

.error-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.retry-button, .reload-button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.retry-button {
  background-color: #007bff;
  color: white;
}

.retry-button:hover {
  background-color: #0056b3;
}

.reload-button {
  background-color: #6c757d;
  color: white;
}

.reload-button:hover {
  background-color: #545b62;
}

.async-error-boundary {
  padding: 20px;
  text-align: center;
  background-color: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  margin: 10px 0;
}

.async-error-boundary h3 {
  color: #856404;
  margin-bottom: 8px;
}

.async-error-boundary .error-message {
  color: #856404;
  font-style: italic;
  margin: 8px 0;
}
`;