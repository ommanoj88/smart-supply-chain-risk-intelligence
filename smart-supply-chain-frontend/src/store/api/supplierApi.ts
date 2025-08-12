import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

/**
 * Enhanced Supplier API with RTK Query for efficient data fetching
 */
export const supplierApi = createApi({
  reducerPath: 'supplierApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/suppliers',
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as any).auth.token;
      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }
      return headers;
    },
  }),
  tagTypes: ['Supplier', 'SupplierStats'],
  endpoints: (builder) => ({
    // Get all suppliers with advanced filtering
    getSuppliers: builder.query({
      query: ({ page = 0, size = 20, sortBy = 'name', sortDirection = 'asc', filters = {} }) => {
        const params = new URLSearchParams({
          page: page.toString(),
          size: size.toString(),
          sortBy,
          sortDirection,
          ...filters,
        });
        return `?${params}`;
      },
      providesTags: ['Supplier'],
    }),
    
    // Get supplier by ID
    getSupplier: builder.query({
      query: (id) => `/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Supplier', id }],
    }),
    
    // Get supplier performance metrics
    getSupplierPerformance: builder.query({
      query: (id) => `/${id}/performance`,
      providesTags: (_result, _error, id) => [{ type: 'SupplierStats', id }],
    }),
    
    // Get supplier risk assessment
    getSupplierRiskAssessment: builder.query({
      query: (id) => `/${id}/risk-assessment`,
      providesTags: (_result, _error, id) => [{ type: 'SupplierStats', id }],
    }),
    
    // Create new supplier
    createSupplier: builder.mutation({
      query: (newSupplier) => ({
        url: '',
        method: 'POST',
        body: newSupplier,
      }),
      invalidatesTags: ['Supplier'],
    }),
    
    // Update supplier
    updateSupplier: builder.mutation({
      query: ({ id, ...patch }) => ({
        url: `/${id}`,
        method: 'PUT',
        body: patch,
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Supplier', id },
        { type: 'SupplierStats', id },
      ],
    }),
    
    // Delete supplier
    deleteSupplier: builder.mutation({
      query: (id) => ({
        url: `/${id}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Supplier'],
    }),
    
    // Get supplier analytics
    getSupplierAnalytics: builder.query({
      query: (timeRange = '30d') => `/analytics?timeRange=${timeRange}`,
      providesTags: ['SupplierStats'],
    }),
    
    // Bulk operations
    bulkUpdateSuppliers: builder.mutation({
      query: (updates) => ({
        url: '/bulk-update',
        method: 'POST',
        body: updates,
      }),
      invalidatesTags: ['Supplier'],
    }),
  }),
});

export const {
  useGetSuppliersQuery,
  useGetSupplierQuery,
  useGetSupplierPerformanceQuery,
  useGetSupplierRiskAssessmentQuery,
  useCreateSupplierMutation,
  useUpdateSupplierMutation,
  useDeleteSupplierMutation,
  useGetSupplierAnalyticsQuery,
  useBulkUpdateSuppliersMutation,
  useLazyGetSupplierQuery,
  useLazyGetSuppliersQuery,
} = supplierApi;