package com.supplychainrisk.repository;

import com.supplychainrisk.entity.ShipmentItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ShipmentItemRepository extends JpaRepository<ShipmentItem, Long> {

    List<ShipmentItem> findByShipmentId(Long shipmentId);

    List<ShipmentItem> findBySku(String sku);

    @Query("SELECT SUM(i.totalValue) FROM ShipmentItem i WHERE i.shipment.id = :shipmentId")
    BigDecimal getTotalValueByShipmentId(@Param("shipmentId") Long shipmentId);

    @Query("SELECT SUM(i.weightKg * i.quantity) FROM ShipmentItem i WHERE i.shipment.id = :shipmentId")
    BigDecimal getTotalWeightByShipmentId(@Param("shipmentId") Long shipmentId);

    @Query("SELECT SUM(i.quantity) FROM ShipmentItem i WHERE i.shipment.id = :shipmentId")
    Long getTotalQuantityByShipmentId(@Param("shipmentId") Long shipmentId);
}