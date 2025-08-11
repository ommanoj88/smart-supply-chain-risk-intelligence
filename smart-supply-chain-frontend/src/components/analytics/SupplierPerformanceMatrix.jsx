import React, { useState, useEffect, useRef } from 'react';
import * as d3 from 'd3';
import { Card, CardContent, Typography, Box, Grid, Chip } from '@mui/material';
import mlService from '../../services/mlService';

/**
 * Supplier Performance Matrix using D3.js
 * Shows supplier performance vs risk score with interactive bubbles
 */
const SupplierPerformanceMatrix = ({ height = 500 }) => {
  const svgRef = useRef();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedSupplier, setSelectedSupplier] = useState(null);

  useEffect(() => {
    loadSupplierData();
  }, []);

  const loadSupplierData = async () => {
    try {
      setLoading(true);
      // This would call a real API endpoint
      // const supplierData = await mlService.getSupplierPerformanceMatrix();
      setData(generateFallbackData());
    } catch (err) {
      console.error('Error loading supplier data:', err);
      setData(generateFallbackData());
    } finally {
      setLoading(false);
    }
  };

  const generateFallbackData = () => {
    const suppliers = [
      'Acme Manufacturing', 'Global Tech Solutions', 'Premier Components',
      'Advanced Materials Co', 'Precision Parts Inc', 'Quality Supplies Ltd',
      'Reliable Logistics', 'TechCorp Industries', 'Innovative Systems',
      'Superior Components', 'Elite Manufacturing', 'Prime Suppliers',
      'Excellence Corp', 'Trusted Partners', 'Professional Services',
      'Dynamic Solutions', 'Strategic Suppliers', 'Benchmark Industries',
      'Platinum Services', 'Gold Standard Co'
    ];

    return suppliers.map((name, index) => ({
      id: index + 1,
      name,
      performanceScore: 60 + Math.random() * 35, // 60-95
      riskScore: 10 + Math.random() * 80, // 10-90
      orderVolume: 50000 + Math.random() * 500000, // 50k-550k
      onTimeDelivery: 70 + Math.random() * 28, // 70-98%
      qualityScore: 6 + Math.random() * 3.5, // 6-9.5
      category: ['Electronics', 'Materials', 'Components', 'Logistics', 'Software'][Math.floor(Math.random() * 5)],
      region: ['North America', 'Asia', 'Europe', 'South America'][Math.floor(Math.random() * 4)],
      partnershipYears: 1 + Math.floor(Math.random() * 10)
    }));
  };

  useEffect(() => {
    if (!data.length || loading) return;

    const svg = d3.select(svgRef.current);
    svg.selectAll("*").remove();

    const margin = { top: 60, right: 100, bottom: 60, left: 80 };
    const width = svg.node().getBoundingClientRect().width - margin.left - margin.right;
    const chartHeight = height - margin.top - margin.bottom;

    const g = svg.append("g")
      .attr("transform", `translate(${margin.left},${margin.top})`);

    // Scales
    const xScale = d3.scaleLinear()
      .domain([0, 100])
      .range([0, width]);

    const yScale = d3.scaleLinear()
      .domain([0, 100])
      .range([chartHeight, 0]);

    const sizeScale = d3.scaleSqrt()
      .domain(d3.extent(data, d => d.orderVolume))
      .range([8, 30]);

    const colorScale = d3.scaleOrdinal()
      .domain(['Electronics', 'Materials', 'Components', 'Logistics', 'Software'])
      .range(['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd']);

    // Create quadrant backgrounds
    const quadrants = [
      { x: 0, y: 0, width: width/2, height: chartHeight/2, 
        label: 'High Risk\nLow Performance', color: '#ffebee' },
      { x: width/2, y: 0, width: width/2, height: chartHeight/2, 
        label: 'Low Risk\nLow Performance', color: '#fff3e0' },
      { x: 0, y: chartHeight/2, width: width/2, height: chartHeight/2, 
        label: 'High Risk\nHigh Performance', color: '#f3e5f5' },
      { x: width/2, y: chartHeight/2, width: width/2, height: chartHeight/2, 
        label: 'Low Risk\nHigh Performance', color: '#e8f5e8' }
    ];

    g.selectAll('.quadrant')
      .data(quadrants)
      .enter().append('rect')
      .attr('class', 'quadrant')
      .attr('x', d => d.x)
      .attr('y', d => d.y)
      .attr('width', d => d.width)
      .attr('height', d => d.height)
      .attr('fill', d => d.color)
      .attr('opacity', 0.3);

    // Add quadrant labels
    g.selectAll('.quadrant-label')
      .data(quadrants)
      .enter().append('text')
      .attr('class', 'quadrant-label')
      .attr('x', d => d.x + d.width/2)
      .attr('y', d => d.y + d.height/2)
      .attr('text-anchor', 'middle')
      .attr('dominant-baseline', 'middle')
      .style('font-size', '12px')
      .style('font-weight', 'bold')
      .style('fill', '#666')
      .style('opacity', 0.7)
      .selectAll('tspan')
      .data(d => d.label.split('\n'))
      .enter().append('tspan')
      .attr('x', function() { return d3.select(this.parentNode).attr('x'); })
      .attr('dy', (d, i) => i === 0 ? 0 : '1.2em')
      .text(d => d);

    // Add grid lines
    g.append('g')
      .attr('class', 'grid')
      .attr('transform', `translate(0,${chartHeight})`)
      .call(d3.axisBottom(xScale)
        .ticks(10)
        .tickSize(-chartHeight)
        .tickFormat('')
      )
      .style('stroke-dasharray', '3,3')
      .style('opacity', 0.3);

    g.append('g')
      .attr('class', 'grid')
      .call(d3.axisLeft(yScale)
        .ticks(10)
        .tickSize(-width)
        .tickFormat('')
      )
      .style('stroke-dasharray', '3,3')
      .style('opacity', 0.3);

    // Add average lines
    const avgPerformance = d3.mean(data, d => d.performanceScore);
    const avgRisk = d3.mean(data, d => d.riskScore);

    g.append('line')
      .attr('x1', xScale(avgRisk))
      .attr('x2', xScale(avgRisk))
      .attr('y1', 0)
      .attr('y2', chartHeight)
      .attr('stroke', '#333')
      .attr('stroke-width', 2)
      .attr('stroke-dasharray', '5,5')
      .attr('opacity', 0.7);

    g.append('line')
      .attr('x1', 0)
      .attr('x2', width)
      .attr('y1', yScale(avgPerformance))
      .attr('y2', yScale(avgPerformance))
      .attr('stroke', '#333')
      .attr('stroke-width', 2)
      .attr('stroke-dasharray', '5,5')
      .attr('opacity', 0.7);

    // Add bubbles for suppliers
    const bubbles = g.selectAll('.supplier-bubble')
      .data(data)
      .enter().append('circle')
      .attr('class', 'supplier-bubble')
      .attr('cx', d => xScale(d.riskScore))
      .attr('cy', d => yScale(d.performanceScore))
      .attr('r', d => sizeScale(d.orderVolume))
      .attr('fill', d => colorScale(d.category))
      .attr('stroke', '#fff')
      .attr('stroke-width', 2)
      .attr('opacity', 0.8)
      .style('cursor', 'pointer')
      .on('mouseover', function(event, d) {
        d3.select(this)
          .transition()
          .duration(100)
          .attr('r', sizeScale(d.orderVolume) * 1.2)
          .attr('opacity', 1);

        showTooltip(event, d);
      })
      .on('mouseout', function(event, d) {
        d3.select(this)
          .transition()
          .duration(100)
          .attr('r', sizeScale(d.orderVolume))
          .attr('opacity', 0.8);

        hideTooltip();
      })
      .on('click', function(event, d) {
        setSelectedSupplier(d);
      });

    // Axes
    g.append('g')
      .attr('transform', `translate(0,${chartHeight})`)
      .call(d3.axisBottom(xScale))
      .selectAll('text')
      .style('font-size', '12px');

    g.append('g')
      .call(d3.axisLeft(yScale))
      .selectAll('text')
      .style('font-size', '12px');

    // Axis labels
    g.append('text')
      .attr('transform', 'rotate(-90)')
      .attr('y', 0 - margin.left)
      .attr('x', 0 - (chartHeight / 2))
      .attr('dy', '1em')
      .style('text-anchor', 'middle')
      .style('font-size', '14px')
      .style('font-weight', 'bold')
      .text('Performance Score');

    g.append('text')
      .attr('transform', `translate(${width / 2}, ${chartHeight + margin.bottom - 10})`)
      .style('text-anchor', 'middle')
      .style('font-size', '14px')
      .style('font-weight', 'bold')
      .text('Risk Score');

    // Title
    g.append('text')
      .attr('x', width / 2)
      .attr('y', 0 - (margin.top / 2))
      .attr('text-anchor', 'middle')
      .style('font-size', '18px')
      .style('font-weight', 'bold')
      .text('Supplier Performance vs Risk Matrix');

    // Legend
    const legend = svg.append('g')
      .attr('transform', `translate(${width + margin.left + 20}, ${margin.top})`);

    const categories = colorScale.domain();
    legend.selectAll('.legend-item')
      .data(categories)
      .enter().append('g')
      .attr('class', 'legend-item')
      .attr('transform', (d, i) => `translate(0, ${i * 25})`)
      .each(function(d) {
        const item = d3.select(this);
        item.append('circle')
          .attr('r', 8)
          .attr('fill', colorScale(d))
          .attr('stroke', '#fff')
          .attr('stroke-width', 2);
        
        item.append('text')
          .attr('x', 15)
          .attr('y', 0)
          .attr('dy', '0.35em')
          .style('font-size', '12px')
          .text(d);
      });

    // Size legend
    const sizeLegend = legend.append('g')
      .attr('transform', `translate(0, ${categories.length * 25 + 30})`);

    sizeLegend.append('text')
      .attr('x', 0)
      .attr('y', 0)
      .style('font-size', '12px')
      .style('font-weight', 'bold')
      .text('Order Volume');

    const sizeValues = [100000, 300000, 500000];
    sizeValues.forEach((value, i) => {
      const size = sizeScale(value);
      sizeLegend.append('circle')
        .attr('cx', 0)
        .attr('cy', 20 + i * 35)
        .attr('r', size)
        .attr('fill', '#ccc')
        .attr('stroke', '#fff')
        .attr('stroke-width', 1);

      sizeLegend.append('text')
        .attr('x', size + 10)
        .attr('y', 20 + i * 35)
        .attr('dy', '0.35em')
        .style('font-size', '10px')
        .text(`$${(value / 1000)}K`);
    });

  }, [data, height, loading]);

  const showTooltip = (event, d) => {
    const tooltip = d3.select('body').append('div')
      .attr('class', 'd3-tooltip')
      .style('position', 'absolute')
      .style('background', 'rgba(0,0,0,0.9)')
      .style('color', 'white')
      .style('padding', '12px')
      .style('border-radius', '6px')
      .style('font-size', '12px')
      .style('pointer-events', 'none')
      .style('z-index', 1000)
      .style('max-width', '250px');

    tooltip.html(`
      <div style="font-weight: bold; margin-bottom: 8px;">${d.name}</div>
      <div><strong>Performance:</strong> ${d.performanceScore.toFixed(1)}</div>
      <div><strong>Risk Score:</strong> ${d.riskScore.toFixed(1)}</div>
      <div><strong>Order Volume:</strong> $${(d.orderVolume / 1000).toFixed(0)}K</div>
      <div><strong>On-time Delivery:</strong> ${d.onTimeDelivery.toFixed(1)}%</div>
      <div><strong>Quality Score:</strong> ${d.qualityScore.toFixed(1)}/10</div>
      <div><strong>Category:</strong> ${d.category}</div>
      <div><strong>Region:</strong> ${d.region}</div>
      <div><strong>Partnership:</strong> ${d.partnershipYears} years</div>
    `)
    .style('left', (event.pageX + 10) + 'px')
    .style('top', (event.pageY - 10) + 'px');
  };

  const hideTooltip = () => {
    d3.selectAll('.d3-tooltip').remove();
  };

  if (loading) {
    return (
      <Card>
        <CardContent>
          <Box display="flex" justifyContent="center" alignItems="center" height={height}>
            <Typography>Loading supplier performance data...</Typography>
          </Box>
        </CardContent>
      </Card>
    );
  }

  return (
    <Grid container spacing={2}>
      <Grid item xs={12} lg={selectedSupplier ? 8 : 12}>
        <Card>
          <CardContent>
            <svg
              ref={svgRef}
              width="100%"
              height={height}
              style={{ overflow: 'visible' }}
            />
          </CardContent>
        </Card>
      </Grid>
      
      {selectedSupplier && (
        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                {selectedSupplier.name}
              </Typography>
              
              <Box mb={2}>
                <Chip 
                  label={selectedSupplier.category} 
                  color="primary" 
                  size="small" 
                  style={{ marginRight: 8 }}
                />
                <Chip 
                  label={selectedSupplier.region} 
                  variant="outlined" 
                  size="small" 
                />
              </Box>

              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">
                    Performance Score
                  </Typography>
                  <Typography variant="h6">
                    {selectedSupplier.performanceScore.toFixed(1)}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">
                    Risk Score
                  </Typography>
                  <Typography variant="h6">
                    {selectedSupplier.riskScore.toFixed(1)}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">
                    Order Volume
                  </Typography>
                  <Typography variant="h6">
                    ${(selectedSupplier.orderVolume / 1000).toFixed(0)}K
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">
                    On-time Delivery
                  </Typography>
                  <Typography variant="h6">
                    {selectedSupplier.onTimeDelivery.toFixed(1)}%
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">
                    Quality Score
                  </Typography>
                  <Typography variant="h6">
                    {selectedSupplier.qualityScore.toFixed(1)}/10
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="textSecondary">
                    Partnership Years
                  </Typography>
                  <Typography variant="h6">
                    {selectedSupplier.partnershipYears}
                  </Typography>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>
      )}
    </Grid>
  );
};

export default SupplierPerformanceMatrix;