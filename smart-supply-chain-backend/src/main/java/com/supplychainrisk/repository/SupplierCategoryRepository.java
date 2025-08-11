package com.supplychainrisk.repository;

import com.supplychainrisk.entity.SupplierCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierCategoryRepository extends JpaRepository<SupplierCategory, Long> {
    
    // Find by name
    Optional<SupplierCategory> findByName(String name);
    
    // Check if category name exists
    boolean existsByName(String name);
    
    // Find all categories ordered by name
    List<SupplierCategory> findAllByOrderByNameAsc();
    
    // Get category usage statistics
    @Query("SELECT c.name, COUNT(s) FROM SupplierCategory c LEFT JOIN c.suppliers s GROUP BY c.id, c.name")
    List<Object[]> getCategoryUsageStatistics();
}