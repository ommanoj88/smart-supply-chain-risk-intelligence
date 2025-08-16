import React, { useEffect, useState } from 'react';
import { Box, Typography, useTheme, alpha } from '@mui/material';
import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown, TrendingFlat } from '@mui/icons-material';

interface AnimatedCounterProps {
  label: string;
  value: number;
  previousValue?: number;
  format?: 'number' | 'currency' | 'percentage';
  prefix?: string;
  suffix?: string;
  trend?: {
    value: number;
    direction: 'up' | 'down' | 'neutral';
    period?: string;
  };
  animated?: boolean;
  duration?: number;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info';
  size?: 'small' | 'medium' | 'large';
  icon?: React.ReactNode;
}

const AnimatedCounter: React.FC<AnimatedCounterProps> = ({
  label,
  value,
  previousValue,
  format = 'number',
  prefix = '',
  suffix = '',
  trend,
  animated = true,
  duration = 2000,
  color = 'primary',
  size = 'medium',
  icon,
}) => {
  const theme = useTheme();
  const [displayValue, setDisplayValue] = useState(0);
  const [isAnimating, setIsAnimating] = useState(false);

  useEffect(() => {
    if (animated) {
      setIsAnimating(true);
      const steps = 60;
      const stepValue = value / steps;
      const stepDuration = duration / steps;
      let currentStep = 0;

      const interval = setInterval(() => {
        currentStep++;
        setDisplayValue(Math.round(stepValue * currentStep));

        if (currentStep >= steps) {
          setDisplayValue(value);
          setIsAnimating(false);
          clearInterval(interval);
        }
      }, stepDuration);

      return () => {
        clearInterval(interval);
        setIsAnimating(false);
      };
    } else {
      setDisplayValue(value);
      return undefined;
    }
  }, [value, animated, duration]);

  const formatValue = (val: number): string => {
    switch (format) {
      case 'currency':
        return new Intl.NumberFormat('en-US', {
          style: 'currency',
          currency: 'USD',
          minimumFractionDigits: 0,
          maximumFractionDigits: val >= 1000000 ? 1 : 0,
        }).format(val);
      case 'percentage':
        return `${val.toFixed(1)}%`;
      case 'number':
      default:
        return val.toLocaleString();
    }
  };

  const getColorValue = () => {
    switch (color) {
      case 'success':
        return theme.palette.success.main;
      case 'warning':
        return theme.palette.warning.main;
      case 'error':
        return theme.palette.error.main;
      case 'info':
        return theme.palette.info.main;
      case 'primary':
      default:
        return theme.palette.primary.main;
    }
  };

  const getFontSize = () => {
    switch (size) {
      case 'small':
        return '1.5rem';
      case 'large':
        return '3rem';
      case 'medium':
      default:
        return '2.25rem';
    }
  };

  const getTrendIcon = () => {
    if (!trend) return null;
    
    const iconColor = trend.direction === 'up' 
      ? theme.palette.success.main 
      : trend.direction === 'down' 
      ? theme.palette.error.main 
      : theme.palette.text.secondary;

    const IconComponent = trend.direction === 'up' 
      ? TrendingUp 
      : trend.direction === 'down' 
      ? TrendingDown 
      : TrendingFlat;

    return <IconComponent sx={{ color: iconColor, fontSize: '1rem' }} />;
  };

  const colorValue = getColorValue();

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <Box
        sx={{
          p: 3,
          borderRadius: 2,
          background: `linear-gradient(135deg, ${alpha(colorValue, 0.05)} 0%, ${alpha(colorValue, 0.02)} 100%)`,
          border: `1px solid ${alpha(colorValue, 0.1)}`,
          position: 'relative',
          overflow: 'hidden',
          transition: 'all 0.3s ease',
          '&:hover': {
            transform: 'translateY(-2px)',
            boxShadow: `0 8px 32px ${alpha(colorValue, 0.15)}`,
            border: `1px solid ${alpha(colorValue, 0.2)}`,
          },
        }}
      >
        {/* Background decoration */}
        <Box
          sx={{
            position: 'absolute',
            top: -20,
            right: -20,
            width: 80,
            height: 80,
            borderRadius: '50%',
            background: `radial-gradient(circle, ${alpha(colorValue, 0.1)} 0%, transparent 70%)`,
            pointerEvents: 'none',
          }}
        />

        {/* Header with label and icon */}
        <Box display="flex" alignItems="center" justifyContent="space-between" sx={{ mb: 2 }}>
          <Typography
            variant="subtitle2"
            sx={{
              color: theme.palette.text.secondary,
              textTransform: 'uppercase',
              letterSpacing: '0.5px',
              fontWeight: 600,
            }}
          >
            {label}
          </Typography>
          {icon && (
            <Box
              sx={{
                color: colorValue,
                opacity: 0.7,
              }}
            >
              {icon}
            </Box>
          )}
        </Box>

        {/* Main value display */}
        <Box display="flex" alignItems="baseline" sx={{ mb: 1 }}>
          <motion.div
            animate={{
              scale: isAnimating ? [1, 1.05, 1] : 1,
            }}
            transition={{
              duration: 0.5,
              repeat: isAnimating ? Infinity : 0,
              repeatType: 'reverse',
            }}
          >
            <Typography
              variant="h3"
              sx={{
                fontWeight: 700,
                fontSize: getFontSize(),
                color: colorValue,
                lineHeight: 1,
                fontFeatureSettings: '"tnum"', // Tabular numbers for better alignment
              }}
            >
              {prefix}
              {formatValue(displayValue)}
              {suffix}
            </Typography>
          </motion.div>
        </Box>

        {/* Trend indicator */}
        {trend && (
          <Box display="flex" alignItems="center" gap={0.5}>
            {getTrendIcon()}
            <Typography
              variant="body2"
              sx={{
                color: trend.direction === 'up' 
                  ? theme.palette.success.main 
                  : trend.direction === 'down' 
                  ? theme.palette.error.main 
                  : theme.palette.text.secondary,
                fontWeight: 600,
              }}
            >
              {trend.direction === 'up' ? '+' : trend.direction === 'down' ? '-' : ''}
              {Math.abs(trend.value)}
              {format === 'percentage' ? 'pp' : '%'}
            </Typography>
            {trend.period && (
              <Typography
                variant="body2"
                sx={{
                  color: theme.palette.text.secondary,
                  ml: 0.5,
                }}
              >
                {trend.period}
              </Typography>
            )}
          </Box>
        )}

        {/* Previous value comparison */}
        {previousValue !== undefined && previousValue !== value && (
          <Typography
            variant="caption"
            sx={{
              color: theme.palette.text.secondary,
              display: 'block',
              mt: 0.5,
            }}
          >
            Previous: {formatValue(previousValue)}
          </Typography>
        )}
      </Box>
    </motion.div>
  );
};

export default AnimatedCounter;