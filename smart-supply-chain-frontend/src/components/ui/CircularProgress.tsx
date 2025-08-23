import React, { useEffect, useState } from 'react';
import { Box, Typography, useTheme } from '@mui/material';
import { motion } from 'framer-motion';

interface CircularProgressProps {
  value: number;
  max?: number;
  size?: number;
  strokeWidth?: number;
  showValue?: boolean;
  animated?: boolean;
  gradient?: boolean;
  color?: string;
  title?: string;
  subtitle?: string;
}

const CircularProgress: React.FC<CircularProgressProps> = ({
  value,
  max = 100,
  size = 200,
  strokeWidth = 12,
  showValue = true,
  animated = true,
  gradient = true,
  color,
  title,
  subtitle,
}) => {
  const theme = useTheme();
  const [animatedValue, setAnimatedValue] = useState(0);
  
  const radius = (size - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const percentage = Math.min(Math.max(value, 0), max) / max;
  const strokeDasharray = circumference;
  const strokeDashoffset = circumference - (percentage * circumference);
  
  // Animate the value
  useEffect(() => {
    if (animated) {
      const animationDuration = 2000; // 2 seconds
      const steps = 60;
      const stepValue = value / steps;
      let currentStep = 0;
      
      const interval = setInterval(() => {
        currentStep++;
        setAnimatedValue(stepValue * currentStep);
        
        if (currentStep >= steps) {
          setAnimatedValue(value);
          clearInterval(interval);
        }
      }, animationDuration / steps);
      
      return () => clearInterval(interval);
    } else {
      setAnimatedValue(value);
      return undefined;
    }
  }, [value, animated]);
  
  // Determine color based on value
  const getColor = () => {
    if (color) return color;
    if (value >= 90) return theme.palette.success.main;
    if (value >= 80) return theme.palette.info.main;
    if (value >= 70) return theme.palette.warning.main;
    return theme.palette.error.main;
  };
  
  const progressColor = getColor();
  
  return (
    <Box 
      display="flex" 
      flexDirection="column" 
      alignItems="center" 
      justifyContent="center"
      sx={{ position: 'relative' }}
    >
      {title && (
        <Typography 
          variant="h6" 
          fontWeight={600} 
          sx={{ mb: 1, textAlign: 'center' }}
        >
          {title}
        </Typography>
      )}
      
      <Box sx={{ position: 'relative', display: 'inline-flex' }}>
        <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
          {/* Background circle */}
          <circle
            cx={size / 2}
            cy={size / 2}
            r={radius}
            stroke={theme.palette.divider}
            strokeWidth={strokeWidth}
            fill="none"
            opacity={0.3}
          />
          
          {/* Progress circle */}
          {gradient ? (
            <>
              <defs>
                <linearGradient id={`gradient-${size}`} x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor={progressColor} stopOpacity="1" />
                  <stop offset="100%" stopColor={progressColor} stopOpacity="0.6" />
                </linearGradient>
              </defs>
              <motion.circle
                cx={size / 2}
                cy={size / 2}
                r={radius}
                stroke={`url(#gradient-${size})`}
                strokeWidth={strokeWidth}
                fill="none"
                strokeLinecap="round"
                strokeDasharray={strokeDasharray}
                initial={{ strokeDashoffset: circumference }}
                animate={{ 
                  strokeDashoffset: animated ? strokeDashoffset : circumference - (animatedValue / max * circumference)
                }}
                transition={{ 
                  duration: animated ? 2 : 0, 
                  ease: "easeInOut" 
                }}
                style={{
                  filter: `drop-shadow(0 0 8px ${progressColor}40)`,
                }}
              />
            </>
          ) : (
            <motion.circle
              cx={size / 2}
              cy={size / 2}
              r={radius}
              stroke={progressColor}
              strokeWidth={strokeWidth}
              fill="none"
              strokeLinecap="round"
              strokeDasharray={strokeDasharray}
              initial={{ strokeDashoffset: circumference }}
              animate={{ 
                strokeDashoffset: animated ? strokeDashoffset : circumference - (animatedValue / max * circumference)
              }}
              transition={{ 
                duration: animated ? 2 : 0, 
                ease: "easeInOut" 
              }}
            />
          )}
        </svg>
        
        {/* Center value display */}
        {showValue && (
          <Box
            sx={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <motion.div
              initial={{ opacity: 0, scale: 0.5 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: animated ? 1 : 0, duration: 0.5 }}
            >
              <Typography
                variant="h3"
                fontWeight={700}
                sx={{
                  color: progressColor,
                  lineHeight: 1,
                  fontSize: size > 150 ? '2.5rem' : '1.8rem',
                }}
              >
                {Math.round(animatedValue)}
              </Typography>
              <Typography
                variant="body2"
                sx={{
                  color: theme.palette.text.secondary,
                  fontSize: size > 150 ? '0.875rem' : '0.75rem',
                  textAlign: 'center',
                }}
              >
                {max === 100 ? '%' : `/ ${max}`}
              </Typography>
            </motion.div>
          </Box>
        )}
      </Box>
      
      {subtitle && (
        <Typography 
          variant="body2" 
          color="text.secondary" 
          sx={{ mt: 1, textAlign: 'center', maxWidth: size }}
        >
          {subtitle}
        </Typography>
      )}
    </Box>
  );
};

export default CircularProgress;