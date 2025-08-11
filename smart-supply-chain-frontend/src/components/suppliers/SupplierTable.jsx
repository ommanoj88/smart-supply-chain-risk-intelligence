import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { 
  ChevronUp, 
  ChevronDown, 
  ChevronLeft, 
  ChevronRight,
  Eye,
  Edit,
  Trash2,
  MoreVertical,
  MapPin,
  Building2,
  RefreshCw
} from 'lucide-react';
import { supplierUtils } from '../../services/supplierApi';

const SupplierTable = ({ 
  suppliers, 
  isLoading, 
  pagination, 
  totalElements, 
  onSortChange, 
  onPageChange,
  onRefresh 
}) => {
  const [selectedSuppliers, setSelectedSuppliers] = useState(new Set());

  const handleSort = (field) => {
    const newDirection = 
      pagination.sortBy === field && pagination.sortDirection === 'asc' 
        ? 'desc' 
        : 'asc';
    onSortChange(field, newDirection);
  };

  const handleSelectAll = () => {
    if (selectedSuppliers.size === suppliers.length) {
      setSelectedSuppliers(new Set());
    } else {
      setSelectedSuppliers(new Set(suppliers.map(s => s.id)));
    }
  };

  const handleSelectSupplier = (id) => {
    const newSelected = new Set(selectedSuppliers);
    if (newSelected.has(id)) {
      newSelected.delete(id);
    } else {
      newSelected.add(id);
    }
    setSelectedSuppliers(newSelected);
  };

  const SortIcon = ({ field }) => {
    if (pagination.sortBy !== field) {
      return <ChevronUp className="w-4 h-4 text-gray-400" />;
    }
    return pagination.sortDirection === 'asc' 
      ? <ChevronUp className="w-4 h-4 text-primary-600" />
      : <ChevronDown className="w-4 h-4 text-primary-600" />;
  };

  const RiskBadge = ({ score }) => {
    const risk = supplierUtils.formatRiskScore(score);
    return (
      <span className={`risk-badge ${risk.bgColor} ${risk.color}`}>
        {score} - {risk.level}
      </span>
    );
  };

  const StatusBadge = ({ status }) => {
    const statusInfo = supplierUtils.formatStatus(status);
    return (
      <span className={`risk-badge ${statusInfo.bgColor} ${statusInfo.color}`}>
        {statusInfo.label}
      </span>
    );
  };

  const TierBadge = ({ tier }) => {
    const tierInfo = supplierUtils.formatTier(tier);
    return (
      <span className={`risk-badge ${tierInfo.bgColor} ${tierInfo.color}`}>
        {tierInfo.label}
      </span>
    );
  };

  const totalPages = Math.ceil(totalElements / pagination.size);

  if (isLoading) {
    return (
      <div className="card">
        <div className="p-6">
          <div className="animate-pulse space-y-4">
            <div className="h-4 bg-gray-200 rounded w-1/4"></div>
            {[...Array(5)].map((_, i) => (
              <div key={i} className="flex space-x-4">
                <div className="h-4 bg-gray-200 rounded flex-1"></div>
                <div className="h-4 bg-gray-200 rounded w-20"></div>
                <div className="h-4 bg-gray-200 rounded w-20"></div>
                <div className="h-4 bg-gray-200 rounded w-20"></div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      {/* Table Header Actions */}
      <div className="p-6 border-b border-gray-200">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-4">
            {selectedSuppliers.size > 0 && (
              <div className="text-sm text-gray-600">
                {selectedSuppliers.size} supplier{selectedSuppliers.size !== 1 ? 's' : ''} selected
              </div>
            )}
          </div>
          <button
            onClick={onRefresh}
            className="btn-secondary inline-flex items-center space-x-2"
          >
            <RefreshCw className="w-4 h-4" />
            <span>Refresh</span>
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              <th className="w-12 px-6 py-3">
                <input
                  type="checkbox"
                  checked={selectedSuppliers.size === suppliers.length && suppliers.length > 0}
                  onChange={handleSelectAll}
                  className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                />
              </th>
              <th 
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort('name')}
              >
                <div className="flex items-center space-x-1">
                  <span>Supplier</span>
                  <SortIcon field="name" />
                </div>
              </th>
              <th 
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort('tier')}
              >
                <div className="flex items-center space-x-1">
                  <span>Tier</span>
                  <SortIcon field="tier" />
                </div>
              </th>
              <th 
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort('industry')}
              >
                <div className="flex items-center space-x-1">
                  <span>Industry</span>
                  <SortIcon field="industry" />
                </div>
              </th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Location
              </th>
              <th 
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort('overallRiskScore')}
              >
                <div className="flex items-center space-x-1">
                  <span>Risk Score</span>
                  <SortIcon field="overallRiskScore" />
                </div>
              </th>
              <th 
                className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider cursor-pointer hover:bg-gray-100"
                onClick={() => handleSort('status')}
              >
                <div className="flex items-center space-x-1">
                  <span>Status</span>
                  <SortIcon field="status" />
                </div>
              </th>
              <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {suppliers.map((supplier) => (
              <motion.tr
                key={supplier.id}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="hover:bg-gray-50 transition-colors duration-150"
              >
                <td className="px-6 py-4">
                  <input
                    type="checkbox"
                    checked={selectedSuppliers.has(supplier.id)}
                    onChange={() => handleSelectSupplier(supplier.id)}
                    className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
                  />
                </td>
                <td className="px-6 py-4">
                  <div className="flex items-center space-x-3">
                    <div className="flex-shrink-0">
                      <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-600 rounded-fluent flex items-center justify-center">
                        <Building2 className="w-5 h-5 text-white" />
                      </div>
                    </div>
                    <div>
                      <div className="text-sm font-medium text-gray-900">{supplier.name}</div>
                      <div className="text-sm text-gray-500">{supplier.supplierCode}</div>
                    </div>
                  </div>
                </td>
                <td className="px-6 py-4">
                  <TierBadge tier={supplier.tier} />
                </td>
                <td className="px-6 py-4">
                  <div className="text-sm text-gray-900">{supplier.industry || 'N/A'}</div>
                </td>
                <td className="px-6 py-4">
                  <div className="flex items-center space-x-1 text-sm text-gray-600">
                    <MapPin className="w-4 h-4" />
                    <span>{supplier.city && supplier.country ? `${supplier.city}, ${supplier.country}` : supplier.country || 'N/A'}</span>
                  </div>
                </td>
                <td className="px-6 py-4">
                  <RiskBadge score={supplier.overallRiskScore || 0} />
                </td>
                <td className="px-6 py-4">
                  <StatusBadge status={supplier.status} />
                </td>
                <td className="px-6 py-4 text-right">
                  <div className="flex items-center justify-end space-x-2">
                    <button className="text-gray-400 hover:text-primary-600 p-1">
                      <Eye className="w-4 h-4" />
                    </button>
                    <button className="text-gray-400 hover:text-primary-600 p-1">
                      <Edit className="w-4 h-4" />
                    </button>
                    <button className="text-gray-400 hover:text-red-600 p-1">
                      <Trash2 className="w-4 h-4" />
                    </button>
                    <button className="text-gray-400 hover:text-gray-600 p-1">
                      <MoreVertical className="w-4 h-4" />
                    </button>
                  </div>
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="px-6 py-4 border-t border-gray-200">
        <div className="flex items-center justify-between">
          <div className="text-sm text-gray-600">
            Showing {pagination.page * pagination.size + 1} to {Math.min((pagination.page + 1) * pagination.size, totalElements)} of {totalElements} results
          </div>
          <div className="flex items-center space-x-2">
            <button
              onClick={() => onPageChange(pagination.page - 1)}
              disabled={pagination.page === 0}
              className="btn-secondary p-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            
            <div className="flex items-center space-x-1">
              {[...Array(Math.min(5, totalPages))].map((_, i) => {
                let pageNum;
                if (totalPages <= 5) {
                  pageNum = i;
                } else if (pagination.page < 3) {
                  pageNum = i;
                } else if (pagination.page > totalPages - 3) {
                  pageNum = totalPages - 5 + i;
                } else {
                  pageNum = pagination.page - 2 + i;
                }
                
                return (
                  <button
                    key={pageNum}
                    onClick={() => onPageChange(pageNum)}
                    className={`w-8 h-8 text-sm rounded-fluent ${
                      pagination.page === pageNum
                        ? 'bg-primary-500 text-white'
                        : 'text-gray-600 hover:bg-gray-100'
                    }`}
                  >
                    {pageNum + 1}
                  </button>
                );
              })}
            </div>
            
            <button
              onClick={() => onPageChange(pagination.page + 1)}
              disabled={pagination.page >= totalPages - 1}
              className="btn-secondary p-2 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SupplierTable;