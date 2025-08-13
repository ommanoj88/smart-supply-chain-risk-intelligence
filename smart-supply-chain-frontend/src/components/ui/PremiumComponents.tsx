import React, { useState, useRef, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  IconButton,
  TextField,
  Chip,
  Avatar,
  Badge,
  LinearProgress,
  CircularProgress,
  Paper,
  useTheme,
  alpha,
  Fade,
  Grow,
  Slide,
  Zoom,
  Collapse,
  styled,
  keyframes,
} from '@mui/material';
import { motion, AnimatePresence, useAnimation, useInView } from 'framer-motion';
import {
  Search,
  FilterList,
  Download,
  Share,
  Fullscreen,
  Refresh,
  MoreVert,
  TrendingUp,
  TrendingDown,
  Remove,
  Visibility,
  VisibilityOff,
  PlayArrow,
  Pause,
  Stop,
} from '@mui/icons-material';

// Enhanced Animation Keyframes
const shimmer = keyframes`
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: 200px 0;
  }
`;

const glow = keyframes`
  0%, 100% {
    box-shadow: 0 0 5px rgba(59, 130, 246, 0.5);
  }
  50% {
    box-shadow: 0 0 20px rgba(59, 130, 246, 0.8), 0 0 30px rgba(59, 130, 246, 0.6);
  }
`;

const float = keyframes`
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
`;

const pulse = keyframes`
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
`;

// Styled Components with Premium Effects
const GlassCard = styled(Card)(({ theme }) => ({
  background: `linear-gradient(145deg, 
    ${alpha(theme.palette.background.paper, 0.8)} 0%, 
    ${alpha(theme.palette.background.paper, 0.4)} 100%
  )`,
  backdropFilter: 'blur(20px) saturate(180%)',
  border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
  borderRadius: '20px',
  boxShadow: `
    0 8px 32px ${alpha(theme.palette.common.black, 0.1)},
    0 1px 0 ${alpha(theme.palette.common.white, 0.1)} inset
  `,
  transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
  position: 'relative',
  overflow: 'visible',
  
  '&:hover': {
    transform: 'translateY(-8px) scale(1.02)',
    boxShadow: `
      0 16px 64px ${alpha(theme.palette.common.black, 0.15)},
      0 2px 0 ${alpha(theme.palette.common.white, 0.15)} inset
    `,
    border: `1px solid ${alpha(theme.palette.primary.main, 0.2)}`,
  },

  '&::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    height: '1px',
    background: `linear-gradient(90deg, transparent, ${alpha(theme.palette.common.white, 0.4)}, transparent)`,
    borderRadius: '20px 20px 0 0',
  },
}));

const PremiumButton = styled(Button)(({ theme }) => ({
  borderRadius: '12px',
  textTransform: 'none',
  fontWeight: 600,
  padding: '12px 24px',
  position: 'relative',
  overflow: 'hidden',
  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
  
  '&::before': {
    content: '""',
    position: 'absolute',
    top: 0,
    left: '-100%',
    width: '100%',
    height: '100%',
    background: `linear-gradient(90deg, transparent, ${alpha(theme.palette.common.white, 0.2)}, transparent)`,
    transition: 'left 0.6s',
  },
  
  '&:hover::before': {
    left: '100%',
  },
  
  '&:hover': {
    transform: 'translateY(-2px)',
    boxShadow: `0 8px 24px ${alpha(theme.palette.primary.main, 0.3)}`,
  },
  
  '&:active': {
    transform: 'translateY(0)',
  },
}));

const AnimatedLinearProgress = styled(LinearProgress)(({ theme }) => ({
  height: 8,
  borderRadius: 4,
  backgroundColor: alpha(theme.palette.primary.main, 0.1),
  
  '& .MuiLinearProgress-bar': {
    borderRadius: 4,
    background: `linear-gradient(90deg, 
      ${theme.palette.primary.main} 0%, 
      ${theme.palette.secondary.main} 100%
    )`,
    boxShadow: `0 0 10px ${alpha(theme.palette.primary.main, 0.5)}`,
  },
}));

const FloatingActionButton = styled(IconButton)(({ theme }) => ({
  position: 'fixed',
  bottom: 24,
  right: 24,
  width: 64,
  height: 64,
  background: `linear-gradient(135deg, 
    ${theme.palette.primary.main} 0%, 
    ${theme.palette.secondary.main} 100%
  )`,
  color: theme.palette.primary.contrastText,
  borderRadius: '50%',
  boxShadow: `
    0 8px 32px ${alpha(theme.palette.primary.main, 0.3)},
    0 4px 16px ${alpha(theme.palette.common.black, 0.1)}
  `,
  zIndex: 1000,
  animation: `${float} 3s ease-in-out infinite`,
  
  '&:hover': {
    transform: 'scale(1.1)',
    animation: `${glow} 2s ease-in-out infinite`,
    background: `linear-gradient(135deg, 
      ${theme.palette.primary.dark} 0%, 
      ${theme.palette.secondary.dark} 100%
    )`,
  },
}));

// Interface Definitions
interface PremiumKPICardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  trend?: {
    value: number;
    direction: 'up' | 'down' | 'neutral';
    period?: string;
  };
  icon?: React.ReactNode;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info';
  variant?: 'glass' | 'solid' | 'gradient';
  size?: 'small' | 'medium' | 'large';
  loading?: boolean;
  animated?: boolean;
  interactive?: boolean;
  onClick?: () => void;
}

interface EnhancedSearchBarProps {
  placeholder?: string;
  value?: string;
  onChange?: (value: string) => void;
  onSearch?: (value: string) => void;
  suggestions?: string[];
  loading?: boolean;
  variant?: 'glass' | 'solid';
  size?: 'small' | 'medium' | 'large';
  showFilters?: boolean;
  filterOptions?: { label: string; value: string; count?: number }[];
}

interface AnimatedCounterProps {
  value: number;
  duration?: number;
  prefix?: string;
  suffix?: string;
  decimals?: number;
  variant?: 'h4' | 'h5' | 'h6' | 'subtitle1';
  color?: string;
  animationDelay?: number;
}

interface ProgressRingProps {
  value: number;
  max?: number;
  size?: number;
  strokeWidth?: number;
  color?: string;
  backgroundColor?: string;
  showValue?: boolean;
  animated?: boolean;
  gradient?: boolean;
}

/**
 * Premium KPI Card with Advanced Visual Effects
 */
const PremiumKPICard: React.FC<PremiumKPICardProps> = ({
  title,
  value,
  subtitle,
  trend,
  icon,
  color = 'primary',
  variant = 'glass',
  size = 'medium',
  loading = false,
  animated = true,
  interactive = true,
  onClick,
}) => {
  const theme = useTheme();
  const controls = useAnimation();
  const ref = useRef(null);
  const inView = useInView(ref, { once: true });

  useEffect(() => {
    if (inView && animated) {
      controls.start({
        opacity: 1,
        y: 0,
        scale: 1,
        transition: { duration: 0.6, ease: [0.4, 0, 0.2, 1] }
      });
    }
  }, [controls, inView, animated]);

  const getColorScheme = () => {
    const schemes = {
      primary: {
        main: theme.palette.primary.main,
        light: theme.palette.primary.light,
        dark: theme.palette.primary.dark,
      },
      success: {
        main: theme.palette.success.main,
        light: theme.palette.success.light,
        dark: theme.palette.success.dark,
      },
      warning: {
        main: theme.palette.warning.main,
        light: theme.palette.warning.light,
        dark: theme.palette.warning.dark,
      },
      error: {
        main: theme.palette.error.main,
        light: theme.palette.error.light,
        dark: theme.palette.error.dark,
      },
      info: {
        main: theme.palette.info.main,
        light: theme.palette.info.light,
        dark: theme.palette.info.dark,
      },
    };
    return schemes[color];
  };

  const getSizeProps = () => {
    switch (size) {
      case 'small':
        return {
          padding: 2,
          titleVariant: 'caption' as const,
          valueVariant: 'h6' as const,
          iconSize: 20,
        };
      case 'large':
        return {
          padding: 4,
          titleVariant: 'subtitle1' as const,
          valueVariant: 'h3' as const,
          iconSize: 32,
        };
      default:
        return {
          padding: 3,
          titleVariant: 'subtitle2' as const,
          valueVariant: 'h4' as const,
          iconSize: 24,
        };
    }
  };

  const colorScheme = getColorScheme();
  const sizeProps = getSizeProps();

  if (loading) {
    return (
      <GlassCard sx={{ height: '100%' }}>
        <CardContent sx={{ p: sizeProps.padding }}>
          <Box display="flex" alignItems="center" gap={2} mb={2}>
            <Box
              sx={{
                width: sizeProps.iconSize,
                height: sizeProps.iconSize,
                borderRadius: '8px',
                background: `linear-gradient(45deg, ${alpha(colorScheme.main, 0.1)}, ${alpha(colorScheme.light, 0.1)})`,
                animation: `${shimmer} 2s infinite`,
              }}
            />
            <Box flex={1}>
              <Box
                sx={{
                  height: 16,
                  borderRadius: 4,
                  background: `linear-gradient(90deg, ${alpha(theme.palette.divider, 0.1)}, ${alpha(theme.palette.divider, 0.3)}, ${alpha(theme.palette.divider, 0.1)})`,
                  backgroundSize: '200px 100%',
                  animation: `${shimmer} 2s infinite`,
                  mb: 1,
                }}
              />
              <Box
                sx={{
                  height: 32,
                  borderRadius: 4,
                  background: `linear-gradient(90deg, ${alpha(theme.palette.divider, 0.1)}, ${alpha(theme.palette.divider, 0.3)}, ${alpha(theme.palette.divider, 0.1)})`,
                  backgroundSize: '200px 100%',
                  animation: `${shimmer} 2s infinite`,
                }}
              />
            </Box>
          </Box>
        </CardContent>
      </GlassCard>
    );
  }

  return (
    <motion.div
      ref={ref}
      initial={animated ? { opacity: 0, y: 20, scale: 0.95 } : {}}
      animate={controls}
      whileHover={interactive ? { scale: 1.02, y: -4 } : {}}
      whileTap={interactive ? { scale: 0.98 } : {}}
    >
      <GlassCard 
        sx={{ 
          height: '100%',
          cursor: interactive && onClick ? 'pointer' : 'default',
          background: variant === 'gradient' 
            ? `linear-gradient(135deg, ${alpha(colorScheme.main, 0.1)} 0%, ${alpha(colorScheme.light, 0.05)} 100%)`
            : undefined,
          borderLeft: variant === 'solid' ? `4px solid ${colorScheme.main}` : undefined,
        }}
        onClick={onClick}
      >
        <CardContent sx={{ p: sizeProps.padding }}>
          {/* Header with Icon */}
          <Box display="flex" alignItems="flex-start" justifyContent="space-between" mb={2}>
            <Box display="flex" alignItems="center" gap={1.5}>
              {icon && (
                <motion.div
                  initial={{ scale: 0, rotate: -180 }}
                  animate={{ scale: 1, rotate: 0 }}
                  transition={{ duration: 0.5, delay: 0.2 }}
                >
                  <Box
                    sx={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      width: sizeProps.iconSize + 16,
                      height: sizeProps.iconSize + 16,
                      borderRadius: '12px',
                      background: `linear-gradient(135deg, ${alpha(colorScheme.main, 0.1)} 0%, ${alpha(colorScheme.light, 0.2)} 100%)`,
                      border: `1px solid ${alpha(colorScheme.main, 0.2)}`,
                      color: colorScheme.main,
                    }}
                  >
                    {React.cloneElement(icon as React.ReactElement, { 
                      sx: { fontSize: sizeProps.iconSize } 
                    })}
                  </Box>
                </motion.div>
              )}
              <Box>
                <Typography 
                  variant={sizeProps.titleVariant}
                  color="text.secondary"
                  fontWeight={600}
                  sx={{ 
                    textTransform: 'uppercase',
                    letterSpacing: '0.05em',
                    mb: 0.5,
                  }}
                >
                  {title}
                </Typography>
                {subtitle && (
                  <Typography variant="caption" color="text.secondary" sx={{ opacity: 0.7 }}>
                    {subtitle}
                  </Typography>
                )}
              </Box>
            </Box>
          </Box>

          {/* Main Value */}
          <motion.div
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.4, delay: 0.3 }}
          >
            <Typography 
              variant={sizeProps.valueVariant}
              component="div" 
              fontWeight={700}
              sx={{ 
                background: `linear-gradient(135deg, ${theme.palette.text.primary} 0%, ${colorScheme.main} 100%)`,
                backgroundClip: 'text',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                fontFeatureSettings: '"tnum"',
                lineHeight: 1.1,
                mb: 1,
              }}
            >
              <AnimatedCounter 
                value={typeof value === 'number' ? value : parseFloat(value.toString().replace(/[^\d.-]/g, ''))}
                animationDelay={0.5}
              />
            </Typography>
          </motion.div>

          {/* Trend Indicator */}
          {trend && (
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.4, delay: 0.4 }}
            >
              <Box display="flex" alignItems="center" gap={1}>
                <Chip
                  icon={
                    trend.direction === 'up' ? <TrendingUp sx={{ fontSize: 16 }} /> :
                    trend.direction === 'down' ? <TrendingDown sx={{ fontSize: 16 }} /> :
                    <Remove sx={{ fontSize: 16 }} />
                  }
                  label={`${Math.abs(trend.value)}%`}
                  size="small"
                  sx={{
                    backgroundColor: trend.direction === 'up' 
                      ? alpha(theme.palette.success.main, 0.1)
                      : trend.direction === 'down'
                      ? alpha(theme.palette.error.main, 0.1)
                      : alpha(theme.palette.warning.main, 0.1),
                    color: trend.direction === 'up' 
                      ? theme.palette.success.main
                      : trend.direction === 'down'
                      ? theme.palette.error.main
                      : theme.palette.warning.main,
                    border: `1px solid ${trend.direction === 'up' 
                      ? alpha(theme.palette.success.main, 0.2)
                      : trend.direction === 'down'
                      ? alpha(theme.palette.error.main, 0.2)
                      : alpha(theme.palette.warning.main, 0.2)}`,
                    fontWeight: 600,
                  }}
                />
                {trend.period && (
                  <Typography variant="caption" color="text.secondary" sx={{ opacity: 0.7 }}>
                    {trend.period}
                  </Typography>
                )}
              </Box>
            </motion.div>
          )}
        </CardContent>
      </GlassCard>
    </motion.div>
  );
};

/**
 * Enhanced Search Bar with Glass Effect
 */
const EnhancedSearchBar: React.FC<EnhancedSearchBarProps> = ({
  placeholder = "Search...",
  value = "",
  onChange,
  onSearch,
  suggestions = [],
  loading = false,
  variant = 'glass',
  size = 'medium',
  showFilters = false,
  filterOptions = [],
}) => {
  const theme = useTheme();
  const [focused, setFocused] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);

  const getHeightBySize = () => {
    switch (size) {
      case 'small': return 40;
      case 'large': return 56;
      default: return 48;
    }
  };

  return (
    <Box sx={{ position: 'relative', width: '100%' }}>
      <motion.div
        whileHover={{ scale: 1.01 }}
        whileFocus={{ scale: 1.02 }}
      >
        <TextField
          fullWidth
          variant="outlined"
          placeholder={placeholder}
          value={value}
          onChange={(e) => onChange?.(e.target.value)}
          onFocus={() => setFocused(true)}
          onBlur={() => setTimeout(() => setFocused(false), 200)}
          InputProps={{
            startAdornment: (
              <Search sx={{ color: 'text.secondary', mr: 1 }} />
            ),
            endAdornment: (
              <Box display="flex" alignItems="center" gap={1}>
                {loading && <CircularProgress size={20} />}
                {showFilters && (
                  <IconButton size="small">
                    <FilterList />
                  </IconButton>
                )}
              </Box>
            ),
          }}
          sx={{
            '& .MuiOutlinedInput-root': {
              height: getHeightBySize(),
              borderRadius: '16px',
              background: variant === 'glass' 
                ? `linear-gradient(145deg, 
                    ${alpha(theme.palette.background.paper, 0.8)} 0%, 
                    ${alpha(theme.palette.background.paper, 0.4)} 100%
                  )`
                : theme.palette.background.paper,
              backdropFilter: variant === 'glass' ? 'blur(20px) saturate(180%)' : 'none',
              border: `1px solid ${alpha(theme.palette.divider, focused ? 0.3 : 0.1)}`,
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              
              '&:hover': {
                border: `1px solid ${alpha(theme.palette.primary.main, 0.3)}`,
                transform: 'translateY(-2px)',
                boxShadow: `0 8px 32px ${alpha(theme.palette.primary.main, 0.1)}`,
              },
              
              '&.Mui-focused': {
                border: `1px solid ${theme.palette.primary.main}`,
                boxShadow: `0 0 0 3px ${alpha(theme.palette.primary.main, 0.1)}`,
              },
            },
            '& .MuiOutlinedInput-notchedOutline': {
              border: 'none',
            },
          }}
        />
      </motion.div>

      {/* Suggestions Dropdown */}
      <AnimatePresence>
        {focused && showSuggestions && suggestions.length > 0 && (
          <motion.div
            initial={{ opacity: 0, y: -10, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -10, scale: 0.95 }}
            transition={{ duration: 0.2 }}
            style={{ position: 'absolute', top: '100%', left: 0, right: 0, zIndex: 1000 }}
          >
            <Paper
              sx={{
                mt: 1,
                borderRadius: '12px',
                background: `linear-gradient(145deg, 
                  ${alpha(theme.palette.background.paper, 0.95)} 0%, 
                  ${alpha(theme.palette.background.paper, 0.8)} 100%
                )`,
                backdropFilter: 'blur(20px)',
                border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
                boxShadow: `0 8px 32px ${alpha(theme.palette.common.black, 0.1)}`,
                maxHeight: 200,
                overflow: 'auto',
              }}
            >
              {suggestions.map((suggestion, index) => (
                <Box
                  key={index}
                  sx={{
                    p: 2,
                    cursor: 'pointer',
                    transition: 'all 0.2s',
                    '&:hover': {
                      backgroundColor: alpha(theme.palette.primary.main, 0.05),
                    },
                  }}
                  onClick={() => {
                    onChange?.(suggestion);
                    onSearch?.(suggestion);
                    setShowSuggestions(false);
                  }}
                >
                  <Typography variant="body2">{suggestion}</Typography>
                </Box>
              ))}
            </Paper>
          </motion.div>
        )}
      </AnimatePresence>
    </Box>
  );
};

/**
 * Animated Counter Component
 */
const AnimatedCounter: React.FC<AnimatedCounterProps> = ({
  value,
  duration = 1.5,
  prefix = "",
  suffix = "",
  decimals = 0,
  variant = 'h4',
  color,
  animationDelay = 0,
}) => {
  const [count, setCount] = useState(0);
  const controls = useAnimation();
  const ref = useRef(null);
  const inView = useInView(ref, { once: true });

  useEffect(() => {
    if (inView) {
      const timer = setTimeout(() => {
        let start = 0;
        const end = value;
        const incrementTime = (duration * 1000) / Math.abs(end - start);
        const increment = Math.abs(end - start) / (duration * 60);

        const counter = setInterval(() => {
          start += increment;
          if (start >= end) {
            setCount(end);
            clearInterval(counter);
          } else {
            setCount(start);
          }
        }, incrementTime);

        return () => clearInterval(counter);
      }, animationDelay * 1000);

      return () => clearTimeout(timer);
    }
  }, [inView, value, duration, animationDelay]);

  return (
    <Typography 
      ref={ref}
      variant={variant}
      component="span"
      sx={{ 
        color: color,
        fontFeatureSettings: '"tnum"',
        fontWeight: 700,
      }}
    >
      {prefix}{count.toFixed(decimals).replace(/\B(?=(\d{3})+(?!\d))/g, ",")}{suffix}
    </Typography>
  );
};

/**
 * Progress Ring Component
 */
const ProgressRing: React.FC<ProgressRingProps> = ({
  value,
  max = 100,
  size = 120,
  strokeWidth = 8,
  color,
  backgroundColor,
  showValue = true,
  animated = true,
  gradient = true,
}) => {
  const theme = useTheme();
  const progressColor = color || theme.palette.primary.main;
  const bgColor = backgroundColor || alpha(theme.palette.divider, 0.1);
  
  const radius = (size - strokeWidth) / 2;
  const circumference = radius * 2 * Math.PI;
  const progress = (value / max) * 100;
  const strokeDasharray = circumference;
  const strokeDashoffset = circumference - (progress / 100) * circumference;

  const gradientId = `progress-gradient-${Math.random().toString(36).substr(2, 9)}`;

  return (
    <Box 
      sx={{ 
        position: 'relative', 
        width: size, 
        height: size,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        {gradient && (
          <defs>
            <linearGradient id={gradientId} x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor={progressColor} />
              <stop offset="100%" stopColor={alpha(progressColor, 0.6)} />
            </linearGradient>
          </defs>
        )}
        
        {/* Background circle */}
        <circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          stroke={bgColor}
          strokeWidth={strokeWidth}
          fill="none"
        />
        
        {/* Progress circle */}
        <motion.circle
          cx={size / 2}
          cy={size / 2}
          r={radius}
          stroke={gradient ? `url(#${gradientId})` : progressColor}
          strokeWidth={strokeWidth}
          fill="none"
          strokeLinecap="round"
          strokeDasharray={strokeDasharray}
          initial={{ strokeDashoffset: circumference }}
          animate={{ strokeDashoffset: animated ? strokeDashoffset : strokeDashoffset }}
          transition={{ duration: 1.5, ease: [0.4, 0, 0.2, 1] }}
          style={{
            filter: `drop-shadow(0 0 8px ${alpha(progressColor, 0.3)})`,
          }}
        />
      </svg>
      
      {showValue && (
        <Box
          sx={{
            position: 'absolute',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <Typography 
            variant="h6" 
            fontWeight={700}
            sx={{ 
              color: progressColor,
              fontFeatureSettings: '"tnum"',
            }}
          >
            <AnimatedCounter value={progress} suffix="%" />
          </Typography>
        </Box>
      )}
    </Box>
  );
};

/**
 * Floating Action Button with Enhanced Effects
 */
const EnhancedFloatingActionButton: React.FC<{
  icon: React.ReactNode;
  onClick?: () => void;
  tooltip?: string;
  color?: 'primary' | 'secondary' | 'success' | 'warning' | 'error';
  size?: 'small' | 'medium' | 'large';
  position?: { bottom?: number; right?: number; top?: number; left?: number };
}> = ({ 
  icon, 
  onClick, 
  tooltip, 
  color = 'primary', 
  size = 'medium',
  position = { bottom: 24, right: 24 },
}) => {
  const theme = useTheme();
  
  const getSize = () => {
    switch (size) {
      case 'small': return 48;
      case 'large': return 72;
      default: return 64;
    }
  };

  const fabSize = getSize();

  return (
    <motion.div
      whileHover={{ scale: 1.1 }}
      whileTap={{ scale: 0.9 }}
    >
      <FloatingActionButton
        onClick={onClick}
        sx={{
          ...position,
          width: fabSize,
          height: fabSize,
          background: `linear-gradient(135deg, 
            ${theme.palette[color].main} 0%, 
            ${theme.palette[color].dark} 100%
          )`,
        }}
      >
        {icon}
      </FloatingActionButton>
    </motion.div>
  );
};

export {
  PremiumKPICard,
  EnhancedSearchBar,
  AnimatedCounter,
  ProgressRing,
  EnhancedFloatingActionButton,
  GlassCard,
  PremiumButton,
  AnimatedLinearProgress,
};