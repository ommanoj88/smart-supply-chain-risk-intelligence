package com.smartsupplychain.riskintelligence.repository;

import com.smartsupplychain.riskintelligence.model.Alert;
import com.smartsupplychain.riskintelligence.model.User;
import com.smartsupplychain.riskintelligence.enums.AlertSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUser(User user);
    List<Alert> findByUserAndIsAcknowledgedFalse(User user);
    List<Alert> findBySeverity(AlertSeverity severity);
    List<Alert> findByIsAcknowledgedFalse();
    
    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId AND a.isAcknowledged = false ORDER BY a.createdAt DESC")
    List<Alert> findUnacknowledgedByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Alert a WHERE a.severity IN :severities AND a.isAcknowledged = false ORDER BY a.createdAt DESC")
    List<Alert> findUnacknowledgedBySeveritiesOrderByCreatedAtDesc(@Param("severities") List<AlertSeverity> severities);
    
    @Query("SELECT a FROM Alert a WHERE a.createdAt BETWEEN :start AND :end ORDER BY a.createdAt DESC")
    List<Alert> findByCreatedAtBetweenOrderByCreatedAtDesc(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}