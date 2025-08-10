package com.supplychain.risk.repository;

import com.supplychain.risk.entity.Supplier;
import com.supplychain.risk.enums.SupplierStatus;
import com.supplychain.risk.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    List<Supplier> findByStatus(SupplierStatus status);
    
    List<Supplier> findByRiskLevel(RiskLevel riskLevel);
    
    List<Supplier> findByCountry(String country);
    
    List<Supplier> findByCity(String city);
    
    @Query("SELECT s FROM Supplier s WHERE s.name LIKE %:name%")
    List<Supplier> findByNameContaining(@Param("name") String name);
    
    @Query("SELECT s FROM Supplier s WHERE s.riskScore >= :minScore AND s.riskScore <= :maxScore")
    List<Supplier> findByRiskScoreBetween(@Param("minScore") BigDecimal minScore, 
                                         @Param("maxScore") BigDecimal maxScore);
    
    @Query("SELECT s FROM Supplier s WHERE s.status = :status AND s.riskLevel = :riskLevel")
    List<Supplier> findByStatusAndRiskLevel(@Param("status") SupplierStatus status, 
                                          @Param("riskLevel") RiskLevel riskLevel);
    
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.status = :status")
    long countByStatus(@Param("status") SupplierStatus status);
    
    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.riskLevel = :riskLevel")
    long countByRiskLevel(@Param("riskLevel") RiskLevel riskLevel);
    
    @Query("SELECT s FROM Supplier s ORDER BY s.riskScore DESC")
    List<Supplier> findAllOrderByRiskScoreDesc();
    
    @Query("SELECT s FROM Supplier s WHERE s.deliveryPerformance < :threshold")
    List<Supplier> findSuppliersWithPoorPerformance(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT AVG(s.riskScore) FROM Supplier s WHERE s.status = :status")
    BigDecimal getAverageRiskScoreByStatus(@Param("status") SupplierStatus status);
}