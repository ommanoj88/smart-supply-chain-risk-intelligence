import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { X } from 'lucide-react';
import { supplierApi } from '../../services/supplierApi';

const SupplierFilters = ({ filters, onFilterChange }) => {
  // Fetch filter options
  const { data: countriesData } = useQuery({
    queryKey: ['countries'],
    queryFn: () => supplierApi.getDistinctCountries(),
  });

  const { data: industriesData } = useQuery({
    queryKey: ['industries'],
    queryFn: () => supplierApi.getDistinctIndustries(),
  });

  const countries = countriesData?.data || [];
  const industries = industriesData?.data || [];

  const handleFilterChange = (field, value) => {
    const newFilters = { ...filters, [field]: value };
    onFilterChange(newFilters);
  };

  const clearFilters = () => {
    onFilterChange({
      status: '',
      tier: '',
      country: '',
      industry: '',
      minRiskScore: '',
      maxRiskScore: '',
    });
  };

  const hasActiveFilters = Object.values(filters).some(v => v !== '');

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-medium text-gray-900">Advanced Filters</h3>
        {hasActiveFilters && (
          <button
            onClick={clearFilters}
            className="text-sm text-gray-500 hover:text-gray-700 inline-flex items-center space-x-1"
          >
            <X className="w-4 h-4" />
            <span>Clear all</span>
          </button>
        )}
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
        {/* Status Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Status</label>
          <select
            value={filters.status}
            onChange={(e) => handleFilterChange('status', e.target.value)}
            className="input-field"
          >
            <option value="">All Statuses</option>
            <option value="ACTIVE">Active</option>
            <option value="INACTIVE">Inactive</option>
            <option value="PENDING">Pending</option>
            <option value="BLOCKED">Blocked</option>
          </select>
        </div>

        {/* Tier Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Tier</label>
          <select
            value={filters.tier}
            onChange={(e) => handleFilterChange('tier', e.target.value)}
            className="input-field"
          >
            <option value="">All Tiers</option>
            <option value="PRIMARY">Primary</option>
            <option value="SECONDARY">Secondary</option>
            <option value="BACKUP">Backup</option>
          </select>
        </div>

        {/* Country Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Country</label>
          <select
            value={filters.country}
            onChange={(e) => handleFilterChange('country', e.target.value)}
            className="input-field"
          >
            <option value="">All Countries</option>
            {countries.map((country) => (
              <option key={country} value={country}>
                {country}
              </option>
            ))}
          </select>
        </div>

        {/* Industry Filter */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Industry</label>
          <select
            value={filters.industry}
            onChange={(e) => handleFilterChange('industry', e.target.value)}
            className="input-field"
          >
            <option value="">All Industries</option>
            {industries.map((industry) => (
              <option key={industry} value={industry}>
                {industry}
              </option>
            ))}
          </select>
        </div>

        {/* Min Risk Score */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Min Risk Score</label>
          <input
            type="number"
            min="0"
            max="100"
            placeholder="0"
            value={filters.minRiskScore}
            onChange={(e) => handleFilterChange('minRiskScore', e.target.value)}
            className="input-field"
          />
        </div>

        {/* Max Risk Score */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">Max Risk Score</label>
          <input
            type="number"
            min="0"
            max="100"
            placeholder="100"
            value={filters.maxRiskScore}
            onChange={(e) => handleFilterChange('maxRiskScore', e.target.value)}
            className="input-field"
          />
        </div>
      </div>

      {/* Risk Score Range Indicator */}
      {(filters.minRiskScore || filters.maxRiskScore) && (
        <div className="mt-4 p-3 bg-gray-50 rounded-fluent">
          <div className="text-sm text-gray-600 mb-2">Risk Score Range</div>
          <div className="flex items-center space-x-2">
            <div className="flex-1 bg-gray-200 rounded-full h-2 relative">
              <div 
                className="bg-primary-500 h-2 rounded-full absolute"
                style={{
                  left: `${(filters.minRiskScore || 0)}%`,
                  width: `${(filters.maxRiskScore || 100) - (filters.minRiskScore || 0)}%`
                }}
              />
            </div>
            <span className="text-xs text-gray-500 min-w-max">
              {filters.minRiskScore || 0} - {filters.maxRiskScore || 100}
            </span>
          </div>
        </div>
      )}
    </div>
  );
};

export default SupplierFilters;