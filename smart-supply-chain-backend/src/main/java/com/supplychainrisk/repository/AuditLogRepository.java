package com.supplychainrisk.repository;

import com.supplychainrisk.entity.AuditLog;
import com.supplychainrisk.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUserOrderByTimestampDesc(User user);
    
    Page<AuditLog> findByUserOrderByTimestampDesc(User user, Pageable pageable);
    
    List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId);
    
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
    
    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AuditLog> findByTimestampBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user = :user AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<AuditLog> findRecentUserActions(@Param("user") User user, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.timestamp >= :since")
    Long countActionsAfter(@Param("since") LocalDateTime since);
}