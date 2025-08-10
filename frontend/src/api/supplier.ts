import type { ApiResponse, Supplier, CreateSupplierRequest } from '../types';
import apiClient from './client';

export const supplierApi = {
  getAllSuppliers: (): Promise<ApiResponse<Supplier[]>> =>
    apiClient.get('/suppliers'),

  getActiveSuppliers: (): Promise<ApiResponse<Supplier[]>> =>
    apiClient.get('/suppliers/active'),

  getSupplierById: (id: number): Promise<ApiResponse<Supplier>> =>
    apiClient.get(`/suppliers/${id}`),

  getHighRiskSuppliers: (): Promise<ApiResponse<Supplier[]>> =>
    apiClient.get('/suppliers/high-risk'),

  searchSuppliers: (params: { name?: string; location?: string }): Promise<ApiResponse<Supplier[]>> =>
    apiClient.get('/suppliers/search', params),

  createSupplier: (data: CreateSupplierRequest): Promise<ApiResponse<Supplier>> =>
    apiClient.post('/suppliers', data),

  updateSupplier: (id: number, data: Partial<Supplier>): Promise<ApiResponse<Supplier>> =>
    apiClient.put(`/suppliers/${id}`, data),

  updateSupplierRisk: (id: number, riskScore: number): Promise<ApiResponse<Supplier>> =>
    apiClient.put(`/suppliers/${id}/risk?riskScore=${riskScore}`),

  deleteSupplier: (id: number): Promise<ApiResponse<string>> =>
    apiClient.delete(`/suppliers/${id}`),
};