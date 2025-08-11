import React from 'react';
import { motion } from 'framer-motion';
import clsx from 'clsx';
import { format } from 'date-fns';

const ProgressTimeline = ({ 
  events = [], 
  currentStatus,
  className = '',
  orientation = 'vertical',
  animated = true,
  ...props 
}) => {
  const getStatusIcon = (eventType, isCompleted, isException) => {
    const iconClasses = clsx(
      'w-5 h-5',
      isException ? 'text-red-600' : isCompleted ? 'text-green-600' : 'text-gray-400'
    );

    if (isException) {
      return (
        <svg className={iconClasses} fill="currentColor" viewBox="0 0 20 20">
          <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
        </svg>
      );
    }

    switch (eventType) {
      case 'PICKUP':
        return (
          <svg className={iconClasses} fill="currentColor" viewBox="0 0 20 20">
            <path d="M3 4a1 1 0 011-1h12a1 1 0 011 1v2a1 1 0 01-1 1H4a1 1 0 01-1-1V4zM3 10a1 1 0 011-1h6a1 1 0 011 1v6a1 1 0 01-1 1H4a1 1 0 01-1-1v-6zM14 9a1 1 0 00-1 1v6a1 1 0 001 1h2a1 1 0 001-1v-6a1 1 0 00-1-1h-2z" />
          </svg>
        );
      case 'TRANSIT':
        return (
          <svg className={iconClasses} fill="currentColor" viewBox="0 0 20 20">
            <path d="M8 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM15 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
            <path d="M3 4a1 1 0 00-1 1v10a1 1 0 001 1h1.05a2.5 2.5 0 014.9 0H10a1 1 0 001-1V5a1 1 0 00-1-1H3zM14 7a1 1 0 00-1 1v6.05A2.5 2.5 0 0115.95 16H17a1 1 0 001-1v-5a1 1 0 00-.293-.707L16 7.586A1 1 0 0015.414 7H14z" />
          </svg>
        );
      case 'DELIVERY':
        return (
          <svg className={iconClasses} fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd" />
          </svg>
        );
      default:
        return (
          <div className={clsx(
            'w-2 h-2 rounded-full',
            isCompleted ? 'bg-green-600' : 'bg-gray-400'
          )} />
        );
    }
  };

  const isEventCompleted = (event, index) => {
    // Check if this event has occurred based on timestamp
    return event.eventTimestamp && new Date(event.eventTimestamp) <= new Date();
  };

  const getConnectionLineClass = (index, isCompleted) => {
    const baseClasses = orientation === 'vertical' ? 'w-0.5 h-8' : 'h-0.5 w-8';
    const colorClass = isCompleted ? 'bg-green-600' : 'bg-gray-300';
    return clsx(baseClasses, colorClass, 'transition-colors duration-500');
  };

  if (orientation === 'horizontal') {
    return (
      <div className={clsx('flex items-center space-x-4 py-4', className)} {...props}>
        {events.map((event, index) => {
          const isCompleted = isEventCompleted(event, index);
          const isException = event.isException;

          return (
            <React.Fragment key={event.id || index}>
              <motion.div
                className="flex flex-col items-center"
                initial={animated ? { opacity: 0, scale: 0.8 } : {}}
                animate={animated ? { opacity: 1, scale: 1 } : {}}
                transition={animated ? { duration: 0.3, delay: index * 0.1 } : {}}
              >
                {/* Icon */}
                <div className={clsx(
                  'flex items-center justify-center w-10 h-10 rounded-full border-2 transition-all duration-300',
                  isException
                    ? 'bg-red-50 border-red-200'
                    : isCompleted
                    ? 'bg-green-50 border-green-200'
                    : 'bg-gray-50 border-gray-200'
                )}>
                  {getStatusIcon(event.eventType, isCompleted, isException)}
                </div>

                {/* Label */}
                <div className="mt-2 text-center">
                  <div className={clsx(
                    'text-sm font-medium',
                    isException ? 'text-red-700' : isCompleted ? 'text-green-700' : 'text-gray-500'
                  )}>
                    {event.eventCode}
                  </div>
                  {event.eventTimestamp && (
                    <div className="text-xs text-gray-500 mt-1">
                      {format(new Date(event.eventTimestamp), 'MMM d, HH:mm')}
                    </div>
                  )}
                </div>
              </motion.div>

              {/* Connection Line */}
              {index < events.length - 1 && (
                <div className={getConnectionLineClass(index, isCompleted)} />
              )}
            </React.Fragment>
          );
        })}
      </div>
    );
  }

  // Vertical orientation (default)
  return (
    <div className={clsx('space-y-4', className)} {...props}>
      {events.map((event, index) => {
        const isCompleted = isEventCompleted(event, index);
        const isException = event.isException;

        return (
          <motion.div
            key={event.id || index}
            className="flex items-start space-x-4"
            initial={animated ? { opacity: 0, x: -20 } : {}}
            animate={animated ? { opacity: 1, x: 0 } : {}}
            transition={animated ? { duration: 0.3, delay: index * 0.1 } : {}}
          >
            {/* Timeline connector */}
            <div className="flex flex-col items-center">
              {/* Icon */}
              <div className={clsx(
                'flex items-center justify-center w-10 h-10 rounded-full border-2 transition-all duration-300 z-10',
                isException
                  ? 'bg-red-50 border-red-200'
                  : isCompleted
                  ? 'bg-green-50 border-green-200'
                  : 'bg-gray-50 border-gray-200'
              )}>
                {getStatusIcon(event.eventType, isCompleted, isException)}
              </div>

              {/* Vertical line */}
              {index < events.length - 1 && (
                <div className={getConnectionLineClass(index, isCompleted)} />
              )}
            </div>

            {/* Content */}
            <div className="flex-1 min-w-0 pb-8">
              <div className="flex items-center justify-between">
                <h4 className={clsx(
                  'text-sm font-medium',
                  isException ? 'text-red-700' : isCompleted ? 'text-green-700' : 'text-gray-700'
                )}>
                  {event.eventDescription || event.eventCode}
                </h4>
                {event.eventTimestamp && (
                  <time className="text-sm text-gray-500">
                    {format(new Date(event.eventTimestamp), 'MMM d, yyyy HH:mm')}
                  </time>
                )}
              </div>

              {event.locationName && (
                <p className="text-sm text-gray-600 mt-1">
                  📍 {event.locationName}
                  {event.locationCity && `, ${event.locationCity}`}
                  {event.locationCountry && `, ${event.locationCountry}`}
                </p>
              )}

              {isException && event.exceptionReason && (
                <div className="mt-2 p-3 bg-red-50 border border-red-200 rounded-md">
                  <p className="text-sm text-red-700">
                    <strong>Exception:</strong> {event.exceptionReason}
                  </p>
                </div>
              )}
            </div>
          </motion.div>
        );
      })}
    </div>
  );
};

export default ProgressTimeline;