-- Database migration to add missing columns and tables for enhanced security features
-- Run this after the initial schema.sql

-- Add missing columns to users table for enhanced security
ALTER TABLE users ADD COLUMN IF NOT EXISTS username VARCHAR(50) UNIQUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_hash VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS email_verified BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS failed_login_attempts INTEGER DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_reset_token VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_reset_token_expires TIMESTAMP;

-- Update role enum to include AUDITOR
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;
ALTER TABLE users ADD CONSTRAINT users_role_check 
    CHECK (role IN ('ADMIN', 'SUPPLY_MANAGER', 'VIEWER', 'AUDITOR'));

-- Make firebase_uid nullable for JWT users
ALTER TABLE users ALTER COLUMN firebase_uid DROP NOT NULL;

-- Create user_sessions table for JWT session management
CREATE TABLE IF NOT EXISTS user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE,
    ip_address VARCHAR(45), -- IPv6 compatible
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create audit_log table for comprehensive audit logging
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    table_name VARCHAR(100) NOT NULL,
    operation VARCHAR(20) NOT NULL, -- INSERT, UPDATE, DELETE
    record_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create email_verification_tokens table for email verification
CREATE TABLE IF NOT EXISTS email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create risk_alerts table for risk notifications
CREATE TABLE IF NOT EXISTS risk_alerts (
    id BIGSERIAL PRIMARY KEY,
    alert_type VARCHAR(50) NOT NULL, -- SUPPLIER_RISK, SHIPMENT_DELAY, COMPLIANCE, etc.
    severity VARCHAR(20) NOT NULL CHECK (severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    source_table VARCHAR(100),
    source_id BIGINT,
    threshold_value DECIMAL(15, 2),
    actual_value DECIMAL(15, 2),
    is_acknowledged BOOLEAN DEFAULT FALSE,
    acknowledged_by BIGINT REFERENCES users(id),
    acknowledged_at TIMESTAMP,
    is_resolved BOOLEAN DEFAULT FALSE,
    resolved_by BIGINT REFERENCES users(id),
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create notification_preferences table
CREATE TABLE IF NOT EXISTS notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    notification_type VARCHAR(50) NOT NULL,
    email_enabled BOOLEAN DEFAULT TRUE,
    sms_enabled BOOLEAN DEFAULT FALSE,
    in_app_enabled BOOLEAN DEFAULT TRUE,
    threshold_value DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, notification_type)
);

-- Add unique constraints for security
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_email_unique ON users(email);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_username_unique ON users(username) WHERE username IS NOT NULL;

-- Add indexes for new columns
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email_verified ON users(email_verified);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_failed_login_attempts ON users(failed_login_attempts);
CREATE INDEX IF NOT EXISTS idx_users_locked_until ON users(locked_until);
CREATE INDEX IF NOT EXISTS idx_users_password_reset_token ON users(password_reset_token);
CREATE INDEX IF NOT EXISTS idx_users_last_login_at ON users(last_login_at);

-- User sessions indexes
CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_token_hash ON user_sessions(token_hash);
CREATE INDEX IF NOT EXISTS idx_user_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_user_sessions_is_revoked ON user_sessions(is_revoked);

-- Audit log indexes
CREATE INDEX IF NOT EXISTS idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_table_name ON audit_log(table_name);
CREATE INDEX IF NOT EXISTS idx_audit_log_operation ON audit_log(operation);
CREATE INDEX IF NOT EXISTS idx_audit_log_timestamp ON audit_log(timestamp);

-- Risk alerts indexes
CREATE INDEX IF NOT EXISTS idx_risk_alerts_type ON risk_alerts(alert_type);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_severity ON risk_alerts(severity);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_source ON risk_alerts(source_table, source_id);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_acknowledged ON risk_alerts(is_acknowledged);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_resolved ON risk_alerts(is_resolved);
CREATE INDEX IF NOT EXISTS idx_risk_alerts_created_at ON risk_alerts(created_at);

-- Email verification tokens indexes
CREATE INDEX IF NOT EXISTS idx_email_verification_user_id ON email_verification_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_email_verification_token ON email_verification_tokens(token);
CREATE INDEX IF NOT EXISTS idx_email_verification_expires_at ON email_verification_tokens(expires_at);

-- Notification preferences indexes
CREATE INDEX IF NOT EXISTS idx_notification_prefs_user_id ON notification_preferences(user_id);
CREATE INDEX IF NOT EXISTS idx_notification_prefs_type ON notification_preferences(notification_type);

-- Add triggers for audit logging
CREATE OR REPLACE FUNCTION log_table_changes()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (table_name, operation, record_id, old_values, timestamp)
        VALUES (TG_TABLE_NAME, TG_OP, OLD.id, to_jsonb(OLD), CURRENT_TIMESTAMP);
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (table_name, operation, record_id, old_values, new_values, timestamp)
        VALUES (TG_TABLE_NAME, TG_OP, NEW.id, to_jsonb(OLD), to_jsonb(NEW), CURRENT_TIMESTAMP);
        RETURN NEW;
    ELSIF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (table_name, operation, record_id, new_values, timestamp)
        VALUES (TG_TABLE_NAME, TG_OP, NEW.id, to_jsonb(NEW), CURRENT_TIMESTAMP);
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Add audit triggers for important tables
DROP TRIGGER IF EXISTS audit_users_trigger ON users;
CREATE TRIGGER audit_users_trigger
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION log_table_changes();

DROP TRIGGER IF EXISTS audit_suppliers_trigger ON suppliers;
CREATE TRIGGER audit_suppliers_trigger
    AFTER INSERT OR UPDATE OR DELETE ON suppliers
    FOR EACH ROW EXECUTE FUNCTION log_table_changes();

DROP TRIGGER IF EXISTS audit_shipments_trigger ON shipments;
CREATE TRIGGER audit_shipments_trigger
    AFTER INSERT OR UPDATE OR DELETE ON shipments
    FOR EACH ROW EXECUTE FUNCTION log_table_changes();

-- Update notification preferences trigger
DROP TRIGGER IF EXISTS update_notification_prefs_updated_at ON notification_preferences;
CREATE TRIGGER update_notification_prefs_updated_at
    BEFORE UPDATE ON notification_preferences
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Function to clean up expired tokens and sessions
CREATE OR REPLACE FUNCTION cleanup_expired_tokens()
RETURNS void AS $$
BEGIN
    -- Clean up expired password reset tokens
    UPDATE users SET 
        password_reset_token = NULL,
        password_reset_token_expires = NULL
    WHERE password_reset_token_expires < CURRENT_TIMESTAMP;
    
    -- Clean up expired email verification tokens
    DELETE FROM email_verification_tokens WHERE expires_at < CURRENT_TIMESTAMP;
    
    -- Mark expired sessions as revoked
    UPDATE user_sessions SET is_revoked = TRUE WHERE expires_at < CURRENT_TIMESTAMP AND is_revoked = FALSE;
    
    -- Delete old revoked sessions (older than 7 days)
    DELETE FROM user_sessions WHERE is_revoked = TRUE AND created_at < CURRENT_TIMESTAMP - INTERVAL '7 days';
    
    -- Delete old audit logs (older than 1 year)
    DELETE FROM audit_log WHERE timestamp < CURRENT_TIMESTAMP - INTERVAL '1 year';
END;
$$ LANGUAGE plpgsql;

-- Create a scheduled job to run cleanup (requires pg_cron extension)
-- SELECT cron.schedule('cleanup-expired-tokens', '0 2 * * *', 'SELECT cleanup_expired_tokens();');

-- Insert default notification types
INSERT INTO notification_preferences (user_id, notification_type, email_enabled, sms_enabled, in_app_enabled)
SELECT id, 'RISK_ALERT', true, false, true FROM users
ON CONFLICT (user_id, notification_type) DO NOTHING;

INSERT INTO notification_preferences (user_id, notification_type, email_enabled, sms_enabled, in_app_enabled)
SELECT id, 'SHIPMENT_DELAY', true, false, true FROM users
ON CONFLICT (user_id, notification_type) DO NOTHING;

INSERT INTO notification_preferences (user_id, notification_type, email_enabled, sms_enabled, in_app_enabled)
SELECT id, 'DELIVERY_CONFIRMATION', false, false, true FROM users
ON CONFLICT (user_id, notification_type) DO NOTHING;