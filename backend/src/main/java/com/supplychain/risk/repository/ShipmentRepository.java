package com.supplychain.risk.repository;

import com.supplychain.risk.entity.Shipment;
import com.supplychain.risk.entity.Supplier;
import com.supplychain.risk.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    
    List<Shipment> findBySupplier(Supplier supplier);
    
    List<Shipment> findByStatus(ShipmentStatus status);
    
    List<Shipment> findByProductName(String productName);
    
    List<Shipment> findByProductCategory(String productCategory);
    
    @Query("SELECT s FROM Shipment s WHERE s.supplier.id = :supplierId")
    List<Shipment> findBySupplierId(@Param("supplierId") Long supplierId);
    
    @Query("SELECT s FROM Shipment s WHERE s.status = :status AND s.supplier.id = :supplierId")
    List<Shipment> findByStatusAndSupplierId(@Param("status") ShipmentStatus status, 
                                           @Param("supplierId") Long supplierId);
    
    @Query("SELECT s FROM Shipment s WHERE s.expectedDeliveryDate BETWEEN :startDate AND :endDate")
    List<Shipment> findByExpectedDeliveryDateBetween(@Param("startDate") LocalDate startDate, 
                                                    @Param("endDate") LocalDate endDate);
    
    @Query("SELECT s FROM Shipment s WHERE s.expectedDeliveryDate < :date AND s.status NOT IN ('DELIVERED', 'CANCELLED')")
    List<Shipment> findOverdueShipments(@Param("date") LocalDate date);
    
    @Query("SELECT s FROM Shipment s WHERE s.status = 'DELAYED'")
    List<Shipment> findDelayedShipments();
    
    @Query("SELECT s FROM Shipment s WHERE s.isPriority = true")
    List<Shipment> findPriorityShipments();
    
    @Query("SELECT s FROM Shipment s WHERE s.createdAt >= :startDate ORDER BY s.createdAt DESC")
    List<Shipment> findRecentShipments(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status = :status")
    long countByStatus(@Param("status") ShipmentStatus status);
    
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.supplier.id = :supplierId")
    long countBySupplierId(@Param("supplierId") Long supplierId);
    
    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.expectedDeliveryDate < CURRENT_DATE AND s.status NOT IN ('DELIVERED', 'CANCELLED')")
    long countOverdueShipments();
    
    @Query("SELECT s FROM Shipment s WHERE s.carrierName = :carrierName")
    List<Shipment> findByCarrierName(@Param("carrierName") String carrierName);
    
    boolean existsByTrackingNumber(String trackingNumber);
}