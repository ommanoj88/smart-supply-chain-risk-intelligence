package com.supplychainrisk.repository;

import com.supplychainrisk.entity.RiskFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskFactorRepository extends JpaRepository<RiskFactor, Long> {
    
    List<RiskFactor> findBySupplierIdAndIsActive(Long supplierId, Boolean isActive);
    
    List<RiskFactor> findBySupplierIdOrderByRiskScoreDesc(Long supplierId);
    
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.supplier.id = :supplierId AND rf.factorType = :factorType AND rf.isActive = true")
    List<RiskFactor> findBySupplierIdAndFactorType(@Param("supplierId") Long supplierId, 
                                                   @Param("factorType") String factorType);
    
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.supplier.id = :supplierId AND rf.severity = :severity AND rf.isActive = true")
    List<RiskFactor> findBySupplierIdAndSeverity(@Param("supplierId") Long supplierId, 
                                                 @Param("severity") RiskFactor.Severity severity);
    
    @Query("SELECT AVG(rf.riskScore) FROM RiskFactor rf WHERE rf.supplier.id = :supplierId AND rf.isActive = true")
    Double calculateAverageRiskScore(@Param("supplierId") Long supplierId);
}