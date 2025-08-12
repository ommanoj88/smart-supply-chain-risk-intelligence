-- Performance optimization indexes for Smart Supply Chain Risk Intelligence
-- These indexes are designed to improve query performance for the most common operations

-- Enhanced User table indexes
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_failed_login_attempts ON users(failed_login_attempts);
CREATE INDEX IF NOT EXISTS idx_users_locked_until ON users(locked_until);
CREATE INDEX IF NOT EXISTS idx_users_password_reset_token ON users(password_reset_token);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_users_last_login_at ON users(last_login_at);

-- User Sessions table indexes
CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_token_hash ON user_sessions(token_hash);
CREATE INDEX IF NOT EXISTS idx_user_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_user_sessions_is_revoked ON user_sessions(is_revoked);
CREATE INDEX IF NOT EXISTS idx_user_sessions_created_at ON user_sessions(created_at);

-- Composite index for active sessions
CREATE INDEX IF NOT EXISTS idx_user_sessions_active ON user_sessions(user_id, is_revoked, expires_at) 
    WHERE is_revoked = false AND expires_at > now();

-- Enhanced Suppliers table indexes
CREATE INDEX IF NOT EXISTS idx_suppliers_supplier_code ON suppliers(supplier_code);
CREATE INDEX IF NOT EXISTS idx_suppliers_preferred ON suppliers(preferred_supplier);
CREATE INDEX IF NOT EXISTS idx_suppliers_strategic ON suppliers(strategic_supplier);
CREATE INDEX IF NOT EXISTS idx_suppliers_created_by ON suppliers(created_by);
CREATE INDEX IF NOT EXISTS idx_suppliers_updated_by ON suppliers(updated_by);
CREATE INDEX IF NOT EXISTS idx_suppliers_updated_at ON suppliers(updated_at);
CREATE INDEX IF NOT EXISTS idx_suppliers_industry ON suppliers(industry);
CREATE INDEX IF NOT EXISTS idx_suppliers_business_type ON suppliers(business_type);

-- Risk-based indexes
CREATE INDEX IF NOT EXISTS idx_suppliers_financial_risk ON suppliers(financial_risk_score);
CREATE INDEX IF NOT EXISTS idx_suppliers_operational_risk ON suppliers(operational_risk_score);
CREATE INDEX IF NOT EXISTS idx_suppliers_compliance_risk ON suppliers(compliance_risk_score);
CREATE INDEX IF NOT EXISTS idx_suppliers_geographic_risk ON suppliers(geographic_risk_score);

-- Performance KPI indexes
CREATE INDEX IF NOT EXISTS idx_suppliers_on_time_delivery ON suppliers(on_time_delivery_rate);
CREATE INDEX IF NOT EXISTS idx_suppliers_quality_rating ON suppliers(quality_rating);

-- Composite indexes for common queries
CREATE INDEX IF NOT EXISTS idx_suppliers_status_risk ON suppliers(status, overall_risk_score);
CREATE INDEX IF NOT EXISTS idx_suppliers_country_status ON suppliers(country, status);
CREATE INDEX IF NOT EXISTS idx_suppliers_tier_status ON suppliers(tier, status);

-- Enhanced Shipments table indexes
CREATE INDEX IF NOT EXISTS idx_shipments_tracking_number ON shipments(tracking_number);
CREATE INDEX IF NOT EXISTS idx_shipments_supplier_id ON shipments(supplier_id);
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);
CREATE INDEX IF NOT EXISTS idx_shipments_carrier_name ON shipments(carrier_name);
CREATE INDEX IF NOT EXISTS idx_shipments_ship_date ON shipments(ship_date);
CREATE INDEX IF NOT EXISTS idx_shipments_estimated_delivery ON shipments(estimated_delivery_date);
CREATE INDEX IF NOT EXISTS idx_shipments_actual_delivery ON shipments(actual_delivery_date);
CREATE INDEX IF NOT EXISTS idx_shipments_created_at ON shipments(created_at);
CREATE INDEX IF NOT EXISTS idx_shipments_updated_at ON shipments(updated_at);
CREATE INDEX IF NOT EXISTS idx_shipments_origin_country ON shipments(origin_country);
CREATE INDEX IF NOT EXISTS idx_shipments_destination_country ON shipments(destination_country);
CREATE INDEX IF NOT EXISTS idx_shipments_risk_score ON shipments(risk_score);

-- Composite indexes for common shipment queries
CREATE INDEX IF NOT EXISTS idx_shipments_status_supplier ON shipments(status, supplier_id);
CREATE INDEX IF NOT EXISTS idx_shipments_date_range ON shipments(ship_date, status);
CREATE INDEX IF NOT EXISTS idx_shipments_carrier_status ON shipments(carrier_name, status);
CREATE INDEX IF NOT EXISTS idx_shipments_supplier_date ON shipments(supplier_id, ship_date);

-- Shipment Tracking Events indexes
CREATE INDEX IF NOT EXISTS idx_tracking_events_shipment_id ON shipment_tracking_events(shipment_id);
CREATE INDEX IF NOT EXISTS idx_tracking_events_timestamp ON shipment_tracking_events(event_timestamp);
CREATE INDEX IF NOT EXISTS idx_tracking_events_type ON shipment_tracking_events(event_type);
CREATE INDEX IF NOT EXISTS idx_tracking_events_exception ON shipment_tracking_events(is_exception);
CREATE INDEX IF NOT EXISTS idx_tracking_events_location ON shipment_tracking_events(location_country, location_city);

-- Composite index for event timeline queries
CREATE INDEX IF NOT EXISTS idx_tracking_events_shipment_timeline ON shipment_tracking_events(shipment_id, event_timestamp);

-- Shipment Items indexes
CREATE INDEX IF NOT EXISTS idx_shipment_items_shipment_id ON shipment_items(shipment_id);
CREATE INDEX IF NOT EXISTS idx_shipment_items_name ON shipment_items(item_name);

-- Audit Log indexes (if audit_log table exists)
CREATE INDEX IF NOT EXISTS idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_table_name ON audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_audit_log_operation ON audit_log(operation);
CREATE INDEX IF NOT EXISTS idx_audit_log_timestamp ON audit_log(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_log_ip_address ON audit_log(ip_address);

-- Composite indexes for audit queries
CREATE INDEX IF NOT EXISTS idx_audit_log_user_date ON audit_log(user_id, timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_log_table_date ON audit_log(table_name, timestamp);

-- Carriers table indexes
CREATE INDEX IF NOT EXISTS idx_carriers_code ON carriers(code);
CREATE INDEX IF NOT EXISTS idx_carriers_name ON carriers(name);
CREATE INDEX IF NOT EXISTS idx_carriers_is_active ON carriers(is_active);
CREATE INDEX IF NOT EXISTS idx_carriers_reliability ON carriers(reliability_score);

-- Supplier Performance History indexes
CREATE INDEX IF NOT EXISTS idx_perf_history_supplier_date ON supplier_performance_history(supplier_id, performance_date);
CREATE INDEX IF NOT EXISTS idx_perf_history_overall_score ON supplier_performance_history(overall_score);

-- Supplier Documents indexes
CREATE INDEX IF NOT EXISTS idx_supplier_docs_type ON supplier_documents(document_type);
CREATE INDEX IF NOT EXISTS idx_supplier_docs_upload_date ON supplier_documents(upload_date);
CREATE INDEX IF NOT EXISTS idx_supplier_docs_uploaded_by ON supplier_documents(uploaded_by);

-- Partitioning suggestions (commented out, implement based on data volume)
-- For high-volume tables, consider partitioning by date:
-- PARTITION BY RANGE (created_at) for shipment_tracking_events
-- PARTITION BY RANGE (performance_date) for supplier_performance_history

-- Statistics update (run periodically for optimal performance)
-- ANALYZE users, suppliers, shipments, shipment_tracking_events, supplier_performance_history;