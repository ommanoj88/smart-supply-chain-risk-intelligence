package com.smartsupplychain.riskintelligence.repository;

import com.smartsupplychain.riskintelligence.model.Shipment;
import com.smartsupplychain.riskintelligence.model.Supplier;
import com.smartsupplychain.riskintelligence.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByStatus(ShipmentStatus status);
    List<Shipment> findBySupplier(Supplier supplier);
    List<Shipment> findByTrackingNumber(String trackingNumber);
    
    @Query("SELECT s FROM Shipment s WHERE s.estimatedArrival BETWEEN :start AND :end")
    List<Shipment> findByEstimatedArrivalBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT s FROM Shipment s WHERE s.estimatedArrival < :date AND s.status IN :statuses")
    List<Shipment> findDelayedShipments(@Param("date") LocalDateTime date, @Param("statuses") List<ShipmentStatus> statuses);
    
    @Query("SELECT s FROM Shipment s WHERE s.supplier.id = :supplierId ORDER BY s.lastUpdate DESC")
    List<Shipment> findBySupplierIdOrderByLastUpdateDesc(@Param("supplierId") Long supplierId);
}