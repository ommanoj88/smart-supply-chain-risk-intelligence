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

-- =====================================
-- ADVANCED SHIPMENT TRACKING SCHEMA
-- =====================================

-- Carriers table (logistics providers)
CREATE TABLE IF NOT EXISTS carriers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    
    -- API Configuration
    api_endpoint VARCHAR(255),
    api_key_encrypted TEXT,
    webhook_url VARCHAR(255),
    
    -- Service capabilities
    services_offered TEXT[], -- Array of service types
    countries_supported TEXT[], -- Array of country codes
    tracking_url_template VARCHAR(500),
    
    -- Performance metrics
    avg_delivery_time_days DECIMAL(5, 2),
    on_time_percentage DECIMAL(5, 2),
    reliability_score INTEGER DEFAULT 0,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    last_sync_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shipments table (comprehensive tracking)
CREATE TABLE IF NOT EXISTS shipments (
    id BIGSERIAL PRIMARY KEY,
    tracking_number VARCHAR(100) UNIQUE NOT NULL,
    reference_number VARCHAR(100),
    supplier_id BIGINT REFERENCES suppliers(id),
    
    -- Shipment Details
    shipment_type VARCHAR(50) DEFAULT 'STANDARD', -- STANDARD, EXPRESS, FREIGHT
    service_level VARCHAR(50), -- NEXT_DAY, 2_DAY, GROUND, OCEAN, AIR
    weight_kg DECIMAL(10, 3),
    dimensions_length_cm DECIMAL(8, 2),
    dimensions_width_cm DECIMAL(8, 2),
    dimensions_height_cm DECIMAL(8, 2),
    declared_value DECIMAL(15, 2),
    currency VARCHAR(3) DEFAULT 'USD',
    
    -- Origin and Destination
    origin_name VARCHAR(255),
    origin_address TEXT,
    origin_city VARCHAR(100),
    origin_state VARCHAR(100),
    origin_country VARCHAR(100),
    origin_postal_code VARCHAR(20),
    origin_latitude DECIMAL(10, 8),
    origin_longitude DECIMAL(11, 8),
    
    destination_name VARCHAR(255),
    destination_address TEXT,
    destination_city VARCHAR(100),
    destination_state VARCHAR(100),
    destination_country VARCHAR(100),
    destination_postal_code VARCHAR(20),
    destination_latitude DECIMAL(10, 8),
    destination_longitude DECIMAL(11, 8),
    
    -- Carrier Information
    carrier_name VARCHAR(100) NOT NULL,
    carrier_service_code VARCHAR(50),
    carrier_tracking_url TEXT,
    
    -- Status and Timing
    status VARCHAR(50) DEFAULT 'CREATED', -- CREATED, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, EXCEPTION
    substatus VARCHAR(100), -- Detailed status from carrier
    ship_date TIMESTAMP,
    estimated_delivery_date TIMESTAMP,
    actual_delivery_date TIMESTAMP,
    transit_days INTEGER,
    
    -- Risk and Performance
    risk_score INTEGER DEFAULT 0 CHECK (risk_score >= 0 AND risk_score <= 100),
    delay_risk_probability DECIMAL(5, 2) DEFAULT 0.00,
    predicted_delay_hours INTEGER DEFAULT 0,
    on_time_performance BOOLEAN,
    
    -- Cost and Billing
    shipping_cost DECIMAL(10, 2),
    fuel_surcharge DECIMAL(10, 2),
    total_cost DECIMAL(10, 2),
    billed_weight_kg DECIMAL(10, 3),
    
    -- Environmental Impact
    carbon_footprint_kg DECIMAL(10, 3),
    
    -- Metadata
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Search optimization
    search_vector tsvector
);

-- Shipment tracking events (detailed timeline)
CREATE TABLE IF NOT EXISTS shipment_tracking_events (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT REFERENCES shipments(id) ON DELETE CASCADE,
    
    event_code VARCHAR(50) NOT NULL,
    event_description TEXT NOT NULL,
    event_timestamp TIMESTAMP NOT NULL,
    
    -- Location of event
    location_name VARCHAR(255),
    location_city VARCHAR(100),
    location_state VARCHAR(100),
    location_country VARCHAR(100),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    
    -- Event details
    event_type VARCHAR(50), -- PICKUP, TRANSIT, DELIVERY, EXCEPTION
    is_exception BOOLEAN DEFAULT FALSE,
    exception_reason TEXT,
    
    -- Carrier data
    carrier_event_code VARCHAR(50),
    carrier_raw_data JSONB,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shipment items (for multi-item shipments)
CREATE TABLE IF NOT EXISTS shipment_items (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT REFERENCES shipments(id) ON DELETE CASCADE,
    
    item_name VARCHAR(255) NOT NULL,
    item_description TEXT,
    quantity INTEGER NOT NULL DEFAULT 1,
    weight_kg DECIMAL(10, 3),
    unit_value DECIMAL(10, 2),
    total_value DECIMAL(10, 2),
    sku VARCHAR(100),
    hs_code VARCHAR(20), -- Harmonized System code for customs
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Shipment documents
CREATE TABLE IF NOT EXISTS shipment_documents (
    id BIGSERIAL PRIMARY KEY,
    shipment_id BIGINT REFERENCES shipments(id) ON DELETE CASCADE,
    
    document_type VARCHAR(50) NOT NULL, -- INVOICE, BOL, CUSTOMS, CERTIFICATE
    document_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),
    
    uploaded_by BIGINT REFERENCES users(id),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Performance indexes for shipment tracking
CREATE INDEX IF NOT EXISTS idx_shipments_tracking_number ON shipments(tracking_number);
CREATE INDEX IF NOT EXISTS idx_shipments_status ON shipments(status);
CREATE INDEX IF NOT EXISTS idx_shipments_carrier ON shipments(carrier_name);
CREATE INDEX IF NOT EXISTS idx_shipments_dates ON shipments(ship_date, estimated_delivery_date);
CREATE INDEX IF NOT EXISTS idx_shipments_supplier ON shipments(supplier_id);
CREATE INDEX IF NOT EXISTS idx_shipments_search_vector ON shipments USING GIN(search_vector);
CREATE INDEX IF NOT EXISTS idx_tracking_events_shipment_timestamp ON shipment_tracking_events(shipment_id, event_timestamp);
CREATE INDEX IF NOT EXISTS idx_shipment_items_shipment_id ON shipment_items(shipment_id);
CREATE INDEX IF NOT EXISTS idx_shipment_documents_shipment_id ON shipment_documents(shipment_id);
CREATE INDEX IF NOT EXISTS idx_carriers_name ON carriers(name);
CREATE INDEX IF NOT EXISTS idx_carriers_code ON carriers(code);

-- Create trigger for shipments updated_at
DROP TRIGGER IF EXISTS update_shipments_updated_at ON shipments;
CREATE TRIGGER update_shipments_updated_at
    BEFORE UPDATE ON shipments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Create trigger for carriers updated_at
DROP TRIGGER IF EXISTS update_carriers_updated_at ON carriers;
CREATE TRIGGER update_carriers_updated_at
    BEFORE UPDATE ON carriers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Function to update search vector for shipments
CREATE OR REPLACE FUNCTION update_shipment_search_vector()
RETURNS TRIGGER AS $$
BEGIN
    NEW.search_vector := to_tsvector('english', 
        COALESCE(NEW.tracking_number, '') || ' ' ||
        COALESCE(NEW.reference_number, '') || ' ' ||
        COALESCE(NEW.carrier_name, '') || ' ' ||
        COALESCE(NEW.origin_city, '') || ' ' ||
        COALESCE(NEW.origin_country, '') || ' ' ||
        COALESCE(NEW.destination_city, '') || ' ' ||
        COALESCE(NEW.destination_country, '') || ' ' ||
        COALESCE(NEW.status, '')
    );
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for shipment search vector update
DROP TRIGGER IF EXISTS update_shipment_search_vector_trigger ON shipments;
CREATE TRIGGER update_shipment_search_vector_trigger
    BEFORE INSERT OR UPDATE ON shipments
    FOR EACH ROW
    EXECUTE FUNCTION update_shipment_search_vector();

-- Insert some default carriers
INSERT INTO carriers (name, code, services_offered, countries_supported, tracking_url_template, is_active) VALUES 
    ('DHL Express', 'DHL', ARRAY['EXPRESS', 'INTERNATIONAL'], ARRAY['US', 'DE', 'UK', 'CN', 'IN'], 'https://www.dhl.com/en/express/tracking.html?AWB={TRACKING_NUMBER}', true),
    ('FedEx', 'FEDEX', ARRAY['EXPRESS', 'GROUND', 'FREIGHT'], ARRAY['US', 'CA', 'MX', 'UK', 'DE'], 'https://www.fedex.com/fedextrack/?trknbr={TRACKING_NUMBER}', true),
    ('UPS', 'UPS', ARRAY['GROUND', 'AIR', 'FREIGHT'], ARRAY['US', 'CA', 'MX', 'UK', 'DE'], 'https://www.ups.com/track?loc=en_US&tracknum={TRACKING_NUMBER}', true),
    ('Maersk Line', 'MSK', ARRAY['OCEAN', 'FREIGHT'], ARRAY['US', 'CN', 'NL', 'DK', 'SG'], 'https://www.maersk.com/tracking?shipmentId={TRACKING_NUMBER}', true),
    ('MSC', 'MSC', ARRAY['OCEAN', 'FREIGHT'], ARRAY['US', 'IT', 'CH', 'CN', 'SG'], 'https://www.msc.com/track-a-shipment?trackingNumber={TRACKING_NUMBER}', true)
ON CONFLICT (code) DO NOTHING;