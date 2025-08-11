import React, { useState, useEffect, useRef } from 'react';
import * as d3 from 'd3';
import { Card, CardContent, Typography, Box, CircularProgress } from '@mui/material';
import mlService from '../../services/mlService';

/**
 * Advanced Risk Trend Chart using D3.js
 * Shows predictive risk trends over time with confidence intervals
 */
const RiskTrendChart = ({ timeRange = '30d', height = 400 }) => {
  const svgRef = useRef();
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadRiskTrends();
  }, [timeRange]);

  const loadRiskTrends = async () => {
    try {
      setLoading(true);
      const trendData = await mlService.getRiskTrends(timeRange);
      setData(trendData.trends || []);
      setError(null);
    } catch (err) {
      setError(err.message);
      // Use fallback data for demonstration
      setData(generateFallbackData());
    } finally {
      setLoading(false);
    }
  };

  const generateFallbackData = () => {
    const days = timeRange === '7d' ? 7 : timeRange === '30d' ? 30 : 90;
    const data = [];
    const now = new Date();

    for (let i = days; i >= 0; i--) {
      const date = new Date(now);
      date.setDate(date.getDate() - i);
      
      const baseRisk = 40 + Math.sin(i * 0.1) * 10 + Math.random() * 10;
      const confidence = 5 + Math.random() * 3;
      
      data.push({
        date: date,
        riskScore: Math.max(0, Math.min(100, baseRisk)),
        lowerBound: Math.max(0, baseRisk - confidence),
        upperBound: Math.min(100, baseRisk + confidence),
        shipmentCount: Math.floor(50 + Math.random() * 100)
      });
    }
    
    return data;
  };

  useEffect(() => {
    if (!data.length || loading) return;

    const svg = d3.select(svgRef.current);
    svg.selectAll("*").remove(); // Clear previous chart

    const margin = { top: 20, right: 80, bottom: 40, left: 60 };
    const width = svg.node().getBoundingClientRect().width - margin.left - margin.right;
    const chartHeight = height - margin.top - margin.bottom;

    const g = svg.append("g")
      .attr("transform", `translate(${margin.left},${margin.top})`);

    // Scales
    const xScale = d3.scaleTime()
      .domain(d3.extent(data, d => d.date))
      .range([0, width]);

    const yScale = d3.scaleLinear()
      .domain([0, 100])
      .range([chartHeight, 0]);

    // Color scale for risk levels
    const colorScale = d3.scaleLinear()
      .domain([0, 30, 60, 80, 100])
      .range(['#4caf50', '#8bc34a', '#ff9800', '#f44336', '#d32f2f']);

    // Area generator for confidence interval
    const area = d3.area()
      .x(d => xScale(d.date))
      .y0(d => yScale(d.lowerBound))
      .y1(d => yScale(d.upperBound))
      .curve(d3.curveMonotoneX);

    // Line generator for main trend
    const line = d3.line()
      .x(d => xScale(d.date))
      .y(d => yScale(d.riskScore))
      .curve(d3.curveMonotoneX);

    // Gradient for area fill
    const gradient = g.append("defs")
      .append("linearGradient")
      .attr("id", "risk-gradient")
      .attr("gradientUnits", "userSpaceOnUse")
      .attr("x1", 0).attr("y1", chartHeight)
      .attr("x2", 0).attr("y2", 0);

    gradient.selectAll("stop")
      .data([
        { offset: "0%", color: "#4caf50", opacity: 0.1 },
        { offset: "30%", color: "#8bc34a", opacity: 0.2 },
        { offset: "60%", color: "#ff9800", opacity: 0.3 },
        { offset: "80%", color: "#f44336", opacity: 0.4 },
        { offset: "100%", color: "#d32f2f", opacity: 0.5 }
      ])
      .enter().append("stop")
      .attr("offset", d => d.offset)
      .attr("stop-color", d => d.color)
      .attr("stop-opacity", d => d.opacity);

    // Draw confidence interval area
    g.append("path")
      .datum(data)
      .attr("class", "confidence-area")
      .attr("fill", "url(#risk-gradient)")
      .attr("d", area);

    // Draw main trend line
    g.append("path")
      .datum(data)
      .attr("class", "risk-line")
      .attr("fill", "none")
      .attr("stroke", "#1976d2")
      .attr("stroke-width", 3)
      .attr("d", line);

    // Add data points
    g.selectAll(".risk-point")
      .data(data)
      .enter().append("circle")
      .attr("class", "risk-point")
      .attr("cx", d => xScale(d.date))
      .attr("cy", d => yScale(d.riskScore))
      .attr("r", 4)
      .attr("fill", d => colorScale(d.riskScore))
      .attr("stroke", "#fff")
      .attr("stroke-width", 2)
      .on("mouseover", function(event, d) {
        // Tooltip
        const tooltip = d3.select("body").append("div")
          .attr("class", "d3-tooltip")
          .style("position", "absolute")
          .style("background", "rgba(0,0,0,0.8)")
          .style("color", "white")
          .style("padding", "8px")
          .style("border-radius", "4px")
          .style("font-size", "12px")
          .style("pointer-events", "none")
          .style("z-index", 1000);

        tooltip.html(`
          <div><strong>Date:</strong> ${d.date.toLocaleDateString()}</div>
          <div><strong>Risk Score:</strong> ${d.riskScore.toFixed(1)}</div>
          <div><strong>Confidence:</strong> ${d.lowerBound.toFixed(1)} - ${d.upperBound.toFixed(1)}</div>
          <div><strong>Shipments:</strong> ${d.shipmentCount}</div>
        `)
        .style("left", (event.pageX + 10) + "px")
        .style("top", (event.pageY - 10) + "px");

        d3.select(this)
          .transition()
          .duration(100)
          .attr("r", 6);
      })
      .on("mouseout", function() {
        d3.selectAll(".d3-tooltip").remove();
        d3.select(this)
          .transition()
          .duration(100)
          .attr("r", 4);
      });

    // X-axis
    g.append("g")
      .attr("transform", `translate(0,${chartHeight})`)
      .call(d3.axisBottom(xScale)
        .ticks(d3.timeDay.every(Math.ceil(data.length / 8)))
        .tickFormat(d3.timeFormat("%m/%d")))
      .selectAll("text")
      .style("font-size", "12px");

    // Y-axis
    g.append("g")
      .call(d3.axisLeft(yScale)
        .ticks(10)
        .tickFormat(d => d + "%"))
      .selectAll("text")
      .style("font-size", "12px");

    // Risk level zones
    const riskZones = [
      { level: "LOW", range: [0, 30], color: "#4caf50" },
      { level: "MEDIUM", range: [30, 60], color: "#ff9800" },
      { level: "HIGH", range: [60, 80], color: "#f44336" },
      { level: "CRITICAL", range: [80, 100], color: "#d32f2f" }
    ];

    const zoneHeight = 20;
    const zoneY = chartHeight + 50;

    riskZones.forEach((zone, i) => {
      const zoneWidth = (zone.range[1] - zone.range[0]) * width / 100;
      const zoneX = zone.range[0] * width / 100;

      g.append("rect")
        .attr("x", zoneX)
        .attr("y", zoneY)
        .attr("width", zoneWidth)
        .attr("height", zoneHeight)
        .attr("fill", zone.color)
        .attr("opacity", 0.3);

      g.append("text")
        .attr("x", zoneX + zoneWidth / 2)
        .attr("y", zoneY + zoneHeight / 2)
        .attr("text-anchor", "middle")
        .attr("dominant-baseline", "middle")
        .style("font-size", "10px")
        .style("font-weight", "bold")
        .style("fill", zone.color)
        .text(zone.level);
    });

    // Axis labels
    g.append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 0 - margin.left)
      .attr("x", 0 - (chartHeight / 2))
      .attr("dy", "1em")
      .style("text-anchor", "middle")
      .style("font-size", "14px")
      .style("font-weight", "bold")
      .text("Risk Score (%)");

    g.append("text")
      .attr("transform", `translate(${width / 2}, ${chartHeight + margin.bottom})`)
      .style("text-anchor", "middle")
      .style("font-size", "14px")
      .style("font-weight", "bold")
      .text("Date");

    // Title
    g.append("text")
      .attr("x", width / 2)
      .attr("y", 0 - (margin.top / 2))
      .attr("text-anchor", "middle")
      .style("font-size", "16px")
      .style("font-weight", "bold")
      .text("Supply Chain Risk Trend Analysis");

  }, [data, height, loading]);

  if (loading) {
    return (
      <Card>
        <CardContent>
          <Box display="flex" justifyContent="center" alignItems="center" height={height}>
            <CircularProgress />
          </Box>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Risk Trend Analysis
          </Typography>
          <Typography color="error">
            Error loading risk trends: {error}
          </Typography>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardContent>
        <svg
          ref={svgRef}
          width="100%"
          height={height + 80} // Extra space for legend
          style={{ overflow: 'visible' }}
        />
      </CardContent>
    </Card>
  );
};

export default RiskTrendChart;