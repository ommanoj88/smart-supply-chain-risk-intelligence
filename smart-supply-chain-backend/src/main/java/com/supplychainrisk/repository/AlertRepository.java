package com.supplychainrisk.repository;

import com.supplychainrisk.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    // Find by status
    Page<Alert> findByStatus(Alert.AlertStatus status, Pageable pageable);
    
    // Find by severity
    Page<Alert> findBySeverity(Alert.AlertSeverity severity, Pageable pageable);
    
    // Find by category
    Page<Alert> findByCategory(Alert.AlertCategory category, Pageable pageable);
    
    // Find by alert type
    Page<Alert> findByAlertType(Alert.AlertType alertType, Pageable pageable);
    
    // Find by assigned to
    Page<Alert> findByAssignedTo(String assignedTo, Pageable pageable);
    
    // Find by assigned team
    Page<Alert> findByAssignedTeam(String assignedTeam, Pageable pageable);
    
    // Find active alerts (not resolved or closed)
    @Query("SELECT a FROM Alert a WHERE a.status NOT IN ('RESOLVED', 'CLOSED', 'DISMISSED')")
    Page<Alert> findActiveAlerts(Pageable pageable);
    
    // Find alerts requiring escalation
    @Query("SELECT a FROM Alert a WHERE a.status = 'NEW' AND a.detectedAt < :escalationTime")
    List<Alert> findAlertsForEscalation(@Param("escalationTime") LocalDateTime escalationTime);
    
    // Find critical unacknowledged alerts
    @Query("SELECT a FROM Alert a WHERE a.severity = 'CRITICAL' AND a.status = 'NEW'")
    List<Alert> findCriticalUnacknowledgedAlerts();
    
    // Count alerts by severity
    @Query("SELECT a.severity, COUNT(a) FROM Alert a WHERE a.status NOT IN ('RESOLVED', 'CLOSED') GROUP BY a.severity")
    List<Object[]> countAlertsBySeverity();
    
    // Count alerts by status
    @Query("SELECT a.status, COUNT(a) FROM Alert a GROUP BY a.status")
    List<Object[]> countAlertsByStatus();
    
    // Find alerts by supplier
    Page<Alert> findBySupplierId(Long supplierId, Pageable pageable);
    
    // Find alerts by shipment
    Page<Alert> findByShipmentId(Long shipmentId, Pageable pageable);
    
    // Find alerts created in date range
    @Query("SELECT a FROM Alert a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Page<Alert> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate, 
                                       Pageable pageable);
    
    // Find overdue alerts (not acknowledged within time limit)
    @Query("SELECT a FROM Alert a WHERE a.status = 'NEW' AND a.detectedAt < :overdueTime")
    List<Alert> findOverdueAlerts(@Param("overdueTime") LocalDateTime overdueTime);
}