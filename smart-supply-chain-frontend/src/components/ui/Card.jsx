import React from 'react';
import { motion } from 'framer-motion';
import clsx from 'clsx';

const Card = ({ 
  children, 
  variant = 'elevated', 
  hover = false, 
  className = '', 
  onClick,
  ...props 
}) => {
  const baseClasses = 'rounded-lg transition-all duration-200 ease-in-out';
  
  const variantClasses = {
    elevated: 'bg-white shadow-lg border-0',
    outlined: 'bg-white shadow-sm border border-gray-200',
    flat: 'bg-gray-50 shadow-none border-0'
  };

  const hoverClasses = hover ? 'hover:shadow-xl hover:-translate-y-0.5 cursor-pointer' : '';

  const Component = onClick || hover ? motion.div : 'div';
  const motionProps = onClick || hover ? {
    whileHover: { y: -2, boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)' },
    whileTap: { y: 0 }
  } : {};

  return (
    <Component
      className={clsx(
        baseClasses,
        variantClasses[variant],
        hoverClasses,
        className
      )}
      onClick={onClick}
      {...motionProps}
      {...props}
    >
      {children}
    </Component>
  );
};

// Card Header Component
const CardHeader = ({ children, className = '', ...props }) => (
  <div 
    className={clsx('px-6 py-4 border-b border-gray-200', className)}
    {...props}
  >
    {children}
  </div>
);

// Card Content Component
const CardContent = ({ children, className = '', ...props }) => (
  <div 
    className={clsx('px-6 py-4', className)}
    {...props}
  >
    {children}
  </div>
);

// Card Footer Component
const CardFooter = ({ children, className = '', ...props }) => (
  <div 
    className={clsx('px-6 py-4 border-t border-gray-200 bg-gray-50 rounded-b-lg', className)}
    {...props}
  >
    {children}
  </div>
);

// Card Title Component
const CardTitle = ({ children, className = '', ...props }) => (
  <h3 
    className={clsx('text-lg font-semibold text-gray-900 leading-tight', className)}
    {...props}
  >
    {children}
  </h3>
);

// Card Description Component
const CardDescription = ({ children, className = '', ...props }) => (
  <p 
    className={clsx('text-sm text-gray-600 mt-1', className)}
    {...props}
  >
    {children}
  </p>
);

// Export all components
Card.Header = CardHeader;
Card.Content = CardContent;
Card.Footer = CardFooter;
Card.Title = CardTitle;
Card.Description = CardDescription;

export default Card;