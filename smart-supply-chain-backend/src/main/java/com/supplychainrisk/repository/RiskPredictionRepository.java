package com.supplychainrisk.repository;

import com.supplychainrisk.entity.RiskPrediction;
import com.supplychainrisk.entity.Supplier;
import com.supplychainrisk.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskPredictionRepository extends JpaRepository<RiskPrediction, Long> {
    
    /**
     * Find all risk predictions for a specific supplier
     */
    List<RiskPrediction> findBySupplierOrderByPredictionDateDesc(Supplier supplier);
    
    /**
     * Find all risk predictions for a specific supplier ID
     */
    List<RiskPrediction> findBySupplier_IdOrderByPredictionDateDesc(Long supplierId);
    
    /**
     * Find all risk predictions for a specific shipment
     */
    List<RiskPrediction> findByShipmentOrderByPredictionDateDesc(Shipment shipment);
    
    /**
     * Find risk predictions by risk type
     */
    List<RiskPrediction> findByRiskTypeOrderByPredictionDateDesc(RiskPrediction.RiskType riskType);
    
    /**
     * Find risk predictions by predicted risk level
     */
    List<RiskPrediction> findByPredictedRiskLevelOrderByPredictionDateDesc(RiskPrediction.RiskLevel riskLevel);
    
    /**
     * Find recent risk predictions within a date range
     */
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.predictionDate >= :startDate AND rp.predictionDate <= :endDate ORDER BY rp.predictionDate DESC")
    List<RiskPrediction> findByPredictionDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find risk predictions for suppliers with high risk levels
     */
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.predictedRiskLevel IN ('HIGH', 'CRITICAL') ORDER BY rp.predictionDate DESC")
    List<RiskPrediction> findHighRiskPredictions();
    
    /**
     * Find recent risk predictions for a supplier
     */
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.supplier.id = :supplierId AND rp.predictionDate >= :since ORDER BY rp.predictionDate DESC")
    List<RiskPrediction> findRecentPredictionsForSupplier(@Param("supplierId") Long supplierId, 
                                                         @Param("since") LocalDateTime since);
    
    /**
     * Find risk predictions with confidence above threshold
     */
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.confidence >= :minConfidence ORDER BY rp.confidence DESC, rp.predictionDate DESC")
    List<RiskPrediction> findByConfidenceGreaterThanEqual(@Param("minConfidence") java.math.BigDecimal minConfidence);
    
    /**
     * Find risk predictions by model version
     */
    List<RiskPrediction> findByModelVersionOrderByPredictionDateDesc(String modelVersion);
    
    /**
     * Count risk predictions by risk type
     */
    @Query("SELECT rp.riskType, COUNT(rp) FROM RiskPrediction rp GROUP BY rp.riskType")
    List<Object[]> countByRiskType();
    
    /**
     * Find average confidence by risk type
     */
    @Query("SELECT rp.riskType, AVG(rp.confidence) FROM RiskPrediction rp GROUP BY rp.riskType")
    List<Object[]> averageConfidenceByRiskType();
    
    /**
     * Find risk predictions that need attention (high impact, low confidence)
     */
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.impactSeverity IN ('SIGNIFICANT', 'SEVERE', 'CATASTROPHIC') AND rp.confidence < :confidenceThreshold ORDER BY rp.estimatedCostImpact DESC")
    List<RiskPrediction> findPredictionsNeedingAttention(@Param("confidenceThreshold") java.math.BigDecimal confidenceThreshold);
    
    /**
     * Find predictions with estimated cost impact above threshold
     */
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.estimatedCostImpact >= :minCostImpact ORDER BY rp.estimatedCostImpact DESC")
    List<RiskPrediction> findByCostImpactGreaterThanEqual(@Param("minCostImpact") java.math.BigDecimal minCostImpact);
    
    /**
     * Delete old risk predictions (cleanup)
     */
    @Query("DELETE FROM RiskPrediction rp WHERE rp.predictionDate < :cutoffDate")
    void deleteOldPredictions(@Param("cutoffDate") LocalDateTime cutoffDate);
}