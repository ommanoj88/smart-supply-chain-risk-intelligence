package com.supplychainrisk.dto;

import com.supplychainrisk.entity.Supplier;
import java.util.List;

public class SupplierNetwork {
    
    private Supplier mainSupplier;
    private Supplier parentSupplier;
    private List<Supplier> subsidiaries;
    private List<RelatedSupplier> partners;
    private List<RelatedSupplier> competitors;
    private NetworkMetrics metrics;
    
    // Inner classes
    public static class RelatedSupplier {
        private Long id;
        private String name;
        private String relationship; // PARTNER, COMPETITOR, SUBSIDIARY, PARENT
        private String industry;
        private String country;
        private Double riskScore;
        private String connectionStrength; // STRONG, MEDIUM, WEAK
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
        
        public String getIndustry() { return industry; }
        public void setIndustry(String industry) { this.industry = industry; }
        
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        
        public Double getRiskScore() { return riskScore; }
        public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
        
        public String getConnectionStrength() { return connectionStrength; }
        public void setConnectionStrength(String connectionStrength) { this.connectionStrength = connectionStrength; }
    }
    
    public static class NetworkMetrics {
        private Integer totalConnections;
        private Integer directConnections;
        private Integer indirectConnections;
        private Double networkRiskScore;
        private String networkDiversity; // HIGH, MEDIUM, LOW
        private String networkStability; // STABLE, MODERATE, VOLATILE
        
        // Getters and Setters
        public Integer getTotalConnections() { return totalConnections; }
        public void setTotalConnections(Integer totalConnections) { this.totalConnections = totalConnections; }
        
        public Integer getDirectConnections() { return directConnections; }
        public void setDirectConnections(Integer directConnections) { this.directConnections = directConnections; }
        
        public Integer getIndirectConnections() { return indirectConnections; }
        public void setIndirectConnections(Integer indirectConnections) { this.indirectConnections = indirectConnections; }
        
        public Double getNetworkRiskScore() { return networkRiskScore; }
        public void setNetworkRiskScore(Double networkRiskScore) { this.networkRiskScore = networkRiskScore; }
        
        public String getNetworkDiversity() { return networkDiversity; }
        public void setNetworkDiversity(String networkDiversity) { this.networkDiversity = networkDiversity; }
        
        public String getNetworkStability() { return networkStability; }
        public void setNetworkStability(String networkStability) { this.networkStability = networkStability; }
    }
    
    // Getters and Setters
    public Supplier getMainSupplier() {
        return mainSupplier;
    }
    
    public void setMainSupplier(Supplier mainSupplier) {
        this.mainSupplier = mainSupplier;
    }
    
    public Supplier getParentSupplier() {
        return parentSupplier;
    }
    
    public void setParentSupplier(Supplier parentSupplier) {
        this.parentSupplier = parentSupplier;
    }
    
    public List<Supplier> getSubsidiaries() {
        return subsidiaries;
    }
    
    public void setSubsidiaries(List<Supplier> subsidiaries) {
        this.subsidiaries = subsidiaries;
    }
    
    public List<RelatedSupplier> getPartners() {
        return partners;
    }
    
    public void setPartners(List<RelatedSupplier> partners) {
        this.partners = partners;
    }
    
    public List<RelatedSupplier> getCompetitors() {
        return competitors;
    }
    
    public void setCompetitors(List<RelatedSupplier> competitors) {
        this.competitors = competitors;
    }
    
    public NetworkMetrics getMetrics() {
        return metrics;
    }
    
    public void setMetrics(NetworkMetrics metrics) {
        this.metrics = metrics;
    }
}