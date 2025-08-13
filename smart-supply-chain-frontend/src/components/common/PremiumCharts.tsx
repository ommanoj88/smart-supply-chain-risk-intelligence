import React, { useMemo, useRef, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Skeleton,
  useTheme,
  alpha,
  Paper,
} from '@mui/material';
import { motion } from 'framer-motion';
import * as d3 from 'd3';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip as RechartsTooltip,
  Legend,
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
} from 'recharts';

interface ChartData {
  [key: string]: any;
}

interface BasePremiumChartProps {
  data: ChartData[];
  title?: string;
  subtitle?: string;
  height?: number;
  loading?: boolean;
  colors?: string[];
  showLegend?: boolean;
  showGrid?: boolean;
  variant?: 'default' | 'glass' | 'premium' | 'minimal';
  animation?: boolean;
  interactive?: boolean;
}

interface PremiumAreaChartProps extends BasePremiumChartProps {
  xKey: string;
  yKey: string;
  gradient?: boolean;
  strokeWidth?: number;
  fillOpacity?: number;
}

interface PremiumBarChartProps extends BasePremiumChartProps {
  xKey: string;
  yKey: string;
  horizontal?: boolean;
  rounded?: boolean;
  gradient?: boolean;
}

interface PremiumLineChartProps extends BasePremiumChartProps {
  xKey: string;
  yKeys: string[];
  strokeWidth?: number;
  curved?: boolean;
  dots?: boolean;
}

interface PremiumPieChartProps extends BasePremiumChartProps {
  dataKey: string;
  nameKey: string;
  showLabels?: boolean;
  innerRadius?: number;
  donut?: boolean;
  gradient3D?: boolean;
}

interface D3RadialProgressProps {
  value: number;
  max: number;
  title: string;
  subtitle?: string;
  height?: number;
  color?: string;
  gradient?: boolean;
  animation?: boolean;
}

/**
 * Premium Chart Wrapper with Glass Morphism and Advanced Styling
 */
const PremiumChartWrapper: React.FC<{
  title?: string;
  subtitle?: string;
  loading?: boolean;
  height: number;
  variant?: 'default' | 'glass' | 'premium' | 'minimal';
  children: React.ReactNode;
}> = ({ title, subtitle, loading, height, variant = 'premium', children }) => {
  const theme = useTheme();

  const getVariantStyles = () => {
    switch (variant) {
      case 'glass':
        return {
          background: `linear-gradient(145deg, 
            ${alpha(theme.palette.background.paper, 0.8)} 0%, 
            ${alpha(theme.palette.background.paper, 0.4)} 100%
          )`,
          backdropFilter: 'blur(20px) saturate(180%)',
          border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
          borderRadius: '16px',
          boxShadow: `
            0 8px 32px ${alpha(theme.palette.common.black, 0.1)},
            0 1px 0 ${alpha(theme.palette.common.white, 0.1)} inset
          `,
        };
      case 'premium':
        return {
          background: `linear-gradient(145deg, 
            ${theme.palette.background.paper} 0%, 
            ${alpha(theme.palette.primary.main, 0.02)} 100%
          )`,
          border: `1px solid ${alpha(theme.palette.primary.main, 0.08)}`,
          borderRadius: '20px',
          boxShadow: `
            0 4px 20px ${alpha(theme.palette.primary.main, 0.08)},
            0 8px 40px ${alpha(theme.palette.common.black, 0.04)}
          `,
          '&:hover': {
            transform: 'translateY(-4px)',
            boxShadow: `
              0 8px 40px ${alpha(theme.palette.primary.main, 0.12)},
              0 16px 60px ${alpha(theme.palette.common.black, 0.06)}
            `,
          },
        };
      case 'minimal':
        return {
          background: 'transparent',
          border: 'none',
          borderRadius: '12px',
          boxShadow: 'none',
        };
      default:
        return {
          borderRadius: '12px',
          boxShadow: theme.shadows[2],
        };
    }
  };

  if (loading) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
      >
        <Card sx={{ height: height + (title ? 80 : 0), ...getVariantStyles() }}>
          <CardContent sx={{ p: 3, height: '100%' }}>
            {title && (
              <Box mb={3}>
                <Skeleton variant="text" width="60%" height={32} sx={{ mb: 1 }} />
                {subtitle && <Skeleton variant="text" width="40%" height={20} />}
              </Box>
            )}
            <Skeleton variant="rectangular" width="100%" height={height - (title ? 120 : 60)} />
          </CardContent>
        </Card>
      </motion.div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6, ease: [0.4, 0, 0.2, 1] }}
      whileHover={{ y: variant === 'premium' ? -4 : 0 }}
    >
      <Card 
        sx={{ 
          height: height + (title ? 80 : 0), 
          transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
          ...getVariantStyles(),
        }}
      >
        <CardContent sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column' }}>
          {title && (
            <motion.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.4, delay: 0.2 }}
            >
              <Box mb={3}>
                <Typography 
                  variant="h6" 
                  component="h3" 
                  fontWeight={700}
                  sx={{ 
                    background: `linear-gradient(135deg, ${theme.palette.text.primary} 0%, ${alpha(theme.palette.primary.main, 0.8)} 100%)`,
                    backgroundClip: 'text',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    mb: 0.5,
                  }}
                >
                  {title}
                </Typography>
                {subtitle && (
                  <Typography 
                    variant="body2" 
                    color="text.secondary" 
                    sx={{ opacity: 0.8, fontWeight: 500 }}
                  >
                    {subtitle}
                  </Typography>
                )}
              </Box>
            </motion.div>
          )}
          <Box sx={{ flex: 1, position: 'relative' }}>
            {children}
          </Box>
        </CardContent>
      </Card>
    </motion.div>
  );
};

/**
 * Custom Enhanced Tooltip Component
 */
const CustomTooltip: React.FC<any> = ({ active, payload, label }) => {
  const theme = useTheme();

  if (active && payload && payload.length) {
    return (
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.2 }}
      >
        <Paper
          sx={{
            p: 2,
            background: `linear-gradient(145deg, 
              ${alpha(theme.palette.background.paper, 0.95)} 0%, 
              ${alpha(theme.palette.background.paper, 0.8)} 100%
            )`,
            backdropFilter: 'blur(20px)',
            border: `1px solid ${alpha(theme.palette.divider, 0.1)}`,
            borderRadius: '12px',
            boxShadow: `0 8px 32px ${alpha(theme.palette.common.black, 0.1)}`,
            minWidth: 160,
          }}
        >
          <Typography variant="subtitle2" fontWeight={600} sx={{ mb: 1 }}>
            {label}
          </Typography>
          {payload.map((entry: any, index: number) => (
            <Box key={index} display="flex" alignItems="center" gap={1} mb={0.5}>
              <Box
                sx={{
                  width: 8,
                  height: 8,
                  borderRadius: '50%',
                  backgroundColor: entry.color,
                }}
              />
              <Typography variant="caption" color="text.secondary" sx={{ flex: 1 }}>
                {entry.name}:
              </Typography>
              <Typography variant="caption" fontWeight={600}>
                {typeof entry.value === 'number' ? entry.value.toLocaleString() : entry.value}
              </Typography>
            </Box>
          ))}
        </Paper>
      </motion.div>
    );
  }

  return null;
};

/**
 * Premium Area Chart with Gradients and Animations
 */
const PremiumAreaChart: React.FC<PremiumAreaChartProps> = ({
  data,
  title,
  subtitle,
  height = 300,
  loading = false,
  xKey,
  yKey,
  gradient = true,
  strokeWidth = 3,
  fillOpacity = 0.3,
  colors,
  showGrid = true,
  variant = 'premium',
  animation = true,
}) => {
  const theme = useTheme();
  const chartId = useMemo(() => `area-gradient-${Math.random().toString(36).substr(2, 9)}`, []);
  
  const defaultColors = [
    theme.palette.primary.main,
    theme.palette.secondary.main,
    theme.palette.success.main,
  ];
  const chartColors = colors || defaultColors;

  return (
    <PremiumChartWrapper 
      title={title} 
      subtitle={subtitle}
      loading={loading} 
      height={height} 
      variant={variant}
    >
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data} margin={{ top: 10, right: 10, left: 10, bottom: 10 }}>
          <defs>
            <linearGradient id={chartId} x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={chartColors[0]} stopOpacity={fillOpacity} />
              <stop offset="100%" stopColor={chartColors[0]} stopOpacity={0} />
            </linearGradient>
          </defs>
          {showGrid && (
            <CartesianGrid 
              strokeDasharray="3 3" 
              stroke={alpha(theme.palette.divider, 0.2)}
              horizontal={true}
              vertical={false}
            />
          )}
          <XAxis 
            dataKey={xKey}
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
            dy={10}
          />
          <YAxis 
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
            dx={-10}
          />
          <RechartsTooltip content={<CustomTooltip />} />
          <Area
            type="monotone"
            dataKey={yKey}
            stroke={chartColors[0]}
            strokeWidth={strokeWidth}
            fill={gradient ? `url(#${chartId})` : alpha(chartColors[0], fillOpacity)}
            isAnimationActive={animation}
            animationDuration={1000}
            animationEasing="ease-out"
          />
        </AreaChart>
      </ResponsiveContainer>
    </PremiumChartWrapper>
  );
};

/**
 * Premium Bar Chart with Rounded Corners and Gradients
 */
const PremiumBarChart: React.FC<PremiumBarChartProps> = ({
  data,
  title,
  subtitle,
  height = 300,
  loading = false,
  xKey,
  yKey,
  horizontal = false,
  rounded = true,
  gradient = true,
  colors,
  showGrid = true,
  variant = 'premium',
  animation = true,
}) => {
  const theme = useTheme();
  const chartId = useMemo(() => `bar-gradient-${Math.random().toString(36).substr(2, 9)}`, []);
  
  const defaultColors = [
    theme.palette.primary.main,
    theme.palette.secondary.main,
    theme.palette.success.main,
  ];
  const chartColors = colors || defaultColors;

  return (
    <PremiumChartWrapper 
      title={title} 
      subtitle={subtitle}
      loading={loading} 
      height={height} 
      variant={variant}
    >
      <ResponsiveContainer width="100%" height="100%">
        <BarChart 
          data={data} 
          layout={horizontal ? 'vertical' : 'horizontal'}
          margin={{ top: 10, right: 10, left: 10, bottom: 10 }}
        >
          <defs>
            <linearGradient id={chartId} x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={chartColors[0]} stopOpacity={1} />
              <stop offset="100%" stopColor={alpha(chartColors[0], 0.6)} stopOpacity={1} />
            </linearGradient>
          </defs>
          {showGrid && (
            <CartesianGrid 
              strokeDasharray="3 3" 
              stroke={alpha(theme.palette.divider, 0.2)}
            />
          )}
          <XAxis 
            type={horizontal ? 'number' : 'category'}
            dataKey={horizontal ? undefined : xKey}
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
          />
          <YAxis 
            type={horizontal ? 'category' : 'number'}
            dataKey={horizontal ? xKey : undefined}
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
          />
          <RechartsTooltip content={<CustomTooltip />} />
          <Bar
            dataKey={yKey}
            fill={gradient ? `url(#${chartId})` : chartColors[0]}
            radius={rounded ? [4, 4, 0, 0] : [0, 0, 0, 0]}
            isAnimationActive={animation}
            animationDuration={800}
            animationEasing="ease-out"
          />
        </BarChart>
      </ResponsiveContainer>
    </PremiumChartWrapper>
  );
};

/**
 * Premium Multi-Line Chart with Enhanced Styling
 */
const PremiumLineChart: React.FC<PremiumLineChartProps> = ({
  data,
  title,
  subtitle,
  height = 300,
  loading = false,
  xKey,
  yKeys,
  strokeWidth = 3,
  curved = true,
  dots = true,
  colors,
  showGrid = true,
  showLegend = true,
  variant = 'premium',
  animation = true,
}) => {
  const theme = useTheme();
  
  const defaultColors = [
    theme.palette.primary.main,
    theme.palette.secondary.main,
    theme.palette.success.main,
    theme.palette.warning.main,
    theme.palette.error.main,
  ];
  const chartColors = colors || defaultColors;

  return (
    <PremiumChartWrapper 
      title={title} 
      subtitle={subtitle}
      loading={loading} 
      height={height} 
      variant={variant}
    >
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data} margin={{ top: 10, right: 10, left: 10, bottom: 10 }}>
          {showGrid && (
            <CartesianGrid 
              strokeDasharray="3 3" 
              stroke={alpha(theme.palette.divider, 0.2)}
              horizontal={true}
              vertical={false}
            />
          )}
          <XAxis 
            dataKey={xKey}
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
            dy={10}
          />
          <YAxis 
            axisLine={false}
            tickLine={false}
            tick={{ fontSize: 12, fill: theme.palette.text.secondary }}
            dx={-10}
          />
          <RechartsTooltip content={<CustomTooltip />} />
          {showLegend && (
            <Legend 
              wrapperStyle={{
                paddingTop: '20px',
                fontSize: '12px',
                color: theme.palette.text.secondary,
              }}
            />
          )}
          {yKeys.map((key, index) => (
            <Line
              key={key}
              type={curved ? "monotone" : "linear"}
              dataKey={key}
              stroke={chartColors[index % chartColors.length]}
              strokeWidth={strokeWidth}
              dot={dots ? { r: 4, strokeWidth: 2 } : false}
              activeDot={{ r: 6, stroke: chartColors[index % chartColors.length], strokeWidth: 2 }}
              isAnimationActive={animation}
              animationDuration={1000}
              animationEasing="ease-out"
            />
          ))}
        </LineChart>
      </ResponsiveContainer>
    </PremiumChartWrapper>
  );
};

/**
 * Premium Pie Chart with 3D Effects and Gradients
 */
const PremiumPieChart: React.FC<PremiumPieChartProps> = ({
  data,
  title,
  subtitle,
  height = 300,
  loading = false,
  dataKey,
  // nameKey, // Currently unused
  showLabels = true,
  innerRadius = 0,
  donut = false,
  gradient3D = true,
  colors,
  variant = 'premium',
  animation = true,
}) => {
  const theme = useTheme();
  
  const defaultColors = [
    theme.palette.primary.main,
    theme.palette.secondary.main,
    theme.palette.success.main,
    theme.palette.warning.main,
    theme.palette.error.main,
    theme.palette.info.main,
  ];
  const chartColors = colors || defaultColors;

  const renderLabel = (entry: any) => {
    const percent = ((entry.value / data.reduce((sum, item) => sum + item[dataKey], 0)) * 100).toFixed(1);
    return showLabels ? `${percent}%` : '';
  };

  return (
    <PremiumChartWrapper 
      title={title} 
      subtitle={subtitle}
      loading={loading} 
      height={height} 
      variant={variant}
    >
      <ResponsiveContainer width="100%" height="100%">
        <PieChart margin={{ top: 10, right: 10, left: 10, bottom: 10 }}>
          <defs>
            {gradient3D && chartColors.map((color, index) => (
              <linearGradient key={`gradient-${index}`} id={`gradient-${index}`} x1="0" y1="0" x2="1" y2="1">
                <stop offset="0%" stopColor={color} stopOpacity={1} />
                <stop offset="100%" stopColor={alpha(color, 0.7)} stopOpacity={1} />
              </linearGradient>
            ))}
          </defs>
          <Pie
            data={data}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={renderLabel}
            outerRadius={Math.min(height / 2 - 40, 120)}
            innerRadius={donut ? innerRadius || Math.min(height / 4 - 20, 60) : innerRadius}
            fill="#8884d8"
            dataKey={dataKey}
            isAnimationActive={animation}
            animationDuration={1000}
            animationEasing="ease-out"
          >
            {data.map((_, index) => (
              <Cell 
                key={`cell-${index}`} 
                fill={gradient3D ? `url(#gradient-${index % chartColors.length})` : chartColors[index % chartColors.length]}
                stroke={alpha(theme.palette.background.paper, 0.2)}
                strokeWidth={2}
              />
            ))}
          </Pie>
          <RechartsTooltip content={<CustomTooltip />} />
        </PieChart>
      </ResponsiveContainer>
    </PremiumChartWrapper>
  );
};

/**
 * D3-Powered Radial Progress Component
 */
const D3RadialProgress: React.FC<D3RadialProgressProps> = ({
  value,
  max,
  title,
  subtitle,
  height = 200,
  color,
  gradient = true,
  animation = true,
}) => {
  const theme = useTheme();
  const svgRef = useRef<SVGSVGElement>(null);
  const progressColor = color || theme.palette.primary.main;

  useEffect(() => {
    if (!svgRef.current) return;

    const svg = d3.select(svgRef.current);
    svg.selectAll("*").remove();

    const width = height;
    const radius = Math.min(width, height) / 2 - 20;
    const innerRadius = radius * 0.7;

    const g = svg
      .attr("width", width)
      .attr("height", height)
      .append("g")
      .attr("transform", `translate(${width / 2}, ${height / 2})`);

    // Background arc
    const backgroundArc = d3
      .arc()
      .innerRadius(innerRadius)
      .outerRadius(radius)
      .startAngle(0)
      .endAngle(Math.PI * 2);

    // Progress arc
    const progressArc = d3
      .arc()
      .innerRadius(innerRadius)
      .outerRadius(radius)
      .startAngle(0);

    // Add gradient
    if (gradient) {
      const defs = svg.append("defs");
      const gradientId = `radial-gradient-${Math.random().toString(36).substr(2, 9)}`;
      
      const gradientDef = defs
        .append("radialGradient")
        .attr("id", gradientId)
        .attr("cx", "50%")
        .attr("cy", "50%");

      gradientDef
        .append("stop")
        .attr("offset", "0%")
        .attr("stop-color", progressColor)
        .attr("stop-opacity", 1);

      gradientDef
        .append("stop")
        .attr("offset", "100%")
        .attr("stop-color", alpha(progressColor, 0.6))
        .attr("stop-opacity", 1);
    }

    // Background
    g.append("path")
      .datum({ endAngle: Math.PI * 2 })
      .style("fill", alpha(theme.palette.divider, 0.1))
      .attr("d", backgroundArc as any);

    // Progress
    const progressPath = g
      .append("path")
      .datum({ endAngle: 0 })
      .style("fill", gradient ? `url(#radial-gradient-${Math.random().toString(36).substr(2, 9)})` : progressColor)
      .attr("d", progressArc as any);

    // Animation
    if (animation) {
      progressPath
        .transition()
        .duration(1000)
        .ease(d3.easeElastic.period(0.5))
        .attrTween("d", function(d: any) {
          const interpolate = d3.interpolate(d.endAngle, (value / max) * Math.PI * 2);
          return function(t: number) {
            d.endAngle = interpolate(t);
            return progressArc(d) || "";
          };
        });
    } else {
      progressPath
        .datum({ endAngle: (value / max) * Math.PI * 2 })
        .attr("d", progressArc as any);
    }

    // Center text
    const centerText = g.append("g");
    
    centerText
      .append("text")
      .attr("text-anchor", "middle")
      .attr("dy", "-0.5em")
      .style("font-size", "1.5rem")
      .style("font-weight", "700")
      .style("fill", theme.palette.text.primary)
      .text(`${Math.round((value / max) * 100)}%`);

    centerText
      .append("text")
      .attr("text-anchor", "middle")
      .attr("dy", "1em")
      .style("font-size", "0.75rem")
      .style("fill", theme.palette.text.secondary)
      .text(title);

  }, [value, max, title, height, progressColor, gradient, animation, theme]);

  return (
    <Box 
      display="flex" 
      flexDirection="column" 
      alignItems="center" 
      sx={{ height, justifyContent: 'center' }}
    >
      <svg ref={svgRef} />
      {subtitle && (
        <Typography 
          variant="caption" 
          color="text.secondary" 
          align="center" 
          sx={{ mt: 1, opacity: 0.8 }}
        >
          {subtitle}
        </Typography>
      )}
    </Box>
  );
};

export { PremiumAreaChart, PremiumBarChart, PremiumLineChart, PremiumPieChart, D3RadialProgress };