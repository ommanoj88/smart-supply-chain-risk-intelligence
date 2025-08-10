import type { ApiResponse, User } from '../types';
import apiClient from './client';

export const authApi = {
  verifyToken: (): Promise<ApiResponse<User>> =>
    apiClient.post('/auth/verify'),

  getCurrentUser: (): Promise<ApiResponse<User>> =>
    apiClient.get('/auth/me'),

  checkHealth: (): Promise<ApiResponse<string>> =>
    apiClient.get('/auth/health'),
};