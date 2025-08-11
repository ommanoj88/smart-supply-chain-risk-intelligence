package com.supplychainrisk.repository;

import com.supplychainrisk.entity.SupplierPerformanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SupplierPerformanceHistoryRepository extends JpaRepository<SupplierPerformanceHistory, Long> {
    
    // Find performance history by supplier
    Page<SupplierPerformanceHistory> findBySupplierId(Long supplierId, Pageable pageable);
    
    // Find performance history by supplier and date range
    @Query("SELECT sph FROM SupplierPerformanceHistory sph WHERE " +
           "sph.supplier.id = :supplierId AND " +
           "sph.performanceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sph.performanceDate DESC")
    List<SupplierPerformanceHistory> findBySupplierIdAndDateRange(
        @Param("supplierId") Long supplierId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Get latest performance record for a supplier
    @Query("SELECT sph FROM SupplierPerformanceHistory sph WHERE " +
           "sph.supplier.id = :supplierId " +
           "ORDER BY sph.performanceDate DESC")
    List<SupplierPerformanceHistory> findLatestBySupplier(@Param("supplierId") Long supplierId, Pageable pageable);
    
    // Get average performance metrics for a supplier
    @Query("SELECT AVG(sph.onTimeDeliveryRate), AVG(sph.qualityScore), " +
           "AVG(sph.costScore), AVG(sph.overallScore) " +
           "FROM SupplierPerformanceHistory sph WHERE sph.supplier.id = :supplierId")
    List<Object[]> getAveragePerformanceMetrics(@Param("supplierId") Long supplierId);
    
    // Get performance trend for a supplier (last N records)
    @Query("SELECT sph FROM SupplierPerformanceHistory sph WHERE " +
           "sph.supplier.id = :supplierId " +
           "ORDER BY sph.performanceDate DESC")
    List<SupplierPerformanceHistory> getPerformanceTrend(@Param("supplierId") Long supplierId, Pageable pageable);
    
    // Find performance records by date range
    @Query("SELECT sph FROM SupplierPerformanceHistory sph WHERE " +
           "sph.performanceDate BETWEEN :startDate AND :endDate " +
           "ORDER BY sph.performanceDate DESC")
    Page<SupplierPerformanceHistory> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
}