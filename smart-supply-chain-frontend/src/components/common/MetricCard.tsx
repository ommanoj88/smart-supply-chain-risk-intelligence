import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Skeleton,
  useTheme,
  alpha,
  Chip,
} from '@mui/material';
import {
  TrendingFlat,
  ArrowUpward,
  ArrowDownward,
} from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';

interface MetricCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  trend?: {
    value: number;
    direction: 'up' | 'down' | 'flat';
    period?: string;
    label?: string;
  };
  status?: 'success' | 'warning' | 'error' | 'info' | 'neutral';
  icon?: React.ReactNode;
  loading?: boolean;
  onClick?: () => void;
  actions?: React.ReactNode;
  size?: 'small' | 'medium' | 'large';
  variant?: 'default' | 'premium' | 'minimal' | 'glass';
  target?: number;
  unit?: string;
  description?: string;
  colorScheme?: 'primary' | 'success' | 'warning' | 'error' | 'accent';
}

/**
 * Premium Enterprise Metric Card Component
 * Features:
 * - Sophisticated visual design with subtle animations
 * - Multiple variants for different use cases
 * - Target comparison indicators
 * - Glass morphism effects
 * - Professional typography hierarchy
 */
export const MetricCard: React.FC<MetricCardProps> = ({
  title,
  value,
  subtitle,
  trend,
  status = 'neutral',
  icon,
  loading = false,
  onClick,
  actions,
  size = 'medium',
  variant = 'default',
  target,
  unit = '',
  description,
  colorScheme = 'primary',
}) => {
  const theme = useTheme();

  const getColorScheme = () => {
    const schemes = {
      primary: {
        main: theme.palette.primary.main,
        light: theme.palette.primary.light,
        bg: alpha(theme.palette.primary.main, 0.04),
        border: alpha(theme.palette.primary.main, 0.12),
      },
      success: {
        main: theme.palette.success.main,
        light: theme.palette.success.light,
        bg: alpha(theme.palette.success.main, 0.04),
        border: alpha(theme.palette.success.main, 0.12),
      },
      warning: {
        main: theme.palette.warning.main,
        light: theme.palette.warning.light,
        bg: alpha(theme.palette.warning.main, 0.04),
        border: alpha(theme.palette.warning.main, 0.12),
      },
      error: {
        main: theme.palette.error.main,
        light: theme.palette.error.light,
        bg: alpha(theme.palette.error.main, 0.04),
        border: alpha(theme.palette.error.main, 0.12),
      },
      accent: {
        main: theme.palette.secondary.main,
        light: theme.palette.secondary.light,
        bg: alpha(theme.palette.secondary.main, 0.04),
        border: alpha(theme.palette.secondary.main, 0.12),
      },
    };
    return schemes[colorScheme];
  };

  const getTrendColor = () => {
    if (!trend) return theme.palette.text.secondary;
    switch (trend.direction) {
      case 'up': return theme.palette.success.main;
      case 'down': return theme.palette.error.main;
      case 'flat': return theme.palette.warning.main;
      default: return theme.palette.text.secondary;
    }
  };

  const getTrendIcon = () => {
    if (!trend) return null;
    switch (trend.direction) {
      case 'up': return <ArrowUpward sx={{ fontSize: 14 }} />;
      case 'down': return <ArrowDownward sx={{ fontSize: 14 }} />;
      case 'flat': return <TrendingFlat sx={{ fontSize: 14 }} />;
      default: return null;
    }
  };

  const getSizeProps = () => {
    switch (size) {
      case 'small':
        return {
          padding: 2,
          titleVariant: 'caption' as const,
          valueVariant: 'h6' as const,
          iconSize: 16,
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

  const getCardVariantStyles = () => {
    const colorScheme = getColorScheme();
    
    switch (variant) {
      case 'premium':
        return {
          background: `linear-gradient(135deg, ${colorScheme.bg} 0%, ${alpha(colorScheme.main, 0.02)} 100%)`,
          border: `1px solid ${colorScheme.border}`,
          borderLeft: `4px solid ${colorScheme.main}`,
          '&:hover': {
            transform: 'translateY(-2px)',
            boxShadow: `0 8px 32px ${alpha(colorScheme.main, 0.12)}`,
            borderColor: colorScheme.main,
          },
        };
      case 'minimal':
        return {
          background: 'transparent',
          border: 'none',
          boxShadow: 'none',
          '&:hover': {
            background: alpha(colorScheme.main, 0.02),
          },
        };
      case 'glass':
        return {
          background: alpha(theme.palette.background.paper, 0.8),
          backdropFilter: 'blur(20px)',
          border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
          '&:hover': {
            background: alpha(theme.palette.background.paper, 0.9),
            border: `1px solid ${alpha(colorScheme.main, 0.2)}`,
          },
        };
      default:
        return {
          '&:hover': {
            transform: 'translateY(-1px)',
            boxShadow: theme.shadows[4],
          },
        };
    }
  };

  const sizeProps = getSizeProps();

  if (loading) {
    return (
      <Card sx={{ height: '100%', ...getCardVariantStyles() }}>
        <CardContent sx={{ p: sizeProps.padding }}>
          <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
            <Skeleton variant="text" width="60%" height={24} />
            <Skeleton variant="circular" width={sizeProps.iconSize} height={sizeProps.iconSize} />
          </Box>
          <Skeleton variant="text" width="40%" height={40} sx={{ mb: 1 }} />
          <Skeleton variant="text" width="80%" height={20} />
        </CardContent>
      </Card>
    );
  }

  const numericValue = typeof value === 'string' ? parseFloat(value.replace(/[^0-9.-]+/g, '')) : value;
  const targetProgress = target && typeof numericValue === 'number' ? (numericValue / target) * 100 : null;

  return (
    <motion.div
      whileHover={{ scale: onClick ? 1.01 : 1 }}
      whileTap={{ scale: onClick ? 0.99 : 1 }}
      transition={{ duration: 0.2, ease: 'easeOut' }}
    >
      <Card 
        sx={{ 
          height: '100%', 
          cursor: onClick ? 'pointer' : 'default',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          position: 'relative',
          overflow: 'visible',
          ...getCardVariantStyles(),
        }}
        onClick={onClick}
      >
        <CardContent sx={{ p: sizeProps.padding, position: 'relative' }}>
          {/* Header with Icon and Actions */}
          <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
            <Box display="flex" alignItems="center" gap={1}>
              {icon && (
                <motion.div
                  initial={{ scale: 0.8, opacity: 0 }}
                  animate={{ scale: 1, opacity: 1 }}
                  transition={{ duration: 0.3, delay: 0.1 }}
                >
                  <Box 
                    sx={{ 
                      color: getColorScheme().main,
                      display: 'flex',
                      alignItems: 'center',
                      p: 1,
                      borderRadius: 2,
                      backgroundColor: getColorScheme().bg,
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
                    fontSize: size === 'small' ? '0.75rem' : '0.875rem',
                  }}
                >
                  {title}
                </Typography>
                {description && (
                  <Typography variant="caption" color="text.secondary" sx={{ opacity: 0.7 }}>
                    {description}
                  </Typography>
                )}
              </Box>
            </Box>

            {actions && (
              <Box>
                {actions}
              </Box>
            )}
          </Box>
          
          {/* Main Value Display */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, delay: 0.2 }}
          >
            <Box display="flex" alignItems="baseline" gap={0.5} mb={1}>
              <Typography 
                variant={sizeProps.valueVariant}
                component="div" 
                fontWeight={700}
                color="text.primary"
                sx={{ 
                  fontFeatureSettings: '"tnum"',
                  lineHeight: 1.1,
                }}
              >
                {value}
              </Typography>
              {unit && (
                <Typography 
                  variant="body2" 
                  color="text.secondary" 
                  sx={{ fontWeight: 500, ml: 0.5 }}
                >
                  {unit}
                </Typography>
              )}
            </Box>
          </motion.div>

          {/* Target Progress Bar */}
          {target && targetProgress !== null && (
            <Box mb={2}>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={0.5}>
                <Typography variant="caption" color="text.secondary">
                  Target: {target}{unit}
                </Typography>
                <Typography 
                  variant="caption" 
                  sx={{ 
                    color: targetProgress >= 100 ? theme.palette.success.main : theme.palette.warning.main,
                    fontWeight: 600,
                  }}
                >
                  {targetProgress.toFixed(1)}%
                </Typography>
              </Box>
              <Box 
                sx={{ 
                  height: 4, 
                  backgroundColor: alpha(getColorScheme().main, 0.1),
                  borderRadius: 2,
                  overflow: 'hidden',
                }}
              >
                <motion.div
                  initial={{ width: 0 }}
                  animate={{ width: `${Math.min(targetProgress, 100)}%` }}
                  transition={{ duration: 0.6, delay: 0.3 }}
                  style={{
                    height: '100%',
                    backgroundColor: targetProgress >= 100 ? theme.palette.success.main : getColorScheme().main,
                    borderRadius: 2,
                  }}
                />
              </Box>
            </Box>
          )}

          {/* Subtitle */}
          {subtitle && (
            <Typography variant="body2" color="text.secondary" mb={1.5} sx={{ opacity: 0.8 }}>
              {subtitle}
            </Typography>
          )}

          {/* Trend Indicator */}
          <AnimatePresence>
            {trend && (
              <motion.div
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                transition={{ duration: 0.3 }}
              >
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Chip
                    icon={getTrendIcon() || undefined}
                    label={
                      <Box display="flex" alignItems="center" gap={0.5}>
                        <Typography variant="caption" fontWeight={600}>
                          {Math.abs(trend.value)}%
                        </Typography>
                        {trend.label && (
                          <Typography variant="caption" sx={{ opacity: 0.8 }}>
                            {trend.label}
                          </Typography>
                        )}
                      </Box>
                    }
                    size="small"
                    sx={{
                      backgroundColor: alpha(getTrendColor(), 0.1),
                      color: getTrendColor(),
                      border: `1px solid ${alpha(getTrendColor(), 0.2)}`,
                      '& .MuiChip-icon': {
                        color: getTrendColor(),
                      },
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
          </AnimatePresence>
        </CardContent>
      </Card>
    </motion.div>
  );
};

export default MetricCard;