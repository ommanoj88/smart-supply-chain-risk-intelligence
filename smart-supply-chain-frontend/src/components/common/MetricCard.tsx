import React from 'react';
import {
  Card,
  CardContent,
  Typography,
  Box,
  Skeleton,
  useTheme,
} from '@mui/material';
import {
  TrendingUp,
  TrendingDown,
} from '@mui/icons-material';
import { motion, AnimatePresence } from 'framer-motion';

interface MetricCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  trend?: {
    value: number;
    direction: 'up' | 'down';
    period?: string;
  };
  status?: 'success' | 'warning' | 'error' | 'info';
  icon?: React.ReactNode;
  loading?: boolean;
  onClick?: () => void;
  actions?: React.ReactNode;
}

/**
 * Enhanced metric card component with animations and trend indicators
 */
export const MetricCard: React.FC<MetricCardProps> = ({
  title,
  value,
  subtitle,
  trend,
  status,
  icon,
  loading = false,
  onClick,
  actions,
}) => {
  const theme = useTheme();

  const getStatusColor = () => {
    switch (status) {
      case 'success': return theme.palette.success.main;
      case 'warning': return theme.palette.warning.main;
      case 'error': return theme.palette.error.main;
      case 'info': return theme.palette.info.main;
      default: return theme.palette.primary.main;
    }
  };

  const getTrendColor = () => {
    if (!trend) return theme.palette.text.secondary;
    return trend.direction === 'up' 
      ? theme.palette.success.main 
      : theme.palette.error.main;
  };

  if (loading) {
    return (
      <Card sx={{ height: '100%', cursor: onClick ? 'pointer' : 'default' }}>
        <CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
            <Skeleton variant="text" width="60%" height={24} />
            <Skeleton variant="circular" width={24} height={24} />
          </Box>
          <Skeleton variant="text" width="40%" height={40} sx={{ mb: 1 }} />
          <Skeleton variant="text" width="80%" height={20} />
        </CardContent>
      </Card>
    );
  }

  return (
    <motion.div
      whileHover={{ scale: onClick ? 1.02 : 1 }}
      whileTap={{ scale: onClick ? 0.98 : 1 }}
      transition={{ duration: 0.2 }}
    >
      <Card 
        sx={{ 
          height: '100%', 
          cursor: onClick ? 'pointer' : 'default',
          borderLeft: `4px solid ${getStatusColor()}`,
          '&:hover': onClick ? {
            boxShadow: theme.shadows[8],
          } : {},
        }}
        onClick={onClick}
      >
        <CardContent>
          <Box display="flex" justifyContent="space-between" alignItems="flex-start">
            <Box flex={1}>
              <Box display="flex" alignItems="center" gap={1} mb={1}>
                {icon && (
                  <Box color={getStatusColor()} display="flex" alignItems="center">
                    {icon}
                  </Box>
                )}
                <Typography 
                  variant="subtitle2" 
                  color="text.secondary"
                  fontWeight={600}
                >
                  {title}
                </Typography>
              </Box>
              
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3 }}
              >
                <Typography 
                  variant="h4" 
                  component="div" 
                  fontWeight={700}
                  color="text.primary"
                  mb={1}
                >
                  {value}
                </Typography>
              </motion.div>

              {subtitle && (
                <Typography variant="body2" color="text.secondary" mb={1}>
                  {subtitle}
                </Typography>
              )}

              <AnimatePresence>
                {trend && (
                  <motion.div
                    initial={{ opacity: 0, x: -20 }}
                    animate={{ opacity: 1, x: 0 }}
                    exit={{ opacity: 0, x: -20 }}
                    transition={{ duration: 0.2 }}
                  >
                    <Box display="flex" alignItems="center" gap={0.5}>
                      {trend.direction === 'up' ? (
                        <TrendingUp sx={{ color: getTrendColor(), fontSize: 16 }} />
                      ) : (
                        <TrendingDown sx={{ color: getTrendColor(), fontSize: 16 }} />
                      )}
                      <Typography 
                        variant="caption" 
                        sx={{ color: getTrendColor(), fontWeight: 600 }}
                      >
                        {Math.abs(trend.value)}%
                      </Typography>
                      {trend.period && (
                        <Typography variant="caption" color="text.secondary">
                          {trend.period}
                        </Typography>
                      )}
                    </Box>
                  </motion.div>
                )}
              </AnimatePresence>
            </Box>

            {actions && (
              <Box>
                {actions}
              </Box>
            )}
          </Box>
        </CardContent>
      </Card>
    </motion.div>
  );
};

export default MetricCard;