package com.supplychainrisk.repository;

import com.supplychainrisk.entity.RiskAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskAlertRepository extends JpaRepository<RiskAlert, Long> {
    
    Page<RiskAlert> findByStatus(RiskAlert.Status status, Pageable pageable);
    
    Page<RiskAlert> findBySeverity(RiskAlert.Severity severity, Pageable pageable);
    
    Page<RiskAlert> findByAlertType(String alertType, Pageable pageable);
    
    Page<RiskAlert> findBySourceTypeAndSourceId(String sourceType, Long sourceId, Pageable pageable);
    
    List<RiskAlert> findByStatusAndSeverityIn(RiskAlert.Status status, List<RiskAlert.Severity> severities);
    
    @Query("SELECT ra FROM RiskAlert ra WHERE ra.status = 'ACTIVE' AND ra.severity IN ('HIGH', 'CRITICAL')")
    List<RiskAlert> findActiveCriticalAlerts();
    
    @Query("SELECT COUNT(ra) FROM RiskAlert ra WHERE ra.status = :status AND ra.createdAt >= :since")
    long countByStatusSince(@Param("status") RiskAlert.Status status, @Param("since") LocalDateTime since);
    
    @Query("SELECT ra.severity, COUNT(ra) FROM RiskAlert ra WHERE ra.status = 'ACTIVE' GROUP BY ra.severity")
    List<Object[]> countActiveBySeverity();
}