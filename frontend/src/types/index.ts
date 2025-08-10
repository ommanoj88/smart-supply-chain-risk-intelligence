export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
}

export enum UserRole {
  ADMIN = 'ADMIN',
  SUPPLY_MANAGER = 'SUPPLY_MANAGER',
  VIEWER = 'VIEWER'
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  isActive?: boolean;
}

export interface Supplier {
  id: number;
  name: string;
  location: string;
  contactInfo?: string;
  riskScore: number;
  isActive?: boolean;
}

export enum ShipmentStatus {
  PENDING = 'PENDING',
  IN_TRANSIT = 'IN_TRANSIT',
  DELAYED = 'DELAYED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

export interface Shipment {
  id: number;
  supplierId: number;
  supplierName?: string;
  status: ShipmentStatus;
  origin: string;
  destination: string;
  estimatedArrival?: string;
  actualArrival?: string;
  trackingNumber?: string;
  description?: string;
  lastUpdate?: string;
}

export enum RiskLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export interface RiskPrediction {
  id: number;
  shipmentId: number;
  riskLevel: RiskLevel;
  probability: number;
  description?: string;
  predictionFactors?: string;
  createdAt: string;
}

export enum AlertSeverity {
  INFO = 'INFO',
  WARNING = 'WARNING',
  ERROR = 'ERROR',
  CRITICAL = 'CRITICAL'
}

export interface Alert {
  id: number;
  userId: number;
  message: string;
  severity: AlertSeverity;
  isAcknowledged: boolean;
  acknowledgedAt?: string;
  referenceId?: string;
  referenceType?: string;
  createdAt: string;
}

export interface DashboardStats {
  totalSuppliers: number;
  activeShipments: number;
  highRiskShipments: number;
  criticalAlerts: number;
  averageRiskScore: number;
}

export interface CreateSupplierRequest {
  name: string;
  location: string;
  contactInfo?: string;
}

export interface CreateShipmentRequest {
  supplierId: number;
  origin: string;
  destination: string;
  estimatedArrival?: string;
  description?: string;
}