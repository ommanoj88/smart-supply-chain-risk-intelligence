package com.supplychainrisk.repository;

import com.supplychainrisk.entity.AnalyticsResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsResultRepository extends JpaRepository<AnalyticsResult, Long> {
    
    /**
     * Find analytics results by analysis type
     */
    List<AnalyticsResult> findByAnalysisTypeAndStatusOrderByCreatedAtDesc(
        String analysisType, AnalyticsResult.AnalyticsStatus status);
    
    /**
     * Find latest analytics result for a specific supplier and analysis type
     */
    Optional<AnalyticsResult> findTopBySupplierIdAndAnalysisTypeAndStatusOrderByCreatedAtDesc(
        Long supplierId, String analysisType, AnalyticsResult.AnalyticsStatus status);
    
    /**
     * Find analytics results within a time range
     */
    @Query("SELECT a FROM AnalyticsResult a WHERE a.createdAt BETWEEN :startDate AND :endDate " +
           "AND a.status = :status ORDER BY a.createdAt DESC")
    Page<AnalyticsResult> findByDateRangeAndStatus(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("status") AnalyticsResult.AnalyticsStatus status,
        Pageable pageable);
    
    /**
     * Find analytics results for multiple suppliers
     */
    @Query("SELECT a FROM AnalyticsResult a WHERE a.supplierId IN :supplierIds " +
           "AND a.analysisType = :analysisType AND a.status = :status " +
           "ORDER BY a.createdAt DESC")
    List<AnalyticsResult> findBySupplierIdsAndAnalysisType(
        @Param("supplierIds") List<Long> supplierIds,
        @Param("analysisType") String analysisType,
        @Param("status") AnalyticsResult.AnalyticsStatus status);
    
    /**
     * Find expired analytics results for cleanup
     */
    @Query("SELECT a FROM AnalyticsResult a WHERE a.expiresAt < :now AND a.status = :status")
    List<AnalyticsResult> findExpiredResults(
        @Param("now") LocalDateTime now,
        @Param("status") AnalyticsResult.AnalyticsStatus status);
    
    /**
     * Count analytics results by analysis type and date range
     */
    @Query("SELECT COUNT(a) FROM AnalyticsResult a WHERE a.analysisType = :analysisType " +
           "AND a.createdAt BETWEEN :startDate AND :endDate")
    Long countByAnalysisTypeAndDateRange(
        @Param("analysisType") String analysisType,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get average accuracy score for a model version
     */
    @Query("SELECT AVG(a.accuracyScore) FROM AnalyticsResult a WHERE a.modelVersion = :modelVersion " +
           "AND a.accuracyScore IS NOT NULL")
    Double getAverageAccuracyByModelVersion(@Param("modelVersion") String modelVersion);
    
    /**
     * Get average processing time for analysis type
     */
    @Query("SELECT AVG(a.processingTimeMs) FROM AnalyticsResult a WHERE a.analysisType = :analysisType " +
           "AND a.processingTimeMs IS NOT NULL")
    Double getAverageProcessingTime(@Param("analysisType") String analysisType);
    
    /**
     * Find recent analytics results for performance monitoring
     */
    @Query("SELECT a FROM AnalyticsResult a WHERE a.createdAt >= :since " +
           "ORDER BY a.createdAt DESC")
    List<AnalyticsResult> findRecentResults(@Param("since") LocalDateTime since);
    
    /**
     * Delete old analytics results for maintenance
     */
    @Query("DELETE FROM AnalyticsResult a WHERE a.createdAt < :cutoffDate " +
           "AND a.status = :status")
    void deleteOldResults(
        @Param("cutoffDate") LocalDateTime cutoffDate,
        @Param("status") AnalyticsResult.AnalyticsStatus status);
}