package com.supplychainrisk.repository;

import com.supplychainrisk.entity.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {
    
    // Find deliveries by notification
    List<NotificationDelivery> findByNotificationId(Long notificationId);
    
    // Find deliveries by status
    List<NotificationDelivery> findByDeliveryStatus(NotificationDelivery.DeliveryStatus status);
    
    // Find failed deliveries for retry
    @Query("SELECT nd FROM NotificationDelivery nd WHERE nd.deliveryStatus = 'FAILED' AND nd.retryCount < 3")
    List<NotificationDelivery> findFailedDeliveriesForRetry();
    
    // Find deliveries by channel
    List<NotificationDelivery> findByChannel(com.supplychainrisk.entity.Notification.NotificationChannel channel);
    
    // Find deliveries by recipient
    List<NotificationDelivery> findByRecipient(String recipient);
    
    // Find deliveries in date range
    @Query("SELECT nd FROM NotificationDelivery nd WHERE nd.createdAt BETWEEN :startDate AND :endDate")
    List<NotificationDelivery> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    // Get delivery statistics by channel
    @Query("SELECT nd.channel, nd.deliveryStatus, COUNT(nd) FROM NotificationDelivery nd GROUP BY nd.channel, nd.deliveryStatus")
    List<Object[]> getDeliveryStatisticsByChannel();
    
    // Get delivery statistics by status
    @Query("SELECT nd.deliveryStatus, COUNT(nd) FROM NotificationDelivery nd GROUP BY nd.deliveryStatus")
    List<Object[]> getDeliveryStatisticsByStatus();
}