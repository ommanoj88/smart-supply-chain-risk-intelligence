import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

/**
 * Enhanced Shipment API with RTK Query for real-time tracking
 */
export const shipmentApi = createApi({
  reducerPath: 'shipmentApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/shipments',
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as any).auth.token;
      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }
      return headers;
    },
  }),
  tagTypes: ['Shipment', 'TrackingEvent', 'ShipmentStats'],
  endpoints: (builder) => ({
    // Get all shipments with advanced filtering
    getShipments: builder.query({
      query: ({ 
        page = 0, 
        size = 20, 
        sortBy = 'createdAt', 
        sortDirection = 'desc',
        status,
        supplierId,
        dateRange
      }) => {
        const params = new URLSearchParams({
          page: page.toString(),
          size: size.toString(),
          sortBy,
          sortDirection,
        });
        
        if (status) params.append('status', status);
        if (supplierId) params.append('supplierId', supplierId);
        if (dateRange?.start) params.append('startDate', dateRange.start);
        if (dateRange?.end) params.append('endDate', dateRange.end);
        
        return `?${params}`;
      },
      providesTags: ['Shipment'],
    }),
    
    // Get shipment by ID
    getShipment: builder.query({
      query: (id) => `/${id}`,
      providesTags: (_result, _error, id) => [{ type: 'Shipment', id }],
    }),
    
    // Track shipment by tracking number
    trackShipment: builder.query({
      query: (trackingNumber) => `/tracking/${trackingNumber}`,
      providesTags: ['TrackingEvent'],
    }),
    
    // Get shipment tracking events
    getTrackingEvents: builder.query({
      query: (shipmentId) => `/${shipmentId}/tracking-events`,
      providesTags: (_result, _error, shipmentId) => [
        { type: 'TrackingEvent', id: shipmentId }
      ],
    }),
    
    // Create new shipment
    createShipment: builder.mutation({
      query: (newShipment) => ({
        url: '',
        method: 'POST',
        body: newShipment,
      }),
      invalidatesTags: ['Shipment'],
    }),
    
    // Update shipment status
    updateShipmentStatus: builder.mutation({
      query: ({ id, status, notes }) => ({
        url: `/${id}/status`,
        method: 'PUT',
        body: { status, notes },
      }),
      invalidatesTags: (_result, _error, { id }) => [
        { type: 'Shipment', id },
        { type: 'TrackingEvent', id },
      ],
    }),
    
    // Get shipment analytics
    getShipmentAnalytics: builder.query({
      query: (timeRange = '30d') => `/analytics?timeRange=${timeRange}`,
      providesTags: ['ShipmentStats'],
    }),
    
    // Get delivery performance metrics
    getDeliveryPerformance: builder.query({
      query: ({ supplierId, timeRange = '30d' }) => {
        const params = new URLSearchParams({ timeRange });
        if (supplierId) params.append('supplierId', supplierId);
        return `/performance?${params}`;
      },
      providesTags: ['ShipmentStats'],
    }),
    
    // Get risk alerts for shipments
    getRiskAlerts: builder.query({
      query: (severity = 'all') => `/risk-alerts?severity=${severity}`,
      providesTags: ['ShipmentStats'],
    }),
    
    // Bulk update shipments
    bulkUpdateShipments: builder.mutation({
      query: (updates) => ({
        url: '/bulk-update',
        method: 'POST',
        body: updates,
      }),
      invalidatesTags: ['Shipment'],
    }),
  }),
});

export const {
  useGetShipmentsQuery,
  useGetShipmentQuery,
  useTrackShipmentQuery,
  useGetTrackingEventsQuery,
  useCreateShipmentMutation,
  useUpdateShipmentStatusMutation,
  useGetShipmentAnalyticsQuery,
  useGetDeliveryPerformanceQuery,
  useGetRiskAlertsQuery,
  useBulkUpdateShipmentsMutation,
  useLazyGetShipmentQuery,
  useLazyTrackShipmentQuery,
} = shipmentApi;