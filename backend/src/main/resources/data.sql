-- Insert sample users
INSERT INTO users (firebase_uid, email, name, role, is_active, created_at, updated_at) VALUES
('admin-uid-123', 'admin@smartsupplychain.com', 'Admin User', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (firebase_uid, email, name, role, is_active, created_at, updated_at) VALUES
('manager-uid-456', 'manager@smartsupplychain.com', 'Supply Manager', 'SUPPLY_MANAGER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (firebase_uid, email, name, role, is_active, created_at, updated_at) VALUES
('viewer-uid-789', 'viewer@smartsupplychain.com', 'Viewer User', 'VIEWER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample suppliers
INSERT INTO suppliers (name, location, contact_info, risk_score, is_active, last_updated) VALUES
('Global Electronics Ltd', 'Shenzhen, China', 'contact@globalelectronics.cn', 6.5, true, CURRENT_TIMESTAMP);

INSERT INTO suppliers (name, location, contact_info, risk_score, is_active, last_updated) VALUES
('European Manufacturing Co', 'Munich, Germany', 'info@europmfg.de', 3.2, true, CURRENT_TIMESTAMP);

INSERT INTO suppliers (name, location, contact_info, risk_score, is_active, last_updated) VALUES
('Pacific Components Inc', 'Tokyo, Japan', 'sales@pacificcomp.jp', 2.8, true, CURRENT_TIMESTAMP);

INSERT INTO suppliers (name, location, contact_info, risk_score, is_active, last_updated) VALUES
('American Steel Works', 'Pittsburgh, USA', 'orders@amsteel.com', 4.1, true, CURRENT_TIMESTAMP);

INSERT INTO suppliers (name, location, contact_info, risk_score, is_active, last_updated) VALUES
('Southeast Textiles', 'Bangkok, Thailand', 'export@setextiles.th', 7.2, true, CURRENT_TIMESTAMP);

INSERT INTO suppliers (name, location, contact_info, risk_score, is_active, last_updated) VALUES
('Nordic Lumber', 'Stockholm, Sweden', 'trade@nordiclumber.se', 2.5, true, CURRENT_TIMESTAMP);

-- Insert sample shipments
INSERT INTO shipments (supplier_id, status, origin, destination, estimated_arrival, tracking_number, description, last_update) VALUES
(1, 'IN_TRANSIT', 'Shenzhen, China', 'Los Angeles, USA', DATEADD('DAY', 5, CURRENT_TIMESTAMP), 'SCR-20250110-001234', 'Electronic components - smartphones', CURRENT_TIMESTAMP);

INSERT INTO shipments (supplier_id, status, origin, destination, estimated_arrival, tracking_number, description, last_update) VALUES
(2, 'PENDING', 'Munich, Germany', 'Detroit, USA', DATEADD('DAY', 12, CURRENT_TIMESTAMP), 'SCR-20250110-001235', 'Automotive parts - engines', CURRENT_TIMESTAMP);

INSERT INTO shipments (supplier_id, status, origin, destination, estimated_arrival, tracking_number, description, last_update) VALUES
(3, 'DELIVERED', 'Tokyo, Japan', 'Seattle, USA', DATEADD('DAY', -2, CURRENT_TIMESTAMP), 'SCR-20250108-001236', 'Precision machinery', CURRENT_TIMESTAMP);

INSERT INTO shipments (supplier_id, status, origin, destination, estimated_arrival, tracking_number, description, last_update) VALUES
(4, 'DELAYED', 'Pittsburgh, USA', 'Toronto, Canada', DATEADD('DAY', -1, CURRENT_TIMESTAMP), 'SCR-20250109-001237', 'Steel beams and rods', CURRENT_TIMESTAMP);

INSERT INTO shipments (supplier_id, status, origin, destination, estimated_arrival, tracking_number, description, last_update) VALUES
(5, 'IN_TRANSIT', 'Bangkok, Thailand', 'Hamburg, Germany', DATEADD('DAY', 8, CURRENT_TIMESTAMP), 'SCR-20250110-001238', 'Textile products', CURRENT_TIMESTAMP);

INSERT INTO shipments (supplier_id, status, origin, destination, estimated_arrival, tracking_number, description, last_update) VALUES
(6, 'PENDING', 'Stockholm, Sweden', 'Oslo, Norway', DATEADD('DAY', 3, CURRENT_TIMESTAMP), 'SCR-20250110-001239', 'Lumber and wood products', CURRENT_TIMESTAMP);

-- Insert sample risk predictions
INSERT INTO risk_predictions (shipment_id, risk_level, probability, description, prediction_factors, created_at) VALUES
(1, 'HIGH', 0.7250, 'High risk due to supplier location and current weather conditions', 'Supplier Risk Score: 6.5; Origin region risk; Weather delays;', CURRENT_TIMESTAMP);

INSERT INTO risk_predictions (shipment_id, risk_level, probability, description, prediction_factors, created_at) VALUES
(2, 'MEDIUM', 0.4500, 'Medium risk with good supplier reliability', 'Supplier Risk Score: 3.2; Long transit time;', CURRENT_TIMESTAMP);

INSERT INTO risk_predictions (shipment_id, risk_level, probability, description, prediction_factors, created_at) VALUES
(3, 'LOW', 0.1500, 'Low risk - delivered successfully', 'Supplier Risk Score: 2.8; Reliable supplier; Short distance;', CURRENT_TIMESTAMP);

INSERT INTO risk_predictions (shipment_id, risk_level, probability, description, prediction_factors, created_at) VALUES
(4, 'CRITICAL', 0.9200, 'Critical risk - shipment already delayed', 'Supplier Risk Score: 4.1; Already delayed; Weather issues;', CURRENT_TIMESTAMP);

INSERT INTO risk_predictions (shipment_id, risk_level, probability, description, prediction_factors, created_at) VALUES
(5, 'HIGH', 0.6800, 'High risk due to long transit and supplier location', 'Supplier Risk Score: 7.2; Long distance; Monsoon season;', CURRENT_TIMESTAMP);

INSERT INTO risk_predictions (shipment_id, risk_level, probability, description, prediction_factors, created_at) VALUES
(6, 'LOW', 0.2100, 'Low risk with reliable Nordic supplier', 'Supplier Risk Score: 2.5; Short distance; Reliable supplier;', CURRENT_TIMESTAMP);

-- Insert sample alerts
INSERT INTO alerts (user_id, message, severity, is_acknowledged, reference_id, reference_type, created_at) VALUES
(2, 'Shipment SCR-20250109-001237 is experiencing delays due to weather conditions', 'ERROR', false, '4', 'SHIPMENT', CURRENT_TIMESTAMP);

INSERT INTO alerts (user_id, message, severity, is_acknowledged, reference_id, reference_type, created_at) VALUES
(1, 'High risk supplier Global Electronics Ltd requires attention', 'WARNING', false, '1', 'SUPPLIER', CURRENT_TIMESTAMP);

INSERT INTO alerts (user_id, message, severity, is_acknowledged, reference_id, reference_type, created_at) VALUES
(2, 'Critical risk prediction generated for shipment SCR-20250109-001237', 'CRITICAL', false, '4', 'SHIPMENT', CURRENT_TIMESTAMP);

INSERT INTO alerts (user_id, message, severity, is_acknowledged, reference_id, reference_type, created_at) VALUES
(3, 'New shipment SCR-20250110-001234 is now in transit', 'INFO', true, '1', 'SHIPMENT', CURRENT_TIMESTAMP);

INSERT INTO alerts (user_id, message, severity, is_acknowledged, reference_id, reference_type, created_at) VALUES
(1, 'Monthly risk assessment completed', 'INFO', true, NULL, NULL, CURRENT_TIMESTAMP);