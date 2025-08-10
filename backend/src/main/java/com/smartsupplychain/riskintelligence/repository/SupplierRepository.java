package com.smartsupplychain.riskintelligence.repository;

import com.smartsupplychain.riskintelligence.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByIsActiveTrue();
    List<Supplier> findByLocationContainingIgnoreCase(String location);
    List<Supplier> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT s FROM Supplier s WHERE s.riskScore >= :minRisk AND s.riskScore <= :maxRisk AND s.isActive = true")
    List<Supplier> findByRiskScoreRange(@Param("minRisk") BigDecimal minRisk, @Param("maxRisk") BigDecimal maxRisk);
    
    @Query("SELECT s FROM Supplier s WHERE s.riskScore >= :threshold AND s.isActive = true ORDER BY s.riskScore DESC")
    List<Supplier> findHighRiskSuppliers(@Param("threshold") BigDecimal threshold);
}