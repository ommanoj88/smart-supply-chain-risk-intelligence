package com.supplychainrisk.repository;

import com.supplychainrisk.entity.ShipmentTrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShipmentTrackingEventRepository extends JpaRepository<ShipmentTrackingEvent, Long> {

    List<ShipmentTrackingEvent> findByShipmentIdOrderByEventTimestampDesc(Long shipmentId);

    List<ShipmentTrackingEvent> findByShipmentIdOrderByEventTimestampAsc(Long shipmentId);

    @Query("SELECT e FROM ShipmentTrackingEvent e WHERE e.shipment.id = :shipmentId AND e.eventTimestamp BETWEEN :fromDate AND :toDate ORDER BY e.eventTimestamp DESC")
    List<ShipmentTrackingEvent> findByShipmentIdAndDateRange(@Param("shipmentId") Long shipmentId, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    List<ShipmentTrackingEvent> findByIsExceptionTrueOrderByEventTimestampDesc();

    @Query("SELECT e FROM ShipmentTrackingEvent e WHERE e.isException = true AND e.shipment.id = :shipmentId ORDER BY e.eventTimestamp DESC")
    List<ShipmentTrackingEvent> findExceptionsByShipmentId(@Param("shipmentId") Long shipmentId);

    @Query("SELECT e FROM ShipmentTrackingEvent e WHERE e.eventTimestamp >= :fromDate ORDER BY e.eventTimestamp DESC")
    List<ShipmentTrackingEvent> findRecentEvents(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT COUNT(e) FROM ShipmentTrackingEvent e WHERE e.isException = true AND e.eventTimestamp >= :fromDate")
    long countExceptionsSince(@Param("fromDate") LocalDateTime fromDate);
}