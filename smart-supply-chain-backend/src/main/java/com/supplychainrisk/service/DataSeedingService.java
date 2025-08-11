package com.supplychainrisk.service;

import com.supplychainrisk.entity.*;
import com.supplychainrisk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DataSeedingService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeedingService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private SupplierCategoryRepository supplierCategoryRepository;
    
    @Autowired
    private SupplierPerformanceHistoryRepository supplierPerformanceHistoryRepository;
    
    @Autowired
    private CarrierRepository carrierRepository;
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private ShipmentTrackingEventRepository shipmentTrackingEventRepository;
    
    @Autowired
    private ShipmentItemRepository shipmentItemRepository;
    
    @Autowired
    private UserService userService;
    
    private Random random = new Random();
    
    // Data for realistic generation
    private static final String[] FIRST_NAMES = {
        "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda",
        "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
        "Thomas", "Sarah", "Christopher", "Karen", "Charles", "Nancy", "Daniel", "Lisa",
        "Matthew", "Betty", "Anthony", "Helen", "Mark", "Sandra", "Donald", "Donna"
    };
    
    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
        "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White",
        "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker", "Young"
    };
    
    private static final String[] COMPANY_SUFFIXES = {
        "Inc", "Corp", "LLC", "Ltd", "Co", "Industries", "Manufacturing", "Solutions",
        "Systems", "Technologies", "Enterprises", "Group", "Holdings", "International"
    };
    
    private static final String[] INDUSTRIES = {
        "Electronics", "Automotive", "Textiles", "Chemicals", "Food Processing",
        "Pharmaceuticals", "Machinery", "Metals", "Plastics", "Aerospace",
        "Energy", "Construction", "Telecommunications", "Software", "Medical Devices"
    };
    
    // Countries with their major cities and coordinates
    private static final Map<String, CountryData> COUNTRIES = Map.ofEntries(
        Map.entry("United States", new CountryData("US", Arrays.asList(
            new CityData("New York", "NY", 40.7128, -74.0060),
            new CityData("Los Angeles", "CA", 34.0522, -118.2437),
            new CityData("Chicago", "IL", 41.8781, -87.6298),
            new CityData("Houston", "TX", 29.7604, -95.3698),
            new CityData("Phoenix", "AZ", 33.4484, -112.0740),
            new CityData("Philadelphia", "PA", 39.9526, -75.1652),
            new CityData("San Antonio", "TX", 29.4241, -98.4936),
            new CityData("San Diego", "CA", 32.7157, -117.1611),
            new CityData("Dallas", "TX", 32.7767, -96.7970),
            new CityData("San Jose", "CA", 37.3382, -121.8863)
        ))),
        Map.entry("China", new CountryData("CN", Arrays.asList(
            new CityData("Shanghai", "SH", 31.2304, 121.4737),
            new CityData("Beijing", "BJ", 39.9042, 116.4074),
            new CityData("Shenzhen", "GD", 22.3193, 114.1694),
            new CityData("Guangzhou", "GD", 23.1291, 113.2644),
            new CityData("Tianjin", "TJ", 39.3434, 117.3616),
            new CityData("Wuhan", "HB", 30.5928, 114.3055),
            new CityData("Dongguan", "GD", 23.0489, 113.7447),
            new CityData("Chengdu", "SC", 30.5728, 104.0668),
            new CityData("Nanjing", "JS", 32.0603, 118.7969),
            new CityData("Xi'an", "SN", 34.2658, 108.9541)
        ))),
        Map.entry("Germany", new CountryData("DE", Arrays.asList(
            new CityData("Berlin", "BE", 52.5200, 13.4050),
            new CityData("Hamburg", "HH", 53.5511, 9.9937),
            new CityData("Munich", "BY", 48.1351, 11.5820),
            new CityData("Cologne", "NW", 50.9375, 6.9603),
            new CityData("Frankfurt", "HE", 50.1109, 8.6821),
            new CityData("Stuttgart", "BW", 48.7758, 9.1829),
            new CityData("Düsseldorf", "NW", 51.2277, 6.7735),
            new CityData("Dortmund", "NW", 51.5136, 7.4653),
            new CityData("Essen", "NW", 51.4556, 7.0116),
            new CityData("Leipzig", "SN", 51.3397, 12.3731)
        ))),
        Map.entry("Japan", new CountryData("JP", Arrays.asList(
            new CityData("Tokyo", "TK", 35.6762, 139.6503),
            new CityData("Yokohama", "KN", 35.4437, 139.6380),
            new CityData("Osaka", "OS", 34.6937, 135.5023),
            new CityData("Nagoya", "AI", 35.1815, 136.9066),
            new CityData("Sapporo", "HD", 43.0642, 141.3469),
            new CityData("Fukuoka", "FK", 33.5904, 130.4017),
            new CityData("Kobe", "HG", 34.6901, 135.1956),
            new CityData("Kawasaki", "KN", 35.5308, 139.7029),
            new CityData("Kyoto", "KT", 35.0116, 135.7681),
            new CityData("Saitama", "ST", 35.8617, 139.6455)
        ))),
        Map.entry("United Kingdom", new CountryData("GB", Arrays.asList(
            new CityData("London", "ENG", 51.5074, -0.1278),
            new CityData("Birmingham", "ENG", 52.4862, -1.8904),
            new CityData("Manchester", "ENG", 53.4808, -2.2426),
            new CityData("Glasgow", "SCT", 55.8642, -4.2518),
            new CityData("Liverpool", "ENG", 53.4084, -2.9916),
            new CityData("Leeds", "ENG", 53.8008, -1.5491),
            new CityData("Sheffield", "ENG", 53.3811, -1.4701),
            new CityData("Edinburgh", "SCT", 55.9533, -3.1883),
            new CityData("Bristol", "ENG", 51.4545, -2.5879),
            new CityData("Cardiff", "WLS", 51.4816, -3.1791)
        ))),
        Map.entry("India", new CountryData("IN", Arrays.asList(
            new CityData("Mumbai", "MH", 19.0760, 72.8777),
            new CityData("Delhi", "DL", 28.7041, 77.1025),
            new CityData("Bangalore", "KA", 12.9716, 77.5946),
            new CityData("Hyderabad", "TG", 17.3850, 78.4867),
            new CityData("Chennai", "TN", 13.0827, 80.2707),
            new CityData("Kolkata", "WB", 22.5726, 88.3639),
            new CityData("Pune", "MH", 18.5204, 73.8567),
            new CityData("Ahmedabad", "GJ", 23.0225, 72.5714),
            new CityData("Jaipur", "RJ", 26.9124, 75.7873),
            new CityData("Surat", "GJ", 21.1702, 72.8311)
        )))
    );
    
    // Helper classes for data structure
    private static class CountryData {
        final String code;
        final List<CityData> cities;
        CountryData(String code, List<CityData> cities) {
            this.code = code;
            this.cities = cities;
        }
    }
    
    private static class CityData {
        final String name;
        final String state;
        final double latitude;
        final double longitude;
        CityData(String name, String state, double latitude, double longitude) {
            this.name = name;
            this.state = state;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
    
    @Transactional
    public void seedAllData() {
        logger.info("Starting comprehensive data seeding...");
        
        try {
            // Step 1: Create sample users
            createSampleUsers();
            
            // Step 2: Ensure supplier categories exist
            ensureSupplierCategories();
            
            // Step 3: Create realistic suppliers
            createRealisticSuppliers();
            
            // Step 4: Create performance history for suppliers
            createSupplierPerformanceHistory();
            
            // Step 5: Ensure carriers exist
            ensureCarriers();
            
            // Step 6: Create comprehensive shipments
            createRealisticShipments();
            
            // Step 7: Create tracking events for shipments
            createTrackingEvents();
            
            logger.info("Data seeding completed successfully!");
            
        } catch (Exception e) {
            logger.error("Error during data seeding: {}", e.getMessage(), e);
            throw new RuntimeException("Data seeding failed", e);
        }
    }
    
    private void createSampleUsers() {
        logger.info("Creating sample users...");
        
        // Admin user
        if (!userService.existsByUsername("admin")) {
            userService.createUser("admin", "admin@company.com", "password123", 
                "System", "Administrator", User.Role.ADMIN);
        }
        
        // Supply managers
        if (!userService.existsByUsername("sarah.manager")) {
            userService.createUser("sarah.manager", "sarah@company.com", "password123", 
                "Sarah", "Johnson", User.Role.SUPPLY_MANAGER);
        }
        
        if (!userService.existsByUsername("mike.analyst")) {
            userService.createUser("mike.analyst", "mike@company.com", "password123", 
                "Mike", "Chen", User.Role.SUPPLY_MANAGER);
        }
        
        // Viewers
        if (!userService.existsByUsername("lisa.viewer")) {
            userService.createUser("lisa.viewer", "lisa@company.com", "password123", 
                "Lisa", "Rodriguez", User.Role.VIEWER);
        }
        
        // Auditor
        if (!userService.existsByUsername("john.auditor")) {
            userService.createUser("john.auditor", "john@company.com", "password123", 
                "John", "Smith", User.Role.AUDITOR);
        }
        
        logger.info("Sample users created successfully");
    }
    
    private void ensureSupplierCategories() {
        String[] categories = {
            "Manufacturing", "Technology", "Logistics", "Raw Materials", "Services",
            "Electronics", "Automotive", "Textiles", "Chemicals", "Food Processing"
        };
        
        String[] colors = {
            "#0078D4", "#1DB954", "#FF6B35", "#8B5CF6", "#F59E0B",
            "#EF4444", "#10B981", "#3B82F6", "#8B5A3C", "#EC4899"
        };
        
        for (int i = 0; i < categories.length; i++) {
            if (!supplierCategoryRepository.existsByName(categories[i])) {
                SupplierCategory category = new SupplierCategory();
                category.setName(categories[i]);
                category.setDescription(categories[i] + " category suppliers");
                category.setColor(colors[i % colors.length]);
                supplierCategoryRepository.save(category);
            }
        }
    }
    
    // Continue with more methods...
    
    private void createRealisticSuppliers() {
        logger.info("Creating realistic suppliers...");
        
        if (supplierRepository.count() >= 150) {
            logger.info("Suppliers already exist, skipping creation");
            return;
        }
        
        List<SupplierCategory> categories = supplierCategoryRepository.findAll();
        List<User> users = userRepository.findAll();
        User defaultUser = users.stream().findFirst().orElse(null);
        
        int suppliersToCreate = 150;
        
        for (int i = 0; i < suppliersToCreate; i++) {
            Supplier supplier = createRandomSupplier(i + 1, categories, defaultUser);
            supplierRepository.save(supplier);
            
            if (i % 50 == 0) {
                logger.info("Created {} suppliers...", i);
            }
        }
        
        logger.info("Created {} realistic suppliers", suppliersToCreate);
    }
    
    private Supplier createRandomSupplier(int index, List<SupplierCategory> categories, User createdBy) {
        Supplier supplier = new Supplier();
        
        // Basic information
        String companyName = generateCompanyName();
        supplier.setSupplierCode("SUP-" + String.format("%04d", index));
        supplier.setName(companyName);
        supplier.setLegalName(companyName + " " + getRandomElement(COMPANY_SUFFIXES));
        
        // Contact information
        supplier.setPrimaryContactName(getRandomElement(FIRST_NAMES) + " " + getRandomElement(LAST_NAMES));
        supplier.setPrimaryContactEmail(generateEmail(supplier.getPrimaryContactName(), companyName));
        supplier.setPrimaryContactPhone(generatePhoneNumber());
        supplier.setWebsite("https://www." + companyName.toLowerCase().replace(" ", "") + ".com");
        
        // Location
        String country = getRandomCountry();
        CountryData countryData = COUNTRIES.get(country);
        CityData city = getRandomElement(countryData.cities);
        
        supplier.setCountry(country);
        supplier.setCity(city.name);
        supplier.setStateProvince(city.state);
        supplier.setPostalCode(generatePostalCode(countryData.code));
        supplier.setStreetAddress(generateStreetAddress());
        supplier.setLatitude(BigDecimal.valueOf(city.latitude + (random.nextGaussian() * 0.1)));
        supplier.setLongitude(BigDecimal.valueOf(city.longitude + (random.nextGaussian() * 0.1)));
        
        // Business information
        supplier.setIndustry(getRandomElement(INDUSTRIES));
        supplier.setBusinessType(random.nextBoolean() ? "Manufacturing" : "Service Provider");
        supplier.setAnnualRevenue(BigDecimal.valueOf(random.nextDouble() * 1000000000)); // Up to 1B
        supplier.setEmployeeCount(random.nextInt(10000) + 50);
        supplier.setYearsInBusiness(random.nextInt(50) + 1);
        
        // Performance and risk metrics
        generatePerformanceMetrics(supplier);
        
        // Certifications
        supplier.setIsoCertifications(generateIsoCertifications());
        supplier.setComplianceCertifications(generateComplianceCertifications());
        supplier.setLastAuditDate(LocalDate.now().minusDays(random.nextInt(365)));
        supplier.setNextAuditDueDate(supplier.getLastAuditDate().plusDays(365));
        
        // Financial information
        supplier.setCreditRating(generateCreditRating());
        supplier.setPaymentTerms(generatePaymentTerms());
        supplier.setCurrency(getCurrencyForCountry(country));
        
        // Status
        supplier.setStatus(random.nextDouble() < 0.9 ? Supplier.SupplierStatus.ACTIVE : Supplier.SupplierStatus.INACTIVE);
        supplier.setTier(generateSupplierTier());
        supplier.setPreferredSupplier(random.nextDouble() < 0.3);
        supplier.setStrategicSupplier(random.nextDouble() < 0.15);
        
        // Metadata
        supplier.setCreatedBy(createdBy);
        
        // Categories
        Set<SupplierCategory> supplierCategories = new HashSet<>();
        int numCategories = random.nextInt(3) + 1; // 1-3 categories
        for (int i = 0; i < numCategories; i++) {
            supplierCategories.add(getRandomElement(categories));
        }
        supplier.setCategories(supplierCategories);
        
        return supplier;
    }
    
    private void generatePerformanceMetrics(Supplier supplier) {
        // Risk scores (70% low-risk, 20% medium-risk, 10% high-risk)
        double riskDistribution = random.nextDouble();
        int baseRiskScore;
        if (riskDistribution < 0.7) {
            baseRiskScore = random.nextInt(30); // Low risk: 0-29
        } else if (riskDistribution < 0.9) {
            baseRiskScore = random.nextInt(40) + 30; // Medium risk: 30-69
        } else {
            baseRiskScore = random.nextInt(31) + 70; // High risk: 70-100
        }
        
        supplier.setOverallRiskScore(baseRiskScore);
        supplier.setFinancialRiskScore(baseRiskScore + random.nextInt(21) - 10); // ±10 variance
        supplier.setOperationalRiskScore(baseRiskScore + random.nextInt(21) - 10);
        supplier.setComplianceRiskScore(baseRiskScore + random.nextInt(21) - 10);
        supplier.setGeographicRiskScore(baseRiskScore + random.nextInt(21) - 10);
        
        // Clamp values to 0-100
        supplier.setFinancialRiskScore(Math.max(0, Math.min(100, supplier.getFinancialRiskScore())));
        supplier.setOperationalRiskScore(Math.max(0, Math.min(100, supplier.getOperationalRiskScore())));
        supplier.setComplianceRiskScore(Math.max(0, Math.min(100, supplier.getComplianceRiskScore())));
        supplier.setGeographicRiskScore(Math.max(0, Math.min(100, supplier.getGeographicRiskScore())));
        
        // Performance KPIs (inversely related to risk)
        double performanceBase = 100 - baseRiskScore;
        supplier.setOnTimeDeliveryRate(BigDecimal.valueOf(Math.max(60, performanceBase + random.nextGaussian() * 10)));
        supplier.setQualityRating(BigDecimal.valueOf(Math.max(5.0, Math.min(10.0, (performanceBase / 10) + random.nextGaussian() * 1.5))));
        supplier.setCostCompetitivenessScore((int) Math.max(40, Math.min(100, performanceBase + random.nextGaussian() * 15)));
        supplier.setResponsivenessScore((int) Math.max(30, Math.min(100, performanceBase + random.nextGaussian() * 20)));
    }
    
    private void createSupplierPerformanceHistory() {
        logger.info("Creating supplier performance history...");
        
        List<Supplier> suppliers = supplierRepository.findAll();
        LocalDate startDate = LocalDate.now().minusMonths(18);
        
        for (Supplier supplier : suppliers) {
            for (int month = 0; month < 18; month++) {
                LocalDate performanceDate = startDate.plusMonths(month);
                
                SupplierPerformanceHistory history = new SupplierPerformanceHistory();
                history.setSupplier(supplier);
                history.setPerformanceDate(performanceDate);
                
                // Add some variance to the base metrics
                double variance = random.nextGaussian() * 5; // ±5 point variance
                history.setOnTimeDeliveryRate(supplier.getOnTimeDeliveryRate().add(BigDecimal.valueOf(variance)));
                history.setQualityScore(supplier.getQualityRating().add(BigDecimal.valueOf(variance / 10)));
                history.setCostScore(supplier.getCostCompetitivenessScore() + (int) variance);
                history.setOverallScore(calculateOverallScore(history));
                
                if (month % 6 == 0) {
                    history.setNotes("Quarterly performance review completed");
                }
                
                supplierPerformanceHistoryRepository.save(history);
            }
        }
        
        logger.info("Created performance history for {} suppliers", suppliers.size());
    }
    
    private BigDecimal calculateOverallScore(SupplierPerformanceHistory history) {
        double score = (history.getOnTimeDeliveryRate().doubleValue() * 0.4) +
                      (history.getQualityScore().doubleValue() * 10 * 0.4) +
                      (history.getCostScore() * 0.2);
        return BigDecimal.valueOf(Math.max(0, Math.min(100, score)));
    }
    
    private void ensureCarriers() {
        // This will be handled by the existing database schema migration
        logger.info("Carriers should be created by database migration");
    }
    
    private void createRealisticShipments() {
        logger.info("Creating realistic shipments...");
        
        if (shipmentRepository.count() >= 2000) {
            logger.info("Shipments already exist, skipping creation");
            return;
        }
        
        List<Supplier> suppliers = supplierRepository.findAll();
        List<Carrier> carriers = carrierRepository.findAll();
        List<User> users = userRepository.findAll();
        User defaultUser = users.stream().findFirst().orElse(null);
        
        int shipmentsToCreate = 2000;
        LocalDateTime startDate = LocalDateTime.now().minusMonths(18);
        
        for (int i = 0; i < shipmentsToCreate; i++) {
            Shipment shipment = createRandomShipment(i + 1, suppliers, carriers, defaultUser, startDate);
            shipmentRepository.save(shipment);
            
            if (i % 200 == 0) {
                logger.info("Created {} shipments...", i);
            }
        }
        
        logger.info("Created {} realistic shipments", shipmentsToCreate);
    }
    
    private Shipment createRandomShipment(int index, List<Supplier> suppliers, List<Carrier> carriers, 
                                        User createdBy, LocalDateTime startDate) {
        Shipment shipment = new Shipment();
        
        // Basic information
        shipment.setTrackingNumber(generateTrackingNumber(index));
        shipment.setReferenceNumber("REF-" + String.format("%06d", index));
        shipment.setSupplier(getRandomElement(suppliers));
        
        // Carrier and service
        Carrier carrier = getRandomElement(carriers);
        shipment.setCarrierName(carrier.getName());
        shipment.setCarrierServiceCode(generateServiceCode(carrier));
        shipment.setCarrierTrackingUrl(generateTrackingUrl(carrier, shipment.getTrackingNumber()));
        
        // Shipment details
        shipment.setShipmentType(Shipment.ShipmentType.valueOf(generateShipmentType()));
        shipment.setServiceLevel(generateServiceLevel());
        shipment.setWeightKg(BigDecimal.valueOf(random.nextDouble() * 1000 + 1)); // 1-1000 kg
        shipment.setDimensionsLengthCm(BigDecimal.valueOf(random.nextDouble() * 200 + 10)); // 10-200 cm
        shipment.setDimensionsWidthCm(BigDecimal.valueOf(random.nextDouble() * 150 + 10));
        shipment.setDimensionsHeightCm(BigDecimal.valueOf(random.nextDouble() * 100 + 5));
        shipment.setDeclaredValue(BigDecimal.valueOf(random.nextDouble() * 100000 + 100)); // $100-$100k
        
        // Origin and destination
        setRandomOriginDestination(shipment);
        
        // Timing and status
        LocalDateTime shipDate = startDate.plusDays(random.nextInt(545)); // Random date in 18 months
        shipment.setShipDate(shipDate);
        
        // Generate status based on date
        generateShipmentStatus(shipment, shipDate);
        
        // Cost information
        generateShippingCost(shipment);
        
        // Risk assessment
        shipment.setRiskScore(random.nextInt(101));
        shipment.setDelayRiskProbability(BigDecimal.valueOf(random.nextDouble() * 30));
        
        // Environmental impact
        shipment.setCarbonFootprintKg(calculateCarbonFootprint(shipment));
        
        // Metadata
        shipment.setCreatedBy(createdBy);
        
        return shipment;
    }
    
    // Helper methods for data generation
    private String generateCompanyName() {
        String[] prefixes = {"Global", "Advanced", "Superior", "Premier", "Elite", "Dynamic", "Innovative", "Strategic"};
        String[] cores = {"Tech", "Manufacturing", "Solutions", "Systems", "Industries", "Components", "Materials", "Logistics"};
        return getRandomElement(prefixes) + " " + getRandomElement(cores);
    }
    
    private String getRandomElement(String[] array) {
        return array[random.nextInt(array.length)];
    }
    
    private <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
    
    private String getRandomCountry() {
        return getRandomElement(COUNTRIES.keySet().toArray(new String[0]));
    }
    
    private String generateEmail(String name, String company) {
        String cleanName = name.toLowerCase().replace(" ", ".");
        String cleanCompany = company.toLowerCase().replace(" ", "");
        return cleanName + "@" + cleanCompany + ".com";
    }
    
    private String generatePhoneNumber() {
        return String.format("+1-%03d-%03d-%04d", 
            random.nextInt(900) + 100, 
            random.nextInt(900) + 100, 
            random.nextInt(9000) + 1000);
    }
    
    private String generatePostalCode(String countryCode) {
        switch (countryCode) {
            case "US": return String.format("%05d", random.nextInt(99999));
            case "CN": return String.format("%06d", random.nextInt(999999));
            case "DE": return String.format("%05d", random.nextInt(99999));
            case "JP": return String.format("%03d-%04d", random.nextInt(999), random.nextInt(9999));
            case "GB": return String.format("%c%c%d %d%c%c", 
                (char)('A' + random.nextInt(26)), (char)('A' + random.nextInt(26)),
                random.nextInt(10), random.nextInt(10),
                (char)('A' + random.nextInt(26)), (char)('A' + random.nextInt(26)));
            case "IN": return String.format("%06d", random.nextInt(999999));
            default: return String.format("%05d", random.nextInt(99999));
        }
    }
    
    private String generateStreetAddress() {
        String[] streetTypes = {"St", "Ave", "Blvd", "Dr", "Ln", "Way", "Rd"};
        int number = random.nextInt(9999) + 1;
        String street = getRandomElement(LAST_NAMES);
        String type = getRandomElement(streetTypes);
        return number + " " + street + " " + type;
    }
    
    private List<String> generateIsoCertifications() {
        String[] certifications = {"ISO 9001", "ISO 14001", "ISO 45001", "ISO 27001", "ISO 13485"};
        List<String> result = new ArrayList<>();
        int count = random.nextInt(3) + 1; // 1-3 certifications
        Set<String> selected = new HashSet<>();
        
        while (selected.size() < count) {
            selected.add(getRandomElement(certifications));
        }
        
        result.addAll(selected);
        return result;
    }
    
    private List<String> generateComplianceCertifications() {
        String[] certifications = {"GDPR", "SOX", "HIPAA", "FDA", "CE", "FCC", "RoHS"};
        List<String> result = new ArrayList<>();
        int count = random.nextInt(2) + 1; // 1-2 certifications
        Set<String> selected = new HashSet<>();
        
        while (selected.size() < count) {
            selected.add(getRandomElement(certifications));
        }
        
        result.addAll(selected);
        return result;
    }
    
    private String generateCreditRating() {
        String[] ratings = {"AAA", "AA+", "AA", "AA-", "A+", "A", "A-", "BBB+", "BBB", "BBB-", "BB+", "BB"};
        return getRandomElement(ratings);
    }
    
    private String generatePaymentTerms() {
        String[] terms = {"Net 30", "Net 45", "Net 60", "2/10 Net 30", "COD", "Prepaid"};
        return getRandomElement(terms);
    }
    
    private String getCurrencyForCountry(String country) {
        switch (country) {
            case "United States": return "USD";
            case "China": return "CNY";
            case "Germany": return "EUR";
            case "Japan": return "JPY";
            case "United Kingdom": return "GBP";
            case "India": return "INR";
            default: return "USD";
        }
    }
    
    private Supplier.SupplierTier generateSupplierTier() {
        double rand = random.nextDouble();
        if (rand < 0.4) return Supplier.SupplierTier.PRIMARY;
        if (rand < 0.8) return Supplier.SupplierTier.SECONDARY;
        return Supplier.SupplierTier.BACKUP;
    }
    
    // Additional methods for shipment generation will be added in next part...
    
    private String generateTrackingNumber(int index) {
        String[] prefixes = {"1Z", "FX", "DH", "UP", "MS"};
        String prefix = getRandomElement(prefixes);
        return prefix + String.format("%012d", index);
    }
    
    private String generateServiceCode(Carrier carrier) {
        String[] codes = {"EXPRESS", "STANDARD", "ECONOMY", "PRIORITY", "OVERNIGHT"};
        return getRandomElement(codes);
    }
    
    private String generateTrackingUrl(Carrier carrier, String trackingNumber) {
        if (carrier.getTrackingUrlTemplate() != null) {
            return carrier.getTrackingUrlTemplate().replace("{TRACKING_NUMBER}", trackingNumber);
        }
        return "https://tracking.example.com/" + trackingNumber;
    }
    
    private String generateShipmentType() {
        String[] types = {"STANDARD", "EXPRESS", "FREIGHT"};
        double[] probabilities = {0.6, 0.3, 0.1}; // 60% standard, 30% express, 10% freight
        
        double rand = random.nextDouble();
        double cumulative = 0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (rand <= cumulative) {
                return types[i];
            }
        }
        return types[0];
    }
    
    private String generateServiceLevel() {
        String[] services = {"GROUND", "AIR", "OCEAN", "NEXT_DAY", "2_DAY"};
        double[] probabilities = {0.4, 0.25, 0.15, 0.1, 0.1};
        
        double rand = random.nextDouble();
        double cumulative = 0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (rand <= cumulative) {
                return services[i];
            }
        }
        return services[0];
    }
    
    private void setRandomOriginDestination(Shipment shipment) {
        // Major trade routes: Asia → North America (35%), Europe → North America (25%), 
        // Asia → Europe (20%), Intra-regional (20%)
        
        double routeType = random.nextDouble();
        String originCountry, destCountry;
        
        if (routeType < 0.35) {
            // Asia → North America
            originCountry = random.nextBoolean() ? "China" : "Japan";
            destCountry = "United States";
        } else if (routeType < 0.60) {
            // Europe → North America
            originCountry = random.nextBoolean() ? "Germany" : "United Kingdom";
            destCountry = "United States";
        } else if (routeType < 0.80) {
            // Asia → Europe
            originCountry = random.nextBoolean() ? "China" : "India";
            destCountry = random.nextBoolean() ? "Germany" : "United Kingdom";
        } else {
            // Intra-regional
            String[] regions = {"United States", "China", "Germany"};
            String region = getRandomElement(regions);
            originCountry = region;
            destCountry = region;
        }
        
        // Set origin
        CountryData originData = COUNTRIES.get(originCountry);
        CityData originCity = getRandomElement(originData.cities);
        
        shipment.setOriginCountry(originCountry);
        shipment.setOriginCity(originCity.name);
        shipment.setOriginState(originCity.state);
        shipment.setOriginPostalCode(generatePostalCode(originData.code));
        shipment.setOriginAddress(generateStreetAddress());
        shipment.setOriginLatitude(BigDecimal.valueOf(originCity.latitude));
        shipment.setOriginLongitude(BigDecimal.valueOf(originCity.longitude));
        shipment.setOriginName(generateCompanyName());
        
        // Set destination
        CountryData destData = COUNTRIES.get(destCountry);
        CityData destCity = getRandomElement(destData.cities);
        
        shipment.setDestinationCountry(destCountry);
        shipment.setDestinationCity(destCity.name);
        shipment.setDestinationState(destCity.state);
        shipment.setDestinationPostalCode(generatePostalCode(destData.code));
        shipment.setDestinationAddress(generateStreetAddress());
        shipment.setDestinationLatitude(BigDecimal.valueOf(destCity.latitude));
        shipment.setDestinationLongitude(BigDecimal.valueOf(destCity.longitude));
        shipment.setDestinationName(generateCompanyName() + " Warehouse");
    }
    
    private void generateShipmentStatus(Shipment shipment, LocalDateTime shipDate) {
        LocalDateTime now = LocalDateTime.now();
        long daysSinceShip = java.time.Duration.between(shipDate, now).toDays();
        
        // Calculate expected delivery based on service level and distance
        int expectedTransitDays = calculateExpectedTransitDays(shipment);
        LocalDateTime estimatedDelivery = shipDate.plusDays(expectedTransitDays);
        shipment.setEstimatedDeliveryDate(estimatedDelivery);
        
        // Determine status based on time elapsed
        if (daysSinceShip < 0) {
            // Future shipment
            shipment.setStatus(Shipment.ShipmentStatus.CREATED);
        } else if (daysSinceShip >= expectedTransitDays + 2) {
            // Should have been delivered
            if (random.nextDouble() < 0.95) { // 95% delivered
                shipment.setStatus(Shipment.ShipmentStatus.DELIVERED);
                LocalDateTime actualDelivery = estimatedDelivery.plusDays(random.nextInt(3) - 1); // ±1 day variance
                shipment.setActualDeliveryDate(actualDelivery);
                shipment.setTransitDays((int) java.time.Duration.between(shipDate, actualDelivery).toDays());
                shipment.setOnTimePerformance(actualDelivery.isBefore(estimatedDelivery.plusDays(1)));
            } else { // 5% exception
                shipment.setStatus(Shipment.ShipmentStatus.EXCEPTION);
                shipment.setSubstatus("Delayed - Customs Hold");
            }
        } else if (daysSinceShip >= expectedTransitDays - 1) {
            // Near delivery
            if (random.nextDouble() < 0.7) {
                shipment.setStatus(Shipment.ShipmentStatus.OUT_FOR_DELIVERY);
            } else {
                shipment.setStatus(Shipment.ShipmentStatus.IN_TRANSIT);
                shipment.setSubstatus("Arrived at destination facility");
            }
        } else if (daysSinceShip >= 1) {
            // In transit
            if (random.nextDouble() < 0.92) { // 92% normal transit
                shipment.setStatus(Shipment.ShipmentStatus.IN_TRANSIT);
                shipment.setSubstatus("In transit to destination");
            } else { // 8% delayed
                shipment.setStatus(Shipment.ShipmentStatus.IN_TRANSIT);
                shipment.setSubstatus("Delayed - Weather");
                shipment.setPredictedDelayHours(random.nextInt(48) + 12); // 12-60 hours delay
            }
        } else {
            // Just shipped
            shipment.setStatus(Shipment.ShipmentStatus.PICKED_UP);
            shipment.setSubstatus("Picked up by carrier");
        }
    }
    
    private int calculateExpectedTransitDays(Shipment shipment) {
        // Base transit time on service level and distance
        String serviceLevel = shipment.getServiceLevel();
        boolean isInternational = !shipment.getOriginCountry().equals(shipment.getDestinationCountry());
        
        switch (serviceLevel) {
            case "NEXT_DAY": return isInternational ? 3 : 1;
            case "2_DAY": return isInternational ? 5 : 2;
            case "AIR": return isInternational ? 7 : 3;
            case "GROUND": return isInternational ? 14 : random.nextInt(5) + 3;
            case "OCEAN": return isInternational ? random.nextInt(15) + 20 : 10;
            default: return isInternational ? 10 : 5;
        }
    }
    
    private void generateShippingCost(Shipment shipment) {
        // Base cost calculation on weight, distance, and service level
        double baseCost = shipment.getWeightKg().doubleValue() * 2.5; // $2.50 per kg base
        
        // Service level multiplier
        String serviceLevel = shipment.getServiceLevel();
        double serviceMultiplier = switch (serviceLevel) {
            case "NEXT_DAY" -> 3.0;
            case "2_DAY" -> 2.0;
            case "AIR" -> 1.5;
            case "GROUND" -> 1.0;
            case "OCEAN" -> 0.5;
            default -> 1.0;
        };
        
        // International multiplier
        boolean isInternational = !shipment.getOriginCountry().equals(shipment.getDestinationCountry());
        double internationalMultiplier = isInternational ? 2.5 : 1.0;
        
        // Calculate costs
        double shippingCost = baseCost * serviceMultiplier * internationalMultiplier;
        double fuelSurcharge = shippingCost * 0.15; // 15% fuel surcharge
        
        shipment.setShippingCost(BigDecimal.valueOf(shippingCost));
        shipment.setFuelSurcharge(BigDecimal.valueOf(fuelSurcharge));
        shipment.setTotalCost(BigDecimal.valueOf(shippingCost + fuelSurcharge));
        shipment.setBilledWeightKg(shipment.getWeightKg()); // Simplified - same as actual weight
    }
    
    private BigDecimal calculateCarbonFootprint(Shipment shipment) {
        // Simplified carbon footprint calculation
        double weight = shipment.getWeightKg().doubleValue();
        double distance = calculateDistance(shipment); // km
        
        // CO2 per kg per km by transport mode
        double emissionFactor = switch (shipment.getServiceLevel()) {
            case "AIR", "NEXT_DAY", "2_DAY" -> 0.0005; // kg CO2 per kg per km
            case "GROUND" -> 0.0001;
            case "OCEAN" -> 0.00003;
            default -> 0.0002;
        };
        
        return BigDecimal.valueOf(weight * distance * emissionFactor);
    }
    
    private double calculateDistance(Shipment shipment) {
        // Simplified distance calculation using Haversine formula
        double lat1 = shipment.getOriginLatitude().doubleValue();
        double lon1 = shipment.getOriginLongitude().doubleValue();
        double lat2 = shipment.getDestinationLatitude().doubleValue();
        double lon2 = shipment.getDestinationLongitude().doubleValue();
        
        final int R = 6371; // Radius of the earth in km
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; // Distance in km
    }
    
    private void createTrackingEvents() {
        logger.info("Creating tracking events for shipments...");
        
        List<Shipment> shipments = shipmentRepository.findAll();
        int totalEvents = 0;
        
        for (Shipment shipment : shipments) {
            List<ShipmentTrackingEvent> events = generateTrackingEvents(shipment);
            for (ShipmentTrackingEvent event : events) {
                shipmentTrackingEventRepository.save(event);
                totalEvents++;
            }
        }
        
        logger.info("Created {} tracking events for {} shipments", totalEvents, shipments.size());
    }
    
    private List<ShipmentTrackingEvent> generateTrackingEvents(Shipment shipment) {
        List<ShipmentTrackingEvent> events = new ArrayList<>();
        LocalDateTime currentTime = shipment.getShipDate();
        
        // Always create pickup event
        events.add(createTrackingEvent(shipment, currentTime, "PICKUP", "Package picked up", 
            shipment.getOriginCity(), shipment.getOriginCountry(), 
            shipment.getOriginLatitude(), shipment.getOriginLongitude()));
        
        // Generate intermediate events based on status
        Shipment.ShipmentStatus status = shipment.getStatus();
        LocalDateTime now = LocalDateTime.now();
        
        if (Shipment.ShipmentStatus.DELIVERED.equals(status) || Shipment.ShipmentStatus.OUT_FOR_DELIVERY.equals(status) || Shipment.ShipmentStatus.IN_TRANSIT.equals(status)) {
            // Add transit events
            int numTransitEvents = random.nextInt(5) + 2; // 2-6 transit events
            long totalTransitTime = java.time.Duration.between(currentTime, 
                shipment.getActualDeliveryDate() != null ? shipment.getActualDeliveryDate() : now).toHours();
            
            for (int i = 0; i < numTransitEvents; i++) {
                currentTime = currentTime.plusHours(totalTransitTime / (numTransitEvents + 1));
                
                if (currentTime.isAfter(now)) break;
                
                // Generate intermediate location
                String[] transitEvents = {
                    "Departed from origin facility",
                    "In transit",
                    "Arrived at sorting facility",
                    "Departed from sorting facility",
                    "Customs clearance completed",
                    "Arrived at destination facility"
                };
                
                events.add(createTrackingEvent(shipment, currentTime, "TRANSIT", 
                    getRandomElement(transitEvents), 
                    generateIntermediateCity(), generateIntermediateCountry(),
                    generateIntermediateLatitude(), generateIntermediateLongitude()));
            }
        }
        
        // Add out for delivery event
        if (Shipment.ShipmentStatus.DELIVERED.equals(status) || Shipment.ShipmentStatus.OUT_FOR_DELIVERY.equals(status)) {
            if (shipment.getActualDeliveryDate() != null) {
                currentTime = shipment.getActualDeliveryDate().minusHours(2);
            } else {
                currentTime = currentTime.plusHours(12);
            }
            
            if (!currentTime.isAfter(now)) {
                events.add(createTrackingEvent(shipment, currentTime, "OUT_FOR_DELIVERY", 
                    "Out for delivery", 
                    shipment.getDestinationCity(), shipment.getDestinationCountry(),
                    shipment.getDestinationLatitude(), shipment.getDestinationLongitude()));
            }
        }
        
        // Add delivery event
        if (Shipment.ShipmentStatus.DELIVERED.equals(status) && shipment.getActualDeliveryDate() != null) {
            events.add(createTrackingEvent(shipment, shipment.getActualDeliveryDate(), "DELIVERY", 
                "Package delivered", 
                shipment.getDestinationCity(), shipment.getDestinationCountry(),
                shipment.getDestinationLatitude(), shipment.getDestinationLongitude()));
        }
        
        // Add exception events if needed
        if (Shipment.ShipmentStatus.EXCEPTION.equals(status)) {
            currentTime = currentTime.plusHours(random.nextInt(48) + 24); // 1-3 days later
            events.add(createTrackingEvent(shipment, currentTime, "EXCEPTION", 
                shipment.getSubstatus() != null ? shipment.getSubstatus() : "Delivery exception",
                generateIntermediateCity(), generateIntermediateCountry(),
                generateIntermediateLatitude(), generateIntermediateLongitude()));
        }
        
        return events;
    }
    
    private ShipmentTrackingEvent createTrackingEvent(Shipment shipment, LocalDateTime timestamp, 
                                                    String eventType, String description,
                                                    String city, String country, 
                                                    BigDecimal latitude, BigDecimal longitude) {
        ShipmentTrackingEvent event = new ShipmentTrackingEvent();
        event.setShipment(shipment);
        event.setEventTimestamp(timestamp);
        event.setEventType(ShipmentTrackingEvent.EventType.valueOf(eventType));
        event.setEventCode(eventType.substring(0, 3).toUpperCase());
        event.setEventDescription(description);
        event.setLocationCity(city);
        event.setLocationCountry(country);
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        event.setIsException("EXCEPTION".equals(eventType));
        
        if (event.getIsException()) {
            event.setExceptionReason(description);
        }
        
        return event;
    }
    
    // Helper methods for generating intermediate locations
    private String generateIntermediateCity() {
        String[] cities = {"Memphis", "Louisville", "Cincinnati", "Frankfurt", "Amsterdam", "Hong Kong", "Dubai"};
        return getRandomElement(cities);
    }
    
    private String generateIntermediateCountry() {
        String[] countries = {"United States", "Germany", "Netherlands", "Hong Kong", "UAE", "Singapore"};
        return getRandomElement(countries);
    }
    
    private BigDecimal generateIntermediateLatitude() {
        return BigDecimal.valueOf(random.nextDouble() * 180 - 90); // -90 to 90
    }
    
    private BigDecimal generateIntermediateLongitude() {
        return BigDecimal.valueOf(random.nextDouble() * 360 - 180); // -180 to 180
    }
}