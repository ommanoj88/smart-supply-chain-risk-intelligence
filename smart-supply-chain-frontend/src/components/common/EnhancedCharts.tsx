import React from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Skeleton,
  useTheme,
} from '@mui/material';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import { motion } from 'framer-motion';

interface ChartData {
  [key: string]: any;
}

interface BaseChartProps {
  data: ChartData[];
  title?: string;
  height?: number;
  loading?: boolean;
  colors?: string[];
  showLegend?: boolean;
  showGrid?: boolean;
}

interface AreaChartProps extends BaseChartProps {
  xKey: string;
  yKey: string;
  gradient?: boolean;
}

interface BarChartProps extends BaseChartProps {
  xKey: string;
  yKey: string;
  horizontal?: boolean;
}

interface LineChartProps extends BaseChartProps {
  xKey: string;
  yKeys: string[];
  strokeWidth?: number;
}

interface PieChartProps extends BaseChartProps {
  dataKey: string;
  nameKey: string;
  showLabels?: boolean;
  innerRadius?: number;
}

const ChartWrapper: React.FC<{
  title?: string;
  loading?: boolean;
  height: number;
  children: React.ReactNode;
}> = ({ title, loading, height, children }) => {
  if (loading) {
    return (
      <Card sx={{ height: height + (title ? 60 : 0) }}>
        {title && (
          <CardContent sx={{ pb: 1 }}>
            <Skeleton variant="text" width="40%" height={28} />
          </CardContent>
        )}
        <Box sx={{ p: 2, pt: title ? 0 : 2 }}>
          <Skeleton variant="rectangular" width="100%" height={height - 40} />
        </Box>
      </Card>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.3 }}
    >
      <Card sx={{ height: height + (title ? 60 : 0) }}>
        {title && (
          <CardContent sx={{ pb: 1 }}>
            <Typography variant="h6" component="h3" fontWeight={600}>
              {title}
            </Typography>
          </CardContent>
        )}
        <Box sx={{ p: 2, pt: title ? 0 : 2, height: height }}>
          {children}
        </Box>
      </Card>
    </motion.div>
  );
};

/**
 * Enhanced Area Chart Component
 */
export const EnhancedAreaChart: React.FC<AreaChartProps> = ({
  data,
  title,
  height = 300,
  loading = false,
  xKey,
  yKey,
  colors = ['#8884d8'],
  gradient = true,
  showLegend = false,
  showGrid = true,
}) => {
  const theme = useTheme();

  return (
    <ChartWrapper title={title} loading={loading} height={height}>
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
          {gradient && (
            <defs>
              <linearGradient id="colorArea" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor={colors[0]} stopOpacity={0.8} />
                <stop offset="95%" stopColor={colors[0]} stopOpacity={0.1} />
              </linearGradient>
            </defs>
          )}
          {showGrid && <CartesianGrid strokeDasharray="3 3" />}
          <XAxis 
            dataKey={xKey} 
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
          />
          <YAxis 
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: theme.palette.background.paper,
              border: `1px solid ${theme.palette.divider}`,
              borderRadius: theme.shape.borderRadius,
              color: theme.palette.text.primary,
            }}
          />
          {showLegend && <Legend />}
          <Area
            type="monotone"
            dataKey={yKey}
            stroke={colors[0]}
            fill={gradient ? 'url(#colorArea)' : colors[0]}
            strokeWidth={2}
          />
        </AreaChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

/**
 * Enhanced Bar Chart Component
 */
export const EnhancedBarChart: React.FC<BarChartProps> = ({
  data,
  title,
  height = 300,
  loading = false,
  xKey,
  yKey,
  colors = ['#8884d8'],
  horizontal = false,
  showLegend = false,
  showGrid = true,
}) => {
  const theme = useTheme();

  return (
    <ChartWrapper title={title} loading={loading} height={height}>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart 
          data={data} 
          margin={{ top: 10, right: 30, left: 0, bottom: 0 }}
        >
          {showGrid && <CartesianGrid strokeDasharray="3 3" />}
          <XAxis 
            dataKey={horizontal ? yKey : xKey}
            type={horizontal ? 'number' : 'category'}
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
          />
          <YAxis 
            dataKey={horizontal ? xKey : yKey}
            type={horizontal ? 'category' : 'number'}
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: theme.palette.background.paper,
              border: `1px solid ${theme.palette.divider}`,
              borderRadius: theme.shape.borderRadius,
              color: theme.palette.text.primary,
            }}
          />
          {showLegend && <Legend />}
          <Bar 
            dataKey={yKey} 
            fill={colors[0]}
            radius={[2, 2, 0, 0]}
          />
        </BarChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

/**
 * Enhanced Line Chart Component
 */
export const EnhancedLineChart: React.FC<LineChartProps> = ({
  data,
  title,
  height = 300,
  loading = false,
  xKey,
  yKeys,
  colors = ['#8884d8', '#82ca9d', '#ffc658'],
  strokeWidth = 2,
  showLegend = true,
  showGrid = true,
}) => {
  const theme = useTheme();

  return (
    <ChartWrapper title={title} loading={loading} height={height}>
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
          {showGrid && <CartesianGrid strokeDasharray="3 3" />}
          <XAxis 
            dataKey={xKey}
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
          />
          <YAxis 
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: theme.palette.background.paper,
              border: `1px solid ${theme.palette.divider}`,
              borderRadius: theme.shape.borderRadius,
              color: theme.palette.text.primary,
            }}
          />
          {showLegend && <Legend />}
          {yKeys.map((key, index) => (
            <Line
              key={key}
              type="monotone"
              dataKey={key}
              stroke={colors[index % colors.length]}
              strokeWidth={strokeWidth}
              dot={{ r: 4 }}
              activeDot={{ r: 6 }}
            />
          ))}
        </LineChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

/**
 * Enhanced Pie Chart Component
 */
export const EnhancedPieChart: React.FC<PieChartProps> = ({
  data,
  title,
  height = 300,
  loading = false,
  dataKey,
  nameKey,
  colors = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8'],
  showLabels = true,
  showLegend = true,
  innerRadius = 0,
}) => {
  const theme = useTheme();

  const renderLabel = (entry: any) => {
    if (!showLabels) return '';
    const percent = ((entry.value / data.reduce((sum, item) => sum + item[dataKey], 0)) * 100).toFixed(1);
    return `${percent}%`;
  };

  return (
    <ChartWrapper title={title} loading={loading} height={height}>
      <ResponsiveContainer width="100%" height="100%">
        <PieChart>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={renderLabel}
            outerRadius={Math.min(height, 300) / 3}
            innerRadius={innerRadius}
            fill="#8884d8"
            dataKey={dataKey}
            nameKey={nameKey}
          >
            {data.map((_, index) => (
              <Cell key={`cell-${index}`} fill={colors[index % colors.length]} />
            ))}
          </Pie>
          <Tooltip
            contentStyle={{
              backgroundColor: theme.palette.background.paper,
              border: `1px solid ${theme.palette.divider}`,
              borderRadius: theme.shape.borderRadius,
              color: theme.palette.text.primary,
            }}
          />
          {showLegend && <Legend />}
        </PieChart>
      </ResponsiveContainer>
    </ChartWrapper>
  );
};

const EnhancedCharts = {
  EnhancedAreaChart,
  EnhancedBarChart,
  EnhancedLineChart,
  EnhancedPieChart,
};

export default EnhancedCharts;