package com.smartsupplychain.riskintelligence.repository;

import com.smartsupplychain.riskintelligence.model.RiskPrediction;
import com.smartsupplychain.riskintelligence.model.Shipment;
import com.smartsupplychain.riskintelligence.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RiskPredictionRepository extends JpaRepository<RiskPrediction, Long> {
    List<RiskPrediction> findByShipment(Shipment shipment);
    List<RiskPrediction> findByRiskLevel(RiskLevel riskLevel);
    
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.createdAt BETWEEN :start AND :end")
    List<RiskPrediction> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.shipment.id = :shipmentId ORDER BY rp.createdAt DESC")
    List<RiskPrediction> findByShipmentIdOrderByCreatedAtDesc(@Param("shipmentId") Long shipmentId);
    
    @Query("SELECT rp FROM RiskPrediction rp WHERE rp.riskLevel IN :riskLevels ORDER BY rp.createdAt DESC")
    List<RiskPrediction> findByRiskLevelsOrderByCreatedAtDesc(@Param("riskLevels") List<RiskLevel> riskLevels);
}