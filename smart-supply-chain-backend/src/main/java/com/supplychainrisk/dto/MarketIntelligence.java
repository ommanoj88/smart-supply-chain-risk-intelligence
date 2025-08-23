package com.supplychainrisk.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MarketIntelligence {
    
    private IndustryAnalysis industryAnalysis;
    private List<CompetitorAnalysis> competitors;
    private List<MarketTrend> marketTrends;
    private RegulatoryEnvironment regulatoryEnvironment;
    private LocalDateTime generatedAt;
    
    // Default constructor
    public MarketIntelligence() {
        this.generatedAt = LocalDateTime.now();
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private MarketIntelligence intelligence = new MarketIntelligence();
        
        public Builder industryAnalysis(IndustryAnalysis industryAnalysis) {
            intelligence.industryAnalysis = industryAnalysis;
            return this;
        }
        
        public Builder competitors(List<CompetitorAnalysis> competitors) {
            intelligence.competitors = competitors;
            return this;
        }
        
        public Builder marketTrends(List<MarketTrend> marketTrends) {
            intelligence.marketTrends = marketTrends;
            return this;
        }
        
        public Builder regulatoryEnvironment(RegulatoryEnvironment regulatoryEnvironment) {
            intelligence.regulatoryEnvironment = regulatoryEnvironment;
            return this;
        }
        
        public MarketIntelligence build() {
            return intelligence;
        }
    }
    
    // Inner classes
    public static class IndustryAnalysis {
        private String industry;
        private Double marketSize;
        private Double growthRate;
        private String competitionLevel; // LOW, MEDIUM, HIGH
        private List<String> keyTrends;
        private List<String> challenges;
        private List<String> opportunities;
        
        // Getters and Setters
        public String getIndustry() { return industry; }
        public void setIndustry(String industry) { this.industry = industry; }
        
        public Double getMarketSize() { return marketSize; }
        public void setMarketSize(Double marketSize) { this.marketSize = marketSize; }
        
        public Double getGrowthRate() { return growthRate; }
        public void setGrowthRate(Double growthRate) { this.growthRate = growthRate; }
        
        public String getCompetitionLevel() { return competitionLevel; }
        public void setCompetitionLevel(String competitionLevel) { this.competitionLevel = competitionLevel; }
        
        public List<String> getKeyTrends() { return keyTrends; }
        public void setKeyTrends(List<String> keyTrends) { this.keyTrends = keyTrends; }
        
        public List<String> getChallenges() { return challenges; }
        public void setChallenges(List<String> challenges) { this.challenges = challenges; }
        
        public List<String> getOpportunities() { return opportunities; }
        public void setOpportunities(List<String> opportunities) { this.opportunities = opportunities; }
    }
    
    public static class CompetitorAnalysis {
        private String name;
        private String marketPosition;
        private Double marketShare;
        private List<String> strengths;
        private List<String> weaknesses;
        private String threatLevel; // LOW, MEDIUM, HIGH
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getMarketPosition() { return marketPosition; }
        public void setMarketPosition(String marketPosition) { this.marketPosition = marketPosition; }
        
        public Double getMarketShare() { return marketShare; }
        public void setMarketShare(Double marketShare) { this.marketShare = marketShare; }
        
        public List<String> getStrengths() { return strengths; }
        public void setStrengths(List<String> strengths) { this.strengths = strengths; }
        
        public List<String> getWeaknesses() { return weaknesses; }
        public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }
        
        public String getThreatLevel() { return threatLevel; }
        public void setThreatLevel(String threatLevel) { this.threatLevel = threatLevel; }
    }
    
    public static class MarketTrend {
        private String name;
        private String description;
        private String impact; // POSITIVE, NEGATIVE, NEUTRAL
        private String timeframe; // SHORT_TERM, MEDIUM_TERM, LONG_TERM
        private Double confidence;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getImpact() { return impact; }
        public void setImpact(String impact) { this.impact = impact; }
        
        public String getTimeframe() { return timeframe; }
        public void setTimeframe(String timeframe) { this.timeframe = timeframe; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
    }
    
    public static class RegulatoryEnvironment {
        private List<String> applicableRegulations;
        private List<String> upcomingChanges;
        private String complianceRisk; // LOW, MEDIUM, HIGH
        private List<String> recommendations;
        
        // Getters and Setters
        public List<String> getApplicableRegulations() { return applicableRegulations; }
        public void setApplicableRegulations(List<String> applicableRegulations) { this.applicableRegulations = applicableRegulations; }
        
        public List<String> getUpcomingChanges() { return upcomingChanges; }
        public void setUpcomingChanges(List<String> upcomingChanges) { this.upcomingChanges = upcomingChanges; }
        
        public String getComplianceRisk() { return complianceRisk; }
        public void setComplianceRisk(String complianceRisk) { this.complianceRisk = complianceRisk; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
    
    // Getters and Setters
    public IndustryAnalysis getIndustryAnalysis() {
        return industryAnalysis;
    }
    
    public void setIndustryAnalysis(IndustryAnalysis industryAnalysis) {
        this.industryAnalysis = industryAnalysis;
    }
    
    public List<CompetitorAnalysis> getCompetitors() {
        return competitors;
    }
    
    public void setCompetitors(List<CompetitorAnalysis> competitors) {
        this.competitors = competitors;
    }
    
    public List<MarketTrend> getMarketTrends() {
        return marketTrends;
    }
    
    public void setMarketTrends(List<MarketTrend> marketTrends) {
        this.marketTrends = marketTrends;
    }
    
    public RegulatoryEnvironment getRegulatoryEnvironment() {
        return regulatoryEnvironment;
    }
    
    public void setRegulatoryEnvironment(RegulatoryEnvironment regulatoryEnvironment) {
        this.regulatoryEnvironment = regulatoryEnvironment;
    }
    
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}