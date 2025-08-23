package com.supplychainrisk.repository;

import com.supplychainrisk.entity.Shipment;
import com.supplychainrisk.entity.Shipment.ShipmentStatus;
import com.supplychainrisk.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    List<Shipment> findByStatus(ShipmentStatus status);

    List<Shipment> findByCarrierName(String carrierName);

    List<Shipment> findBySupplierId(Long supplierId);
    
    List<Shipment> findBySupplier(Supplier supplier);

    @Query("SELECT s FROM Shipment s WHERE s.status = :status AND s.estimatedDeliveryDate < :date")
    List<Shipment> findDelayedShipments(@Param("status") ShipmentStatus status, @Param("date") LocalDateTime date);

    @Query("SELECT s FROM Shipment s WHERE s.status IN :statuses")
    Page<Shipment> findByStatusIn(@Param("statuses") List<ShipmentStatus> statuses, Pageable pageable);

    @Query("SELECT s FROM Shipment s WHERE " +
           "(:carrierName IS NULL OR s.carrierName = :carrierName) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:supplierId IS NULL OR s.supplier.id = :supplierId) AND " +
           "(:fromDate IS NULL OR s.createdAt >= :fromDate) AND " +
           "(:toDate IS NULL OR s.createdAt <= :toDate)")
    Page<Shipment> findShipmentsWithFilters(
            @Param("carrierName") String carrierName,
            @Param("status") ShipmentStatus status,
            @Param("supplierId") Long supplierId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT s FROM Shipment s WHERE " +
           "LOWER(COALESCE(s.trackingNumber, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(COALESCE(s.referenceNumber, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(COALESCE(s.carrierName, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(COALESCE(s.originCity, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(COALESCE(s.destinationCity, '')) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Shipment> searchShipments(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status = :status")
    long countByStatus(@Param("status") ShipmentStatus status);

    @Query("SELECT s.carrierName, COUNT(s) FROM Shipment s GROUP BY s.carrierName")
    List<Object[]> getShipmentCountByCarrier();

    @Query("SELECT s FROM Shipment s WHERE s.riskScore > :riskThreshold")
    List<Shipment> findHighRiskShipments(@Param("riskThreshold") Double riskThreshold);

    @Query("SELECT s FROM Shipment s WHERE s.supplier.id = :supplierId AND s.status = :status")
    List<Shipment> findBySupplierIdAndStatus(@Param("supplierId") Long supplierId, @Param("status") ShipmentStatus status);

    @Query("SELECT s FROM Shipment s WHERE s.estimatedDeliveryDate BETWEEN :startDate AND :endDate")
    List<Shipment> findByEstimatedDeliveryDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}