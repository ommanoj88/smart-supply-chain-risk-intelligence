package com.supplychain.risk.repository;

import com.supplychain.risk.entity.RiskAssessment;
import com.supplychain.risk.entity.Supplier;
import com.supplychain.risk.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    
    List<RiskAssessment> findBySupplier(Supplier supplier);
    
    List<RiskAssessment> findByAssessmentType(String assessmentType);
    
    List<RiskAssessment> findByRiskLevel(RiskLevel riskLevel);
    
    List<RiskAssessment> findByIsActiveTrue();
    
    @Query("SELECT r FROM RiskAssessment r WHERE r.supplier.id = :supplierId AND r.isActive = true")
    List<RiskAssessment> findActiveAssessmentsBySupplierId(@Param("supplierId") Long supplierId);
    
    @Query("SELECT r FROM RiskAssessment r WHERE r.supplier.id = :supplierId AND r.assessmentType = :type AND r.isActive = true")
    Optional<RiskAssessment> findActiveAssessmentBySupplierIdAndType(@Param("supplierId") Long supplierId, 
                                                                   @Param("type") String assessmentType);
    
    @Query("SELECT r FROM RiskAssessment r WHERE r.nextReviewDate <= :date AND r.isActive = true")
    List<RiskAssessment> findAssessmentsDueForReview(@Param("date") LocalDateTime date);
    
    @Query("SELECT r FROM RiskAssessment r WHERE r.riskScore >= :minScore AND r.riskScore <= :maxScore AND r.isActive = true")
    List<RiskAssessment> findByRiskScoreBetween(@Param("minScore") BigDecimal minScore, 
                                              @Param("maxScore") BigDecimal maxScore);
    
    @Query("SELECT r FROM RiskAssessment r WHERE r.supplier.id = :supplierId ORDER BY r.createdAt DESC")
    List<RiskAssessment> findBySupplierIdOrderByCreatedAtDesc(@Param("supplierId") Long supplierId);
    
    @Query("SELECT AVG(r.riskScore) FROM RiskAssessment r WHERE r.assessmentType = :type AND r.isActive = true")
    BigDecimal getAverageRiskScoreByType(@Param("type") String assessmentType);
    
    @Query("SELECT COUNT(r) FROM RiskAssessment r WHERE r.riskLevel = :riskLevel AND r.isActive = true")
    long countActiveAssessmentsByRiskLevel(@Param("riskLevel") RiskLevel riskLevel);
    
    @Query("SELECT COUNT(r) FROM RiskAssessment r WHERE r.assessmentType = :type AND r.isActive = true")
    long countActiveAssessmentsByType(@Param("type") String assessmentType);
    
    @Query("SELECT r FROM RiskAssessment r WHERE r.createdAt >= :startDate AND r.isActive = true ORDER BY r.createdAt DESC")
    List<RiskAssessment> findRecentAssessments(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT DISTINCT r.assessmentType FROM RiskAssessment r WHERE r.isActive = true")
    List<String> findDistinctAssessmentTypes();
}