package com.supplychainrisk.repository;

import com.supplychainrisk.entity.SupplierDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierDocumentRepository extends JpaRepository<SupplierDocument, Long> {
    
    // Find documents by supplier
    Page<SupplierDocument> findBySupplierId(Long supplierId, Pageable pageable);
    
    // Find documents by supplier and type
    List<SupplierDocument> findBySupplierIdAndDocumentType(Long supplierId, String documentType);
    
    // Find documents by type
    Page<SupplierDocument> findByDocumentType(String documentType, Pageable pageable);
    
    // Get total file size for a supplier
    @Query("SELECT COALESCE(SUM(sd.fileSize), 0) FROM SupplierDocument sd WHERE sd.supplier.id = :supplierId")
    Long getTotalFileSizeBySupplier(@Param("supplierId") Long supplierId);
    
    // Get document count by type
    @Query("SELECT sd.documentType, COUNT(sd) FROM SupplierDocument sd GROUP BY sd.documentType")
    List<Object[]> getDocumentCountByType();
}