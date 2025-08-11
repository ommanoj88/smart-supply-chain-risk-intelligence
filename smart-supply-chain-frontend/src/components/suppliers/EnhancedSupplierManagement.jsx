import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import Card from '../ui/Card';
import Button from '../ui/Button';
import DataTable from '../ui/DataTable';
import StatusBadge from '../ui/StatusBadge';

// Icons
const PlusIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
  </svg>
);

const DownloadIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
  </svg>
);

const UploadIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
  </svg>
);

const SearchIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
  </svg>
);

const GlobalIcon = () => (
  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
  </svg>
);

const EnhancedSupplierManagement = () => {
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedSuppliers, setSelectedSuppliers] = useState(new Set());
  const [filters, setFilters] = useState({
    riskLevel: '',
    country: '',
    status: '',
    certification: ''
  });
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    const fetchSuppliers = async () => {
      try {
        setLoading(true);
        
        // Mock data for demonstration
        const mockSuppliers = [
          {
            id: 1,
            name: 'Global Tech Manufacturing',
            country: 'China',
            city: 'Shenzhen',
            status: 'Active',
            riskScore: 2.3,
            riskLevel: 'Medium',
            certifications: ['ISO 9001', 'ISO 14001'],
            performanceScore: 94,
            onTimeDelivery: 96.5,
            qualityRating: 4.7,
            contactEmail: 'contact@globaltech.com',
            lastOrder: '2024-01-15',
            totalOrders: 156
          },
          {
            id: 2,
            name: 'European Components Ltd',
            country: 'Germany',
            city: 'Munich',
            status: 'Active',
            riskScore: 1.2,
            riskLevel: 'Low',
            certifications: ['ISO 9001', 'CE'],
            performanceScore: 98,
            onTimeDelivery: 99.2,
            qualityRating: 4.9,
            contactEmail: 'info@eurocomp.de',
            lastOrder: '2024-01-20',
            totalOrders: 89
          },
          {
            id: 3,
            name: 'Pacific Supply Solutions',
            country: 'Japan',
            city: 'Tokyo',
            status: 'Under Review',
            riskScore: 3.8,
            riskLevel: 'High',
            certifications: ['ISO 9001'],
            performanceScore: 87,
            onTimeDelivery: 92.1,
            qualityRating: 4.3,
            contactEmail: 'orders@pacificsupply.jp',
            lastOrder: '2024-01-10',
            totalOrders: 234
          },
          {
            id: 4,
            name: 'American Electronics Corp',
            country: 'USA',
            city: 'San Francisco',
            status: 'Active',
            riskScore: 1.8,
            riskLevel: 'Low',
            certifications: ['ISO 9001', 'UL Listed'],
            performanceScore: 96,
            onTimeDelivery: 97.8,
            qualityRating: 4.8,
            contactEmail: 'procurement@amelectronics.com',
            lastOrder: '2024-01-22',
            totalOrders: 342
          }
        ];
        
        setSuppliers(mockSuppliers);
      } catch (error) {
        console.error('Error fetching suppliers:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchSuppliers();
  }, []);

  const getRiskColor = (riskLevel) => {
    switch (riskLevel.toLowerCase()) {
      case 'low': return 'green';
      case 'medium': return 'orange';
      case 'high': return 'red';
      default: return 'gray';
    }
  };

  const handleSelectSupplier = (supplierId) => {
    const newSelected = new Set(selectedSuppliers);
    if (newSelected.has(supplierId)) {
      newSelected.delete(supplierId);
    } else {
      newSelected.add(supplierId);
    }
    setSelectedSuppliers(newSelected);
  };

  const handleSelectAll = () => {
    if (selectedSuppliers.size === suppliers.length) {
      setSelectedSuppliers(new Set());
    } else {
      setSelectedSuppliers(new Set(suppliers.map(s => s.id)));
    }
  };

  const handleBulkAction = (action) => {
    console.log(`Performing ${action} on suppliers:`, Array.from(selectedSuppliers));
    // Here you would implement the actual bulk operations
  };

  const columns = [
    {
      accessor: 'select',
      header: (
        <input
          type="checkbox"
          checked={selectedSuppliers.size === suppliers.length && suppliers.length > 0}
          onChange={handleSelectAll}
          className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
        />
      ),
      cell: (row) => (
        <input
          type="checkbox"
          checked={selectedSuppliers.has(row.id)}
          onChange={() => handleSelectSupplier(row.id)}
          className="rounded border-gray-300 text-primary-600 focus:ring-primary-500"
        />
      ),
      width: '50px'
    },
    {
      accessor: 'name',
      header: 'Supplier Name',
      cell: (row) => (
        <div>
          <div className="font-medium text-gray-900">{row.name}</div>
          <div className="text-sm text-gray-500">{row.contactEmail}</div>
        </div>
      )
    },
    {
      accessor: 'location',
      header: 'Location',
      cell: (row) => (
        <div className="flex items-center">
          <GlobalIcon />
          <span className="ml-2">{row.city}, {row.country}</span>
        </div>
      )
    },
    {
      accessor: 'riskLevel',
      header: 'Risk Level',
      cell: (row) => (
        <StatusBadge
          status={row.riskLevel}
          color={getRiskColor(row.riskLevel)}
        />
      )
    },
    {
      accessor: 'performanceScore',
      header: 'Performance',
      cell: (row) => (
        <div className="text-center">
          <div className="font-medium">{row.performanceScore}%</div>
          <div className="text-xs text-gray-500">
            OTD: {row.onTimeDelivery}%
          </div>
        </div>
      )
    },
    {
      accessor: 'certifications',
      header: 'Certifications',
      cell: (row) => (
        <div className="flex flex-wrap gap-1">
          {row.certifications.map((cert, index) => (
            <span
              key={index}
              className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800"
            >
              {cert}
            </span>
          ))}
        </div>
      )
    },
    {
      accessor: 'status',
      header: 'Status',
      cell: (row) => (
        <StatusBadge
          status={row.status}
          color={row.status === 'Active' ? 'green' : 'orange'}
        />
      )
    },
    {
      accessor: 'actions',
      header: 'Actions',
      cell: (row) => (
        <div className="flex space-x-2">
          <Button variant="ghost" size="sm">
            View
          </Button>
          <Button variant="outline" size="sm">
            Edit
          </Button>
        </div>
      )
    }
  ];

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Supplier Management</h1>
          <p className="text-gray-600">Manage your supplier network and performance</p>
        </div>
        <div className="flex space-x-3">
          <Button variant="outline" size="sm">
            <UploadIcon />
            Import
          </Button>
          <Button variant="outline" size="sm">
            <DownloadIcon />
            Export
          </Button>
          <Button variant="primary" size="sm">
            <PlusIcon />
            Add Supplier
          </Button>
        </div>
      </div>

      {/* Filters and Search */}
      <Card variant="elevated">
        <Card.Content className="p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
            {/* Search */}
            <div className="lg:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Search Suppliers
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <SearchIcon />
                </div>
                <input
                  type="text"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="input-field pl-10"
                  placeholder="Search by name, email, or location..."
                />
              </div>
            </div>

            {/* Risk Level Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Risk Level
              </label>
              <select
                value={filters.riskLevel}
                onChange={(e) => setFilters({...filters, riskLevel: e.target.value})}
                className="input-field"
              >
                <option value="">All Risk Levels</option>
                <option value="low">Low Risk</option>
                <option value="medium">Medium Risk</option>
                <option value="high">High Risk</option>
              </select>
            </div>

            {/* Country Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Country
              </label>
              <select
                value={filters.country}
                onChange={(e) => setFilters({...filters, country: e.target.value})}
                className="input-field"
              >
                <option value="">All Countries</option>
                <option value="china">China</option>
                <option value="germany">Germany</option>
                <option value="japan">Japan</option>
                <option value="usa">USA</option>
              </select>
            </div>

            {/* Status Filter */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Status
              </label>
              <select
                value={filters.status}
                onChange={(e) => setFilters({...filters, status: e.target.value})}
                className="input-field"
              >
                <option value="">All Statuses</option>
                <option value="active">Active</option>
                <option value="under-review">Under Review</option>
                <option value="inactive">Inactive</option>
              </select>
            </div>
          </div>
        </Card.Content>
      </Card>

      {/* Bulk Actions */}
      {selectedSuppliers.size > 0 && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-primary-50 border border-primary-200 rounded-lg p-4"
        >
          <div className="flex items-center justify-between">
            <span className="text-primary-800 font-medium">
              {selectedSuppliers.size} supplier(s) selected
            </span>
            <div className="flex space-x-2">
              <Button variant="outline" size="sm" onClick={() => handleBulkAction('export')}>
                Export Selected
              </Button>
              <Button variant="outline" size="sm" onClick={() => handleBulkAction('update')}>
                Bulk Update
              </Button>
              <Button variant="outline" size="sm" onClick={() => handleBulkAction('audit')}>
                Request Audit
              </Button>
            </div>
          </div>
        </motion.div>
      )}

      {/* Suppliers Table */}
      <Card variant="elevated">
        <DataTable
          data={suppliers}
          columns={columns}
          loading={loading}
          searchable={false} // We have custom search
          pageSize={15}
          emptyState={
            <div className="text-center py-12">
              <GlobalIcon />
              <h3 className="mt-2 text-sm font-medium text-gray-900">No suppliers found</h3>
              <p className="mt-1 text-sm text-gray-500">
                Get started by adding your first supplier.
              </p>
              <div className="mt-6">
                <Button variant="primary">
                  <PlusIcon />
                  Add Supplier
                </Button>
              </div>
            </div>
          }
        />
      </Card>

      {/* Summary Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card variant="outlined">
          <Card.Content className="p-4 text-center">
            <div className="text-2xl font-bold text-gray-900">{suppliers.length}</div>
            <div className="text-sm text-gray-600">Total Suppliers</div>
          </Card.Content>
        </Card>
        <Card variant="outlined">
          <Card.Content className="p-4 text-center">
            <div className="text-2xl font-bold text-green-600">
              {suppliers.filter(s => s.status === 'Active').length}
            </div>
            <div className="text-sm text-gray-600">Active Suppliers</div>
          </Card.Content>
        </Card>
        <Card variant="outlined">
          <Card.Content className="p-4 text-center">
            <div className="text-2xl font-bold text-orange-600">
              {suppliers.filter(s => s.riskLevel === 'High').length}
            </div>
            <div className="text-sm text-gray-600">High Risk</div>
          </Card.Content>
        </Card>
        <Card variant="outlined">
          <Card.Content className="p-4 text-center">
            <div className="text-2xl font-bold text-blue-600">
              {Math.round(suppliers.reduce((acc, s) => acc + s.performanceScore, 0) / suppliers.length) || 0}%
            </div>
            <div className="text-sm text-gray-600">Avg Performance</div>
          </Card.Content>
        </Card>
      </div>
    </div>
  );
};

export default EnhancedSupplierManagement;