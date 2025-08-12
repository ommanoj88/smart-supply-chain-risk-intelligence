package com.supplychainrisk.repository;

import com.supplychainrisk.entity.Notification;
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
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByUser(User user, Pageable pageable);
    
    Page<Notification> findByUserAndStatus(User user, Notification.Status status, Pageable pageable);
    
    Page<Notification> findByUserAndCategory(User user, String category, Pageable pageable);
    
    List<Notification> findByStatusAndScheduledForBefore(Notification.Status status, LocalDateTime dateTime);
    
    List<Notification> findByStatusAndRetryCountLessThanAndFailedAtBefore(
            Notification.Status status, Integer maxRetryCount, LocalDateTime retryAfter);
    
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING' AND " +
           "(n.scheduledFor IS NULL OR n.scheduledFor <= :now)")
    List<Notification> findPendingNotifications(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.status = 'PENDING'")
    long countUnreadByUser(@Param("user") User user);
    
    @Query("SELECT n.category, COUNT(n) FROM Notification n WHERE n.user = :user AND n.status = 'PENDING' GROUP BY n.category")
    List<Object[]> countUnreadByUserAndCategory(@Param("user") User user);
}