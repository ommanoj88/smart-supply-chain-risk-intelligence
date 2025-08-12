import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';
import { supplierApi } from './api/supplierApi';
import { shipmentApi } from './api/shipmentApi';
import { authSlice } from './slices/authSlice';
import { uiSlice } from './slices/uiSlice';
import { dashboardSlice } from './slices/dashboardSlice';

/**
 * Enterprise Redux store configuration with RTK Query
 */
export const store = configureStore({
  reducer: {
    // RTK Query APIs
    [supplierApi.reducerPath]: supplierApi.reducer,
    [shipmentApi.reducerPath]: shipmentApi.reducer,
    
    // Slice reducers
    auth: authSlice.reducer,
    ui: uiSlice.reducer,
    dashboard: dashboardSlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST', 'persist/REHYDRATE'],
      },
    })
      .concat(supplierApi.middleware)
      .concat(shipmentApi.middleware),
  devTools: process.env.NODE_ENV !== 'production',
});

// Enable listener behavior for the store
setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;