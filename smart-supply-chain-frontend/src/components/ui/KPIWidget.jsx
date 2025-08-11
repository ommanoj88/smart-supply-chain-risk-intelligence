import React from 'react';
import { motion } from 'framer-motion';
import clsx from 'clsx';
import Card from './Card';

const KPIWidget = ({ 
  title, 
  value, 
  subtitle, 
  trend, 
  trendValue, 
  icon, 
  color = 'blue',
  className = '',
  onClick,
  ...props 
}) => {
  const colorClasses = {
    blue: {
      bg: 'bg-blue-50',
      icon: 'text-blue-600',
      accent: 'border-blue-200'
    },
    orange: {
      bg: 'bg-orange-50',
      icon: 'text-orange-600',
      accent: 'border-orange-200'
    },
    green: {
      bg: 'bg-green-50',
      icon: 'text-green-600',
      accent: 'border-green-200'
    },
    red: {
      bg: 'bg-red-50',
      icon: 'text-red-600',
      accent: 'border-red-200'
    },
    indigo: {
      bg: 'bg-indigo-50',
      icon: 'text-indigo-600',
      accent: 'border-indigo-200'
    }
  };

  const trendClasses = {
    up: 'text-green-600',
    down: 'text-red-600',
    neutral: 'text-gray-500'
  };

  const getTrendIcon = () => {
    switch (trend) {
      case 'up':
        return (
          <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M3.293 9.707a1 1 0 010-1.414l6-6a1 1 0 011.414 0l6 6a1 1 0 01-1.414 1.414L10 4.414 4.707 9.707a1 1 0 01-1.414 0z" clipRule="evenodd" />
          </svg>
        );
      case 'down':
        return (
          <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M16.707 10.293a1 1 0 010 1.414l-6 6a1 1 0 01-1.414 0l-6-6a1 1 0 111.414-1.414L10 15.586l5.293-5.293a1 1 0 011.414 0z" clipRule="evenodd" />
          </svg>
        );
      case 'neutral':
        return (
          <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M3 10a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clipRule="evenodd" />
          </svg>
        );
      default:
        return null;
    }
  };

  const formatValue = (val) => {
    if (typeof val === 'number') {
      if (val >= 1000000) {
        return `${(val / 1000000).toFixed(1)}M`;
      } else if (val >= 1000) {
        return `${(val / 1000).toFixed(1)}K`;
      }
      return val.toLocaleString();
    }
    return val;
  };

  return (
    <Card 
      variant="elevated" 
      hover={!!onClick}
      onClick={onClick}
      className={clsx('relative overflow-hidden', className)}
      {...props}
    >
      <div className={clsx('absolute top-0 left-0 w-full h-1', colorClasses[color].accent)} />
      
      <Card.Content className="p-6">
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <h3 className="text-sm font-medium text-gray-600 mb-1">
              {title}
            </h3>
            <motion.div 
              className="text-2xl font-bold text-gray-900 mb-1"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3 }}
            >
              {formatValue(value)}
            </motion.div>
            {subtitle && (
              <p className="text-sm text-gray-500">
                {subtitle}
              </p>
            )}
          </div>
          
          {icon && (
            <div className={clsx(
              'p-3 rounded-lg',
              colorClasses[color].bg
            )}>
              <div className={clsx('w-6 h-6', colorClasses[color].icon)}>
                {icon}
              </div>
            </div>
          )}
        </div>

        {(trend && trendValue) && (
          <div className="mt-4 flex items-center">
            <div className={clsx('flex items-center', trendClasses[trend])}>
              {getTrendIcon()}
              <span className="ml-1 text-sm font-medium">
                {trendValue}
              </span>
            </div>
            <span className="ml-2 text-sm text-gray-500">
              vs last period
            </span>
          </div>
        )}
      </Card.Content>
    </Card>
  );
};

export default KPIWidget;