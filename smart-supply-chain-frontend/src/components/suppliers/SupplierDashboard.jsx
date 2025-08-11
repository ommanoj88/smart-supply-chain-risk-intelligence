import React, { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { 
  Users, 
  TrendingUp, 
  AlertTriangle, 
  CheckCircle,
  Building2,
  MapPin,
  Search,
  Filter,
  Plus,
  MoreVertical
} from 'lucide-react';
import { supplierApi, supplierUtils, handleApiError } from '../../services/supplierApi';
import SupplierTable from './SupplierTable';
import SupplierStats from './SupplierStats';
import SupplierFilters from './SupplierFilters';

const SupplierDashboard = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    status: '',
    tier: '',
    country: '',
    industry: '',
    minRiskScore: '',
    maxRiskScore: '',
  });
  const [pagination, setPagination] = useState({
    page: 0,
    size: 20,
    sortBy: 'name',
    sortDirection: 'asc',
  });
  const [showFilters, setShowFilters] = useState(false);

  // Fetch suppliers with current filters and pagination
  const {
    data: suppliersData,
    isLoading,
    isError,
    error,
    refetch
  } = useQuery({
    queryKey: ['suppliers', searchTerm, filters, pagination],
    queryFn: () => {
      if (searchTerm.trim()) {
        return supplierApi.searchSuppliers({
          searchTerm,
          ...filters,
          ...pagination
        });
      } else {
        return supplierApi.getAllSuppliers(pagination);
      }
    },
    keepPreviousData: true
  });

  // Fetch supplier statistics
  const {
    data: statsData,
    isLoading: statsLoading
  } = useQuery({
    queryKey: ['supplier-stats'],
    queryFn: () => supplierApi.getSupplierStatistics(),
  });

  // Fetch suppliers requiring audit
  const {
    data: auditRequiredData
  } = useQuery({
    queryKey: ['audit-required'],
    queryFn: () => supplierApi.getSuppliersRequiringAudit(),
  });

  const handleSearch = (value) => {
    setSearchTerm(value);
    setPagination(prev => ({ ...prev, page: 0 }));
  };

  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
    setPagination(prev => ({ ...prev, page: 0 }));
  };

  const handleSortChange = (sortBy, sortDirection) => {
    setPagination(prev => ({ ...prev, sortBy, sortDirection, page: 0 }));
  };

  const handlePageChange = (page) => {
    setPagination(prev => ({ ...prev, page }));
  };

  // Quick stats from the data
  const stats = statsData?.data || {};
  const suppliers = suppliersData?.data?.content || [];
  const totalElements = suppliersData?.data?.totalElements || 0;
  const auditRequired = auditRequiredData?.data || [];

  // Animation variants
  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1
      }
    }
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: {
        type: "spring",
        stiffness: 300,
        damping: 30
      }
    }
  };

  return (
    <div className="page-container">
      <div className="content-container">
        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="space-y-6"
        >
          {/* Header */}
          <motion.div variants={itemVariants} className="flex flex-col lg:flex-row lg:items-center lg:justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Supplier Management</h1>
              <p className="mt-2 text-gray-600">
                Manage and monitor your supply chain partners with comprehensive risk intelligence
              </p>
            </div>
            <div className="mt-4 lg:mt-0 flex space-x-3">
              <button className="btn-secondary inline-flex items-center space-x-2">
                <Filter className="w-4 h-4" />
                <span>Export</span>
              </button>
              <button className="btn-primary inline-flex items-center space-x-2">
                <Plus className="w-4 h-4" />
                <span>Add Supplier</span>
              </button>
            </div>
          </motion.div>

          {/* Quick Stats */}
          <motion.div variants={itemVariants}>
            <SupplierStats stats={stats} auditRequired={auditRequired.length} isLoading={statsLoading} />
          </motion.div>

          {/* Search and Filters */}
          <motion.div variants={itemVariants} className="card p-6">
            <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between space-y-4 lg:space-y-0">
              {/* Search */}
              <div className="flex-1 lg:max-w-lg">
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                  <input
                    type="text"
                    placeholder="Search suppliers by name, code, or industry..."
                    className="input-field pl-10"
                    value={searchTerm}
                    onChange={(e) => handleSearch(e.target.value)}
                  />
                </div>
              </div>

              {/* Filter Toggle */}
              <div className="flex items-center space-x-3">
                <button
                  onClick={() => setShowFilters(!showFilters)}
                  className={`btn-secondary inline-flex items-center space-x-2 ${
                    showFilters ? 'bg-primary-50 text-primary-600 border-primary-200' : ''
                  }`}
                >
                  <Filter className="w-4 h-4" />
                  <span>Filters</span>
                  {Object.values(filters).some(v => v !== '') && (
                    <span className="bg-primary-500 text-white text-xs px-2 py-0.5 rounded-full">
                      {Object.values(filters).filter(v => v !== '').length}
                    </span>
                  )}
                </button>
              </div>
            </div>

            {/* Advanced Filters */}
            {showFilters && (
              <motion.div
                initial={{ height: 0, opacity: 0 }}
                animate={{ height: 'auto', opacity: 1 }}
                exit={{ height: 0, opacity: 0 }}
                transition={{ duration: 0.3 }}
                className="mt-6 pt-6 border-t border-gray-200"
              >
                <SupplierFilters
                  filters={filters}
                  onFilterChange={handleFilterChange}
                />
              </motion.div>
            )}
          </motion.div>

          {/* Results Summary */}
          <motion.div variants={itemVariants} className="flex items-center justify-between">
            <div className="text-sm text-gray-600">
              {isLoading ? (
                'Loading...'
              ) : (
                `Showing ${suppliers.length} of ${totalElements} suppliers`
              )}
            </div>
            {isError && (
              <div className="text-sm text-red-600 bg-red-50 px-3 py-1 rounded-fluent">
                {handleApiError(error)}
              </div>
            )}
          </motion.div>

          {/* Supplier Table */}
          <motion.div variants={itemVariants}>
            <SupplierTable
              suppliers={suppliers}
              isLoading={isLoading}
              pagination={pagination}
              totalElements={totalElements}
              onSortChange={handleSortChange}
              onPageChange={handlePageChange}
              onRefresh={refetch}
            />
          </motion.div>
        </motion.div>
      </div>
    </div>
  );
};

export default SupplierDashboard;