package com.supplychainrisk.repository;

import com.supplychainrisk.entity.MLModelMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MLModelMetricsRepository extends JpaRepository<MLModelMetrics, Long> {
    
    /**
     * Find metrics for a specific model ID
     */
    List<MLModelMetrics> findByModelIdOrderByEvaluationDateDesc(String modelId);
    
    /**
     * Find latest metrics for a specific model
     */
    @Query("SELECT mmm FROM MLModelMetrics mmm WHERE mmm.modelId = :modelId ORDER BY mmm.evaluationDate DESC LIMIT 1")
    Optional<MLModelMetrics> findLatestByModelId(@Param("modelId") String modelId);
    
    /**
     * Find metrics by model version
     */
    List<MLModelMetrics> findByModelVersionOrderByEvaluationDateDesc(String modelVersion);
    
    /**
     * Find metrics by model type
     */
    List<MLModelMetrics> findByModelTypeOrderByEvaluationDateDesc(MLModelMetrics.ModelType modelType);
    
    /**
     * Find metrics within date range
     */
    @Query("SELECT mmm FROM MLModelMetrics mmm WHERE mmm.evaluationDate >= :startDate AND mmm.evaluationDate <= :endDate ORDER BY mmm.evaluationDate DESC")
    List<MLModelMetrics> findByEvaluationDateBetween(@Param("startDate") LocalDateTime startDate, 
                                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find models with drift alerts triggered
     */
    @Query("SELECT mmm FROM MLModelMetrics mmm WHERE mmm.driftAlertTriggered = true ORDER BY mmm.evaluationDate DESC")
    List<MLModelMetrics> findModelsWithDriftAlerts();
    
    /**
     * Find models with accuracy below threshold
     */
    @Query("SELECT mmm FROM MLModelMetrics mmm WHERE mmm.accuracy < :minAccuracy ORDER BY mmm.accuracy ASC")
    List<MLModelMetrics> findByAccuracyLessThan(@Param("minAccuracy") java.math.BigDecimal minAccuracy);
    
    /**
     * Find models with high business impact
     */
    @Query("SELECT mmm FROM MLModelMetrics mmm WHERE mmm.businessImpactScore >= :minImpact ORDER BY mmm.businessImpactScore DESC")
    List<MLModelMetrics> findByBusinessImpactScoreGreaterThanEqual(@Param("minImpact") java.math.BigDecimal minImpact);
    
    /**
     * Get average accuracy by model type
     */
    @Query("SELECT mmm.modelType, AVG(mmm.accuracy) FROM MLModelMetrics mmm GROUP BY mmm.modelType")
    List<Object[]> averageAccuracyByModelType();
    
    /**
     * Get performance trends for a model
     */
    @Query("SELECT mmm.evaluationDate, mmm.accuracy, mmm.precision, mmm.recall FROM MLModelMetrics mmm WHERE mmm.modelId = :modelId ORDER BY mmm.evaluationDate ASC")
    List<Object[]> getPerformanceTrend(@Param("modelId") String modelId);
    
    /**
     * Find models requiring retraining (high drift scores)
     */
    @Query("SELECT mmm FROM MLModelMetrics mmm WHERE mmm.dataDriftScore > :driftThreshold OR mmm.modelDriftScore > :driftThreshold ORDER BY mmm.dataDriftScore DESC, mmm.modelDriftScore DESC")
    List<MLModelMetrics> findModelsRequiringRetraining(@Param("driftThreshold") java.math.BigDecimal driftThreshold);
    
    /**
     * Get total cost savings across all models
     */
    @Query("SELECT SUM(mmm.costSavings) FROM MLModelMetrics mmm WHERE mmm.evaluationDate >= :since")
    java.math.BigDecimal getTotalCostSavingsSince(@Param("since") LocalDateTime since);
    
    /**
     * Find top performing models by accuracy
     */
    @Query("SELECT mmm FROM MLModelMetrics mmm ORDER BY mmm.accuracy DESC LIMIT :limit")
    List<MLModelMetrics> findTopPerformingModels(@Param("limit") int limit);
    
    /**
     * Count models by type
     */
    @Query("SELECT mmm.modelType, COUNT(DISTINCT mmm.modelId) FROM MLModelMetrics mmm GROUP BY mmm.modelType")
    List<Object[]> countModelsByType();
    
    /**
     * Find models with recent evaluations
     */
    @Query("SELECT mmm FROM MLModelMetrics mmm WHERE mmm.evaluationDate >= :since ORDER BY mmm.evaluationDate DESC")
    List<MLModelMetrics> findRecentEvaluations(@Param("since") LocalDateTime since);
    
    /**
     * Get model performance summary
     */
    @Query("SELECT mmm.modelId, mmm.modelName, MAX(mmm.accuracy), MAX(mmm.evaluationDate) FROM MLModelMetrics mmm GROUP BY mmm.modelId, mmm.modelName ORDER BY MAX(mmm.accuracy) DESC")
    List<Object[]> getModelPerformanceSummary();
    
    /**
     * Delete old metrics (cleanup)
     */
    @Query("DELETE FROM MLModelMetrics mmm WHERE mmm.evaluationDate < :cutoffDate")
    void deleteOldMetrics(@Param("cutoffDate") LocalDateTime cutoffDate);
}