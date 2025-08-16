package com.supplychainrisk.repository;

import com.supplychainrisk.entity.SupplierLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierLocationRepository extends JpaRepository<SupplierLocation, Long> {
    
    List<SupplierLocation> findBySupplierIdAndIsPrimary(Long supplierId, Boolean isPrimary);
    
    List<SupplierLocation> findBySupplierIdOrderByIsPrimaryDesc(Long supplierId);
    
    @Query("SELECT sl FROM SupplierLocation sl WHERE sl.supplier.id = :supplierId AND sl.type = :type")
    List<SupplierLocation> findBySupplierIdAndType(@Param("supplierId") Long supplierId, 
                                                   @Param("type") SupplierLocation.LocationType type);
    
    @Query("SELECT sl FROM SupplierLocation sl WHERE sl.country = :country")
    List<SupplierLocation> findByCountry(@Param("country") String country);
    
    @Query("SELECT DISTINCT sl.country FROM SupplierLocation sl ORDER BY sl.country")
    List<String> findDistinctCountries();
}