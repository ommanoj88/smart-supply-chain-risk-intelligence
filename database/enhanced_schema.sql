-- Enhanced Schema for Smart Supply Chain Risk Intelligence Platform
-- This file adds comprehensive user management, notification system, and integration framework

-- =====================================
-- ENHANCED USER MANAGEMENT SYSTEM
-- =====================================

-- Enhanced roles and permissions system
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_system_role BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    resource VARCHAR(50) NOT NULL, -- SUPPLIER, SHIPMENT, USER, REPORT, etc.
    action VARCHAR(50) NOT NULL, -- CREATE, READ, UPDATE, DELETE, APPROVE, etc.
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Enhanced user management with support for multiple roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    assigned_by BIGINT REFERENCES users(id),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (user_id, role_id)
);

-- Multi-factor authentication support
CREATE TABLE IF NOT EXISTS user_mfa_settings (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    mfa_type VARCHAR(20) NOT NULL, -- SMS, EMAIL, TOTP, HARDWARE_KEY
    phone_number VARCHAR(20),
    secret_key TEXT, -- Encrypted TOTP secret
    backup_codes TEXT[], -- Array of encrypted backup codes
    is_enabled BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Password reset and security
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Account security tracking
CREATE TABLE IF NOT EXISTS user_security_events (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    event_type VARCHAR(50) NOT NULL, -- LOGIN_SUCCESS, LOGIN_FAILED, PASSWORD_CHANGED, etc.
    ip_address INET,
    user_agent TEXT,
    location VARCHAR(255),
    device_info TEXT,
    success BOOLEAN,
    details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- NOTIFICATION AND ALERT SYSTEM
-- =====================================

-- Notification channels and templates
CREATE TABLE IF NOT EXISTS notification_channels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL, -- EMAIL, SMS, SLACK, TEAMS, WEBHOOK
    display_name VARCHAR(100) NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    configuration JSONB, -- Channel-specific configuration
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notification_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    category VARCHAR(50) NOT NULL, -- RISK_ALERT, SHIPMENT_UPDATE, USER_ACTION, etc.
    subject_template TEXT,
    body_template TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User notification preferences
CREATE TABLE IF NOT EXISTS user_notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    channel_id BIGINT REFERENCES notification_channels(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL, -- RISK_ALERTS, SHIPMENT_UPDATES, etc.
    is_enabled BOOLEAN DEFAULT TRUE,
    delivery_settings JSONB, -- Channel-specific delivery settings
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    timezone VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, channel_id, category)
);

-- Notification queue and history
CREATE TABLE IF NOT EXISTS notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    channel_id BIGINT REFERENCES notification_channels(id),
    template_id BIGINT REFERENCES notification_templates(id),
    category VARCHAR(50) NOT NULL,
    priority VARCHAR(20) DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    subject TEXT,
    content TEXT NOT NULL,
    metadata JSONB,
    
    -- Delivery tracking
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, SENT, DELIVERED, FAILED, RETRYING
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    failed_at TIMESTAMP,
    failure_reason TEXT,
    retry_count INTEGER DEFAULT 0,
    max_retries INTEGER DEFAULT 3,
    
    -- Scheduling
    scheduled_for TIMESTAMP,
    expires_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Risk alerts and monitoring
CREATE TABLE IF NOT EXISTS risk_alerts (
    id BIGSERIAL PRIMARY KEY,
    alert_type VARCHAR(50) NOT NULL, -- SUPPLIER_RISK, SHIPMENT_DELAY, GEOPOLITICAL, etc.
    severity VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    title VARCHAR(255) NOT NULL,
    description TEXT,
    
    -- Source information
    source_type VARCHAR(50), -- SUPPLIER, SHIPMENT, EXTERNAL_DATA
    source_id BIGINT, -- ID of the related entity
    
    -- Risk details
    risk_score INTEGER, -- 0-100
    impact_assessment TEXT,
    recommended_actions TEXT[],
    
    -- Alert status
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, ACKNOWLEDGED, RESOLVED, DISMISSED
    acknowledged_by BIGINT REFERENCES users(id),
    acknowledged_at TIMESTAMP,
    resolved_by BIGINT REFERENCES users(id),
    resolved_at TIMESTAMP,
    resolution_notes TEXT,
    
    -- Metadata
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- INTEGRATION AND API MANAGEMENT
-- =====================================

-- External system integrations
CREATE TABLE IF NOT EXISTS external_integrations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    integration_type VARCHAR(50) NOT NULL, -- ERP, CARRIER, WEATHER, NEWS, etc.
    system_name VARCHAR(100),
    description TEXT,
    
    -- Connection details
    endpoint_url VARCHAR(500),
    api_version VARCHAR(20),
    authentication_type VARCHAR(50), -- API_KEY, OAUTH2, BASIC, JWT
    credentials JSONB, -- Encrypted credentials
    
    -- Configuration
    configuration JSONB,
    sync_frequency_minutes INTEGER DEFAULT 60,
    
    -- Status and monitoring
    is_active BOOLEAN DEFAULT TRUE,
    last_sync_at TIMESTAMP,
    last_sync_status VARCHAR(20), -- SUCCESS, FAILED, PARTIAL
    last_error_message TEXT,
    sync_count BIGINT DEFAULT 0,
    error_count BIGINT DEFAULT 0,
    
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Integration sync logs
CREATE TABLE IF NOT EXISTS integration_sync_logs (
    id BIGSERIAL PRIMARY KEY,
    integration_id BIGINT REFERENCES external_integrations(id) ON DELETE CASCADE,
    sync_type VARCHAR(50), -- FULL, INCREMENTAL, MANUAL
    status VARCHAR(20) NOT NULL, -- STARTED, SUCCESS, FAILED, PARTIAL
    
    -- Sync metrics
    records_processed INTEGER DEFAULT 0,
    records_successful INTEGER DEFAULT 0,
    records_failed INTEGER DEFAULT 0,
    duration_ms BIGINT,
    
    -- Details
    error_message TEXT,
    details JSONB,
    
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- API usage tracking and rate limiting
CREATE TABLE IF NOT EXISTS api_keys (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    key_hash VARCHAR(255) UNIQUE NOT NULL, -- SHA-256 hash of the actual key
    key_prefix VARCHAR(10) NOT NULL, -- First 8 characters for identification
    
    -- Permissions and limits
    scopes TEXT[], -- Array of allowed API scopes
    rate_limit_per_minute INTEGER DEFAULT 100,
    rate_limit_per_hour INTEGER DEFAULT 1000,
    rate_limit_per_day INTEGER DEFAULT 10000,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    last_used_at TIMESTAMP,
    usage_count BIGINT DEFAULT 0,
    
    -- Expiration
    expires_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS api_usage_logs (
    id BIGSERIAL PRIMARY KEY,
    api_key_id BIGINT REFERENCES api_keys(id),
    endpoint VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    ip_address INET,
    user_agent TEXT,
    
    -- Response details
    status_code INTEGER,
    response_time_ms INTEGER,
    request_size_bytes INTEGER,
    response_size_bytes INTEGER,
    
    -- Rate limiting
    rate_limit_remaining INTEGER,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- ANALYTICS AND KPI TRACKING
-- =====================================

-- KPI definitions and calculations
CREATE TABLE IF NOT EXISTS kpi_definitions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50), -- SUPPLIER, SHIPMENT, RISK, FINANCIAL
    calculation_method TEXT, -- SQL query or calculation description
    unit VARCHAR(50), -- PERCENTAGE, DAYS, USD, COUNT, etc.
    target_value DECIMAL(15, 4),
    is_higher_better BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS kpi_measurements (
    id BIGSERIAL PRIMARY KEY,
    kpi_definition_id BIGINT REFERENCES kpi_definitions(id) ON DELETE CASCADE,
    measurement_date DATE NOT NULL,
    value DECIMAL(15, 4) NOT NULL,
    
    -- Context
    entity_type VARCHAR(50), -- SUPPLIER, SHIPMENT, GLOBAL
    entity_id BIGINT, -- ID of the specific entity (if applicable)
    
    -- Metadata
    calculation_details JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(kpi_definition_id, measurement_date, entity_type, entity_id)
);

-- Custom dashboards and reports
CREATE TABLE IF NOT EXISTS custom_dashboards (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    layout_configuration JSONB, -- Widget layout and configuration
    is_default BOOLEAN DEFAULT FALSE,
    is_shared BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =====================================
-- INDEXES FOR PERFORMANCE
-- =====================================

-- User management indexes
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_user_mfa_user_id ON user_mfa_settings(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_user_security_events_user_id ON user_security_events(user_id);
CREATE INDEX IF NOT EXISTS idx_user_security_events_created_at ON user_security_events(created_at);

-- Notification system indexes
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_category ON notifications(category);
CREATE INDEX IF NOT EXISTS idx_notifications_scheduled_for ON notifications(scheduled_for);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_status ON risk_alerts(status);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_severity ON risk_alerts(severity);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_source ON risk_alerts(source_type, source_id);

-- Integration indexes
CREATE INDEX IF NOT EXISTS idx_external_integrations_type ON external_integrations(integration_type);
CREATE INDEX IF NOT EXISTS idx_integration_sync_logs_integration_id ON integration_sync_logs(integration_id);
CREATE INDEX IF NOT EXISTS idx_api_keys_user_id ON api_keys(user_id);
CREATE INDEX IF NOT EXISTS idx_api_keys_hash ON api_keys(key_hash);
CREATE INDEX IF NOT EXISTS idx_api_usage_logs_api_key_id ON api_usage_logs(api_key_id);
CREATE INDEX IF NOT EXISTS idx_api_usage_logs_created_at ON api_usage_logs(created_at);

-- Analytics indexes
CREATE INDEX IF NOT EXISTS idx_kpi_measurements_definition_date ON kpi_measurements(kpi_definition_id, measurement_date);
CREATE INDEX IF NOT EXISTS idx_custom_dashboards_user_id ON custom_dashboards(user_id);

-- =====================================
-- TRIGGERS FOR ENHANCED TABLES
-- =====================================

-- Update triggers for timestamp columns
DROP TRIGGER IF EXISTS update_roles_updated_at ON roles;
CREATE TRIGGER update_roles_updated_at
    BEFORE UPDATE ON roles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_user_mfa_settings_updated_at ON user_mfa_settings;
CREATE TRIGGER update_user_mfa_settings_updated_at
    BEFORE UPDATE ON user_mfa_settings
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_notification_channels_updated_at ON notification_channels;
CREATE TRIGGER update_notification_channels_updated_at
    BEFORE UPDATE ON notification_channels
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_notification_templates_updated_at ON notification_templates;
CREATE TRIGGER update_notification_templates_updated_at
    BEFORE UPDATE ON notification_templates
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_notifications_updated_at ON notifications;
CREATE TRIGGER update_notifications_updated_at
    BEFORE UPDATE ON notifications
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_risk_alerts_updated_at ON risk_alerts;
CREATE TRIGGER update_risk_alerts_updated_at
    BEFORE UPDATE ON risk_alerts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

DROP TRIGGER IF EXISTS update_external_integrations_updated_at ON external_integrations;
CREATE TRIGGER update_external_integrations_updated_at
    BEFORE UPDATE ON external_integrations
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================
-- INITIAL DATA FOR ENHANCED SYSTEM
-- =====================================

-- Insert default roles
INSERT INTO roles (name, display_name, description, is_system_role) VALUES 
    ('SUPER_ADMIN', 'Super Administrator', 'Platform management and tenant configuration', true),
    ('ORG_ADMIN', 'Organization Administrator', 'User management and organization settings', true),
    ('SUPPLY_MANAGER', 'Supply Chain Manager', 'Full supply chain operations access', true),
    ('ANALYST', 'Business Analyst', 'Analytics and reporting access', true),
    ('VIEWER', 'Viewer', 'Read-only dashboard access', true),
    ('SUPPLIER_USER', 'Supplier Portal User', 'Limited supplier-specific access', true)
ON CONFLICT (name) DO NOTHING;

-- Insert core permissions
INSERT INTO permissions (name, resource, action, description) VALUES 
    ('supplier.create', 'SUPPLIER', 'CREATE', 'Create new suppliers'),
    ('supplier.read', 'SUPPLIER', 'READ', 'View supplier information'),
    ('supplier.update', 'SUPPLIER', 'UPDATE', 'Update supplier information'),
    ('supplier.delete', 'SUPPLIER', 'DELETE', 'Delete suppliers'),
    ('shipment.create', 'SHIPMENT', 'CREATE', 'Create new shipments'),
    ('shipment.read', 'SHIPMENT', 'READ', 'View shipment information'),
    ('shipment.update', 'SHIPMENT', 'UPDATE', 'Update shipment information'),
    ('shipment.delete', 'SHIPMENT', 'DELETE', 'Delete shipments'),
    ('user.create', 'USER', 'CREATE', 'Create new users'),
    ('user.read', 'USER', 'READ', 'View user information'),
    ('user.update', 'USER', 'UPDATE', 'Update user information'),
    ('user.delete', 'USER', 'DELETE', 'Delete users'),
    ('report.create', 'REPORT', 'CREATE', 'Create custom reports'),
    ('report.read', 'REPORT', 'READ', 'View reports'),
    ('alert.manage', 'ALERT', 'MANAGE', 'Manage risk alerts and notifications'),
    ('integration.manage', 'INTEGRATION', 'MANAGE', 'Manage external integrations')
ON CONFLICT (name) DO NOTHING;

-- Insert default notification channels
INSERT INTO notification_channels (name, display_name, is_enabled) VALUES 
    ('EMAIL', 'Email Notifications', true),
    ('SMS', 'SMS Notifications', true),
    ('SLACK', 'Slack Integration', true),
    ('TEAMS', 'Microsoft Teams', true),
    ('WEBHOOK', 'Webhook Notifications', true),
    ('PUSH', 'Push Notifications', true)
ON CONFLICT (name) DO NOTHING;

-- Insert default notification templates
INSERT INTO notification_templates (name, category, subject_template, body_template, is_active) VALUES 
    ('risk_alert_high', 'RISK_ALERT', 'High Risk Alert: {{title}}', 'A high-priority risk alert has been detected: {{description}}. Please review immediately.', true),
    ('shipment_delayed', 'SHIPMENT_UPDATE', 'Shipment Delay Alert: {{tracking_number}}', 'Shipment {{tracking_number}} is experiencing delays. Expected delay: {{delay_hours}} hours.', true),
    ('supplier_performance', 'SUPPLIER_UPDATE', 'Supplier Performance Alert: {{supplier_name}}', 'Supplier {{supplier_name}} performance has declined. Current score: {{score}}.', true),
    ('password_reset', 'USER_ACTION', 'Password Reset Request', 'A password reset has been requested for your account. Click the link to reset: {{reset_link}}', true)
ON CONFLICT (name) DO NOTHING;

-- Insert sample KPI definitions
INSERT INTO kpi_definitions (name, display_name, description, category, unit, target_value, is_higher_better) VALUES 
    ('on_time_delivery_rate', 'On-Time Delivery Rate', 'Percentage of shipments delivered on time', 'SHIPMENT', 'PERCENTAGE', 95.0, true),
    ('supplier_risk_score', 'Average Supplier Risk Score', 'Average risk score across all suppliers', 'SUPPLIER', 'SCORE', 25.0, false),
    ('shipment_delay_hours', 'Average Shipment Delay', 'Average delay in hours for delayed shipments', 'SHIPMENT', 'HOURS', 24.0, false),
    ('cost_savings', 'Monthly Cost Savings', 'Cost savings achieved through platform usage', 'FINANCIAL', 'USD', 10000.0, true)
ON CONFLICT (name) DO NOTHING;