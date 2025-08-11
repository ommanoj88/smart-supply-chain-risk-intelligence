import React from 'react';
import clsx from 'clsx';

const StatusBadge = ({ 
  status, 
  variant = 'default', 
  size = 'md', 
  className = '',
  ...props 
}) => {
  const baseClasses = 'inline-flex items-center justify-center font-medium rounded-full transition-all duration-200';
  
  const sizeClasses = {
    sm: 'px-2 py-1 text-xs',
    md: 'px-2.5 py-1 text-xs',
    lg: 'px-3 py-1.5 text-sm'
  };

  // Shipment status variants
  const statusVariants = {
    CREATED: 'bg-blue-100 text-blue-800 border border-blue-200',
    PICKED_UP: 'bg-indigo-100 text-indigo-800 border border-indigo-200',
    IN_TRANSIT: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
    OUT_FOR_DELIVERY: 'bg-orange-100 text-orange-800 border border-orange-200',
    DELIVERED: 'bg-green-100 text-green-800 border border-green-200',
    EXCEPTION: 'bg-red-100 text-red-800 border border-red-200',
    
    // Risk levels
    LOW: 'bg-green-100 text-green-800 border border-green-200',
    MEDIUM: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
    HIGH: 'bg-red-100 text-red-800 border border-red-200',
    
    // General variants
    success: 'bg-green-100 text-green-800 border border-green-200',
    warning: 'bg-yellow-100 text-yellow-800 border border-yellow-200',
    error: 'bg-red-100 text-red-800 border border-red-200',
    info: 'bg-blue-100 text-blue-800 border border-blue-200',
    default: 'bg-gray-100 text-gray-800 border border-gray-200'
  };

  const getVariantClass = () => {
    if (statusVariants[status]) {
      return statusVariants[status];
    }
    return statusVariants[variant] || statusVariants.default;
  };

  const getStatusIcon = () => {
    const iconClasses = 'w-3 h-3 mr-1';
    
    switch (status) {
      case 'CREATED':
        return <div className={clsx(iconClasses, 'bg-blue-600 rounded-full')} />;
      case 'PICKED_UP':
        return <div className={clsx(iconClasses, 'bg-indigo-600 rounded-full')} />;
      case 'IN_TRANSIT':
        return (
          <svg className={iconClasses} fill="currentColor" viewBox="0 0 20 20">
            <path d="M8 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM15 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
            <path d="M3 4a1 1 0 00-1 1v10a1 1 0 001 1h1.05a2.5 2.5 0 014.9 0H10a1 1 0 001-1V5a1 1 0 00-1-1H3zM14 7a1 1 0 00-1 1v6.05A2.5 2.5 0 0115.95 16H17a1 1 0 001-1v-5a1 1 0 00-.293-.707L16 7.586A1 1 0 0015.414 7H14z" />
          </svg>
        );
      case 'OUT_FOR_DELIVERY':
        return (
          <svg className={iconClasses} fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-8.293l-3-3a1 1 0 00-1.414 1.414L10.586 9.5H7a1 1 0 100 2h3.586l-1.293 1.293a1 1 0 101.414 1.414l3-3a1 1 0 000-1.414z" clipRule="evenodd" />
          </svg>
        );
      case 'DELIVERED':
        return (
          <svg className={iconClasses} fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
          </svg>
        );
      case 'EXCEPTION':
        return (
          <svg className={iconClasses} fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
          </svg>
        );
      default:
        return null;
    }
  };

  const formatStatusText = (status) => {
    if (!status) return '';
    return status.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase());
  };

  return (
    <span
      className={clsx(
        baseClasses,
        sizeClasses[size],
        getVariantClass(),
        className
      )}
      {...props}
    >
      {getStatusIcon()}
      {formatStatusText(status)}
    </span>
  );
};

export default StatusBadge;