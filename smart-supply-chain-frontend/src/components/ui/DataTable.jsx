import React, { useState, useMemo } from 'react';
import { motion } from 'framer-motion';
import clsx from 'clsx';
import Button from './Button';

const DataTable = ({ 
  data = [], 
  columns = [], 
  sortable = true,
  searchable = true,
  pagination = true,
  pageSize = 10,
  className = '',
  onRowClick,
  rowClassName,
  emptyState,
  loading = false,
  ...props 
}) => {
  const [sortConfig, setSortConfig] = useState({ key: null, direction: 'asc' });
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);

  // Filtering and sorting logic
  const filteredAndSortedData = useMemo(() => {
    let processedData = [...data];

    // Apply search filter
    if (searchTerm && searchable) {
      processedData = processedData.filter(row =>
        columns.some(column => {
          const value = column.accessor ? row[column.accessor] : '';
          return String(value).toLowerCase().includes(searchTerm.toLowerCase());
        })
      );
    }

    // Apply sorting
    if (sortConfig.key && sortable) {
      processedData.sort((a, b) => {
        const aValue = a[sortConfig.key];
        const bValue = b[sortConfig.key];
        
        if (aValue < bValue) {
          return sortConfig.direction === 'asc' ? -1 : 1;
        }
        if (aValue > bValue) {
          return sortConfig.direction === 'asc' ? 1 : -1;
        }
        return 0;
      });
    }

    return processedData;
  }, [data, columns, searchTerm, sortConfig, searchable, sortable]);

  // Pagination logic
  const paginatedData = useMemo(() => {
    if (!pagination) return filteredAndSortedData;
    
    const startIndex = (currentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return filteredAndSortedData.slice(startIndex, endIndex);
  }, [filteredAndSortedData, currentPage, pageSize, pagination]);

  const totalPages = Math.ceil(filteredAndSortedData.length / pageSize);

  const handleSort = (key) => {
    if (!sortable) return;
    
    setSortConfig(prevConfig => ({
      key,
      direction: prevConfig.key === key && prevConfig.direction === 'asc' ? 'desc' : 'asc'
    }));
  };

  const getSortIcon = (columnKey) => {
    if (sortConfig.key !== columnKey) {
      return (
        <svg className="w-4 h-4 text-gray-400" fill="currentColor" viewBox="0 0 20 20">
          <path d="M5 12a1 1 0 102 0V6.414l1.293 1.293a1 1 0 001.414-1.414l-3-3a1 1 0 00-1.414 0l-3 3a1 1 0 001.414 1.414L5 6.414V12zM15 8a1 1 0 10-2 0v5.586l-1.293-1.293a1 1 0 00-1.414 1.414l3 3a1 1 0 001.414 0l3-3a1 1 0 00-1.414-1.414L15 13.586V8z" />
        </svg>
      );
    }

    return sortConfig.direction === 'asc' ? (
      <svg className="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
        <path d="M3 3a1 1 0 000 2h11.586l-4.293 4.293a1 1 0 101.414 1.414L16 6.414V18a1 1 0 102 0V6.414l4.293 4.293a1 1 0 001.414-1.414L19.414 5H21a1 1 0 100-2H3z" />
      </svg>
    ) : (
      <svg className="w-4 h-4 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
        <path d="M21 17a1 1 0 01-1 1H8.414l4.293-4.293a1 1 0 00-1.414-1.414L7 16.586V5a1 1 0 10-2 0v11.586l-4.293-4.293a1 1 0 00-1.414 1.414L3.586 18H2a1 1 0 100 2h18a1 1 0 001-1z" />
      </svg>
    );
  };

  const renderCell = (row, column) => {
    if (column.render) {
      return column.render(row[column.accessor], row);
    }
    return row[column.accessor] || '';
  };

  if (loading) {
    return (
      <div className="animate-pulse">
        <div className="h-10 bg-gray-200 rounded mb-4"></div>
        <div className="space-y-3">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-12 bg-gray-200 rounded"></div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className={clsx('w-full', className)} {...props}>
      {/* Search Bar */}
      {searchable && (
        <div className="mb-4">
          <div className="relative">
            <input
              type="text"
              placeholder="Search..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </div>
          </div>
        </div>
      )}

      {/* Table */}
      <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 rounded-lg">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-300 bg-white">
            <thead className="bg-gray-50">
              <tr>
                {columns.map((column) => (
                  <th
                    key={column.accessor}
                    className={clsx(
                      'px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider',
                      sortable && column.sortable !== false ? 'cursor-pointer hover:bg-gray-100' : ''
                    )}
                    onClick={() => column.sortable !== false && handleSort(column.accessor)}
                  >
                    <div className="flex items-center space-x-1">
                      <span>{column.header}</span>
                      {sortable && column.sortable !== false && getSortIcon(column.accessor)}
                    </div>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {paginatedData.length === 0 ? (
                <tr>
                  <td colSpan={columns.length} className="px-6 py-12 text-center">
                    {emptyState || (
                      <div className="text-gray-500">
                        <svg className="mx-auto h-12 w-12 text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                        <p className="text-sm">No data to display</p>
                      </div>
                    )}
                  </td>
                </tr>
              ) : (
                paginatedData.map((row, index) => (
                  <motion.tr
                    key={row.id || index}
                    className={clsx(
                      'hover:bg-gray-50 transition-colors duration-150',
                      onRowClick ? 'cursor-pointer' : '',
                      rowClassName ? rowClassName(row) : ''
                    )}
                    onClick={() => onRowClick?.(row)}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.3, delay: index * 0.05 }}
                  >
                    {columns.map((column) => (
                      <td
                        key={column.accessor}
                        className="px-6 py-4 whitespace-nowrap text-sm text-gray-900"
                      >
                        {renderCell(row, column)}
                      </td>
                    ))}
                  </motion.tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pagination */}
      {pagination && totalPages > 1 && (
        <div className="flex items-center justify-between mt-6">
          <div className="text-sm text-gray-700">
            Showing{' '}
            <span className="font-medium">
              {(currentPage - 1) * pageSize + 1}
            </span>{' '}
            to{' '}
            <span className="font-medium">
              {Math.min(currentPage * pageSize, filteredAndSortedData.length)}
            </span>{' '}
            of{' '}
            <span className="font-medium">
              {filteredAndSortedData.length}
            </span>{' '}
            results
          </div>
          
          <div className="flex space-x-2">
            <Button
              variant="secondary"
              size="sm"
              disabled={currentPage === 1}
              onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
            >
              Previous
            </Button>
            
            {[...Array(Math.min(5, totalPages))].map((_, i) => {
              const page = i + 1;
              return (
                <Button
                  key={page}
                  variant={currentPage === page ? 'primary' : 'secondary'}
                  size="sm"
                  onClick={() => setCurrentPage(page)}
                >
                  {page}
                </Button>
              );
            })}
            
            <Button
              variant="secondary"
              size="sm"
              disabled={currentPage === totalPages}
              onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
            >
              Next
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};

export default DataTable;