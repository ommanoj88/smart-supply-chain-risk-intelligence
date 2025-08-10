import type { ApiResponse, Shipment, CreateShipmentRequest, ShipmentStatus } from '../types';
import apiClient from './client';

export const shipmentApi = {
  getAllShipments: (): Promise<ApiResponse<Shipment[]>> =>
    apiClient.get('/shipments'),

  getShipmentById: (id: number): Promise<ApiResponse<Shipment>> =>
    apiClient.get(`/shipments/${id}`),

  getShipmentsByStatus: (status: ShipmentStatus): Promise<ApiResponse<Shipment[]>> =>
    apiClient.get(`/shipments/status/${status}`),

  getShipmentsBySupplier: (supplierId: number): Promise<ApiResponse<Shipment[]>> =>
    apiClient.get(`/shipments/supplier/${supplierId}`),

  getDelayedShipments: (): Promise<ApiResponse<Shipment[]>> =>
    apiClient.get('/shipments/delayed'),

  trackShipment: (trackingNumber: string): Promise<ApiResponse<Shipment[]>> =>
    apiClient.get(`/shipments/tracking/${trackingNumber}`),

  createShipment: (data: CreateShipmentRequest): Promise<ApiResponse<Shipment>> =>
    apiClient.post('/shipments', data),

  updateShipment: (id: number, data: Partial<Shipment>): Promise<ApiResponse<Shipment>> =>
    apiClient.put(`/shipments/${id}`, data),

  updateShipmentStatus: (id: number, status: ShipmentStatus): Promise<ApiResponse<Shipment>> =>
    apiClient.put(`/shipments/${id}/status?status=${status}`),

  deleteShipment: (id: number): Promise<ApiResponse<string>> =>
    apiClient.delete(`/shipments/${id}`),
};