-- Database: smart_supply_chain
-- Create database if it doesn't exist
-- CREATE DATABASE smart_supply_chain;

-- Connect to the database
-- \c smart_supply_chain;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    firebase_uid VARCHAR(128) UNIQUE NOT NULL,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'VIEWER' CHECK (role IN ('ADMIN', 'SUPPLY_MANAGER', 'VIEWER')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_firebase_uid ON users(firebase_uid);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Enhanced Suppliers table
CREATE TABLE IF NOT EXISTS suppliers (
    id BIGSERIAL PRIMARY KEY,
    supplier_code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    tier VARCHAR(20) DEFAULT 'SECONDARY' CHECK (tier IN ('PRIMARY', 'SECONDARY', 'BACKUP')),
    
    -- Contact Information
    primary_contact_name VARCHAR(255),
    primary_contact_email VARCHAR(255),
    primary_contact_phone VARCHAR(50),
    secondary_contact_name VARCHAR(255),
    secondary_contact_email VARCHAR(255),
    secondary_contact_phone VARCHAR(50),
    website VARCHAR(255),
    
    -- Address Information
    street_address TEXT,
    city VARCHAR(100),
    state_province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    
    -- Business Information
    industry VARCHAR(100),
    business_type VARCHAR(50),
    annual_revenue DECIMAL(15, 2),
    employee_count INTEGER,
    years_in_business INTEGER,
    
    -- Risk and Performance Metrics
    overall_risk_score INTEGER DEFAULT 0 CHECK (overall_risk_score >= 0 AND overall_risk_score <= 100),
    financial_risk_score INTEGER DEFAULT 0,
    operational_risk_score INTEGER DEFAULT 0,
    compliance_risk_score INTEGER DEFAULT 0,
    geographic_risk_score INTEGER DEFAULT 0,
    
    -- Performance KPIs
    on_time_delivery_rate DECIMAL(5, 2) DEFAULT 0.00,
    quality_rating DECIMAL(5, 2) DEFAULT 0.00,
    cost_competitiveness_score INTEGER DEFAULT 0,
    responsiveness_score INTEGER DEFAULT 0,
    
    -- Certifications and Compliance
    iso_certifications TEXT[], -- Array of ISO certifications
    compliance_certifications TEXT[],
    last_audit_date DATE,
    next_audit_due_date DATE,
    
    -- Financial Information
    credit_rating VARCHAR(10),
    payment_terms VARCHAR(100),
    currency VARCHAR(3) DEFAULT 'USD',
    
    -- Status and Metadata
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'PENDING', 'BLOCKED')),
    preferred_supplier BOOLEAN DEFAULT FALSE,
    strategic_supplier BOOLEAN DEFAULT FALSE,
    
    -- Audit Fields
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Search and Performance
    search_vector tsvector,
    
    -- Indexes for performance
    CONSTRAINT unique_supplier_code UNIQUE (supplier_code)
);

-- Supplier Performance History
CREATE TABLE IF NOT EXISTS supplier_performance_history (
    id BIGSERIAL PRIMARY KEY,
    supplier_id BIGINT REFERENCES suppliers(id) ON DELETE CASCADE,
    performance_date DATE NOT NULL,
    on_time_delivery_rate DECIMAL(5, 2),
    quality_score DECIMAL(5, 2),
    cost_score INTEGER,
    overall_score DECIMAL(5, 2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Supplier Documents
CREATE TABLE IF NOT EXISTS supplier_documents (
    id BIGSERIAL PRIMARY KEY,
    supplier_id BIGINT REFERENCES suppliers(id) ON DELETE CASCADE,
    document_name VARCHAR(255) NOT NULL,
    document_type VARCHAR(100),
    file_path VARCHAR(500),
    file_size BIGINT,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    uploaded_by BIGINT REFERENCES users(id)
);

-- Supplier Categories/Tags
CREATE TABLE IF NOT EXISTS supplier_categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    color VARCHAR(7), -- Hex color code
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Many-to-many relationship for supplier categories
CREATE TABLE IF NOT EXISTS supplier_category_mapping (
    supplier_id BIGINT REFERENCES suppliers(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES supplier_categories(id) ON DELETE CASCADE,
    PRIMARY KEY (supplier_id, category_id)
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_suppliers_name ON suppliers(name);
CREATE INDEX IF NOT EXISTS idx_suppliers_status ON suppliers(status);
CREATE INDEX IF NOT EXISTS idx_suppliers_risk_score ON suppliers(overall_risk_score);
CREATE INDEX IF NOT EXISTS idx_suppliers_location ON suppliers(country, city);
CREATE INDEX IF NOT EXISTS idx_suppliers_tier ON suppliers(tier);
CREATE INDEX IF NOT EXISTS idx_suppliers_search_vector ON suppliers USING GIN(search_vector);
CREATE INDEX IF NOT EXISTS idx_supplier_performance_supplier_id ON supplier_performance_history(supplier_id);
CREATE INDEX IF NOT EXISTS idx_supplier_performance_date ON supplier_performance_history(performance_date);
CREATE INDEX IF NOT EXISTS idx_supplier_documents_supplier_id ON supplier_documents(supplier_id);

-- Create trigger to automatically update the updated_at column for users
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_users_updated_at ON users;
CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create trigger for suppliers updated_at
DROP TRIGGER IF EXISTS update_suppliers_updated_at ON suppliers;
CREATE TRIGGER update_suppliers_updated_at
    BEFORE UPDATE ON suppliers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Function to update search vector for suppliers
CREATE OR REPLACE FUNCTION update_supplier_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector := to_tsvector('english', 
        COALESCE(NEW.name, '') || ' ' ||
        COALESCE(NEW.legal_name, '') || ' ' ||
        COALESCE(NEW.supplier_code, '') || ' ' ||
        COALESCE(NEW.industry, '') || ' ' ||
        COALESCE(NEW.city, '') || ' ' ||
        COALESCE(NEW.country, '') || ' ' ||
        COALESCE(NEW.business_type, '')
    );
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for search vector update
DROP TRIGGER IF EXISTS update_supplier_search_vector_trigger ON suppliers;
CREATE TRIGGER update_supplier_search_vector_trigger
    BEFORE INSERT OR UPDATE ON suppliers
    FOR EACH ROW
    EXECUTE FUNCTION update_supplier_search_vector();

-- Insert some default supplier categories
INSERT INTO supplier_categories (name, description, color) VALUES 
    ('Manufacturing', 'Manufacturing suppliers', '#0078D4'),
    ('Technology', 'Technology and software suppliers', '#1DB954'),
    ('Logistics', 'Shipping and logistics providers', '#FF6B35'),
    ('Raw Materials', 'Raw material suppliers', '#8B5CF6'),
    ('Services', 'Service providers', '#F59E0B')
ON CONFLICT (name) DO NOTHING;