package com.supplychainrisk.repository;

import com.supplychainrisk.entity.SupplierCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SupplierCertificationRepository extends JpaRepository<SupplierCertification, Long> {
    
    List<SupplierCertification> findBySupplierIdAndStatus(Long supplierId, SupplierCertification.Status status);
    
    List<SupplierCertification> findBySupplierIdOrderByExpiryDateAsc(Long supplierId);
    
    @Query("SELECT sc FROM SupplierCertification sc WHERE sc.supplier.id = :supplierId AND sc.certificationType = :type")
    List<SupplierCertification> findBySupplierIdAndCertificationType(@Param("supplierId") Long supplierId, 
                                                                     @Param("type") String certificationType);
    
    @Query("SELECT sc FROM SupplierCertification sc WHERE sc.expiryDate BETWEEN :startDate AND :endDate")
    List<SupplierCertification> findCertificationsExpiringBetween(@Param("startDate") LocalDate startDate, 
                                                                  @Param("endDate") LocalDate endDate);
    
    @Query("SELECT sc FROM SupplierCertification sc WHERE sc.expiryDate <= :date AND sc.status = 'ACTIVE'")
    List<SupplierCertification> findExpiredCertifications(@Param("date") LocalDate date);
}