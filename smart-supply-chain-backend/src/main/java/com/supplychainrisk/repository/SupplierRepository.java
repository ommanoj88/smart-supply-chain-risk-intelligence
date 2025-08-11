package com.supplychainrisk.repository;

import com.supplychainrisk.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    // Find by supplier code
    Optional<Supplier> findBySupplierCode(String supplierCode);
    
    // Check if supplier code exists
    boolean existsBySupplierCode(String supplierCode);
    
    // Find by status
    Page<Supplier> findByStatus(Supplier.SupplierStatus status, Pageable pageable);
    
    // Find by tier
    Page<Supplier> findByTier(Supplier.SupplierTier tier, Pageable pageable);
    
    // Find by country
    Page<Supplier> findByCountry(String country, Pageable pageable);
    
    // Find by industry
    Page<Supplier> findByIndustry(String industry, Pageable pageable);
    
    // Find preferred suppliers
    Page<Supplier> findByPreferredSupplierTrue(Pageable pageable);
    
    // Find strategic suppliers
    Page<Supplier> findByStrategicSupplierTrue(Pageable pageable);
    
    // Advanced search by name or supplier code
    @Query("SELECT s FROM Supplier s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.supplierCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.legalName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Supplier> findByNameOrSupplierCodeContaining(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find by risk score range
    @Query("SELECT s FROM Supplier s WHERE s.overallRiskScore BETWEEN :minScore AND :maxScore")
    Page<Supplier> findByRiskScoreRange(@Param("minScore") Integer minScore, 
                                       @Param("maxScore") Integer maxScore, 
                                       Pageable pageable);
    
    // Find suppliers requiring audit (next audit due date passed)
    @Query("SELECT s FROM Supplier s WHERE s.nextAuditDueDate <= CURRENT_DATE")
    List<Supplier> findSuppliersRequiringAudit();
    
    // Find suppliers by category
    @Query("SELECT DISTINCT s FROM Supplier s JOIN s.categories c WHERE c.name = :categoryName")
    Page<Supplier> findByCategory(@Param("categoryName") String categoryName, Pageable pageable);
    
    // Get supplier count by status
    @Query("SELECT s.status, COUNT(s) FROM Supplier s GROUP BY s.status")
    List<Object[]> getSupplierCountByStatus();
    
    // Get supplier count by tier
    @Query("SELECT s.tier, COUNT(s) FROM Supplier s GROUP BY s.tier")
    List<Object[]> getSupplierCountByTier();
    
    // Get average risk scores
    @Query("SELECT AVG(s.overallRiskScore), AVG(s.financialRiskScore), " +
           "AVG(s.operationalRiskScore), AVG(s.complianceRiskScore), " +
           "AVG(s.geographicRiskScore) FROM Supplier s WHERE s.status = :status")
    List<Object[]> getAverageRiskScores(@Param("status") Supplier.SupplierStatus status);
    
    // Find suppliers by multiple criteria
    @Query("SELECT s FROM Supplier s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:tier IS NULL OR s.tier = :tier) AND " +
           "(:country IS NULL OR LOWER(s.country) LIKE LOWER(CONCAT('%', :country, '%'))) AND " +
           "(:industry IS NULL OR LOWER(s.industry) LIKE LOWER(CONCAT('%', :industry, '%'))) AND " +
           "(:minRiskScore IS NULL OR s.overallRiskScore >= :minRiskScore) AND " +
           "(:maxRiskScore IS NULL OR s.overallRiskScore <= :maxRiskScore)")
    Page<Supplier> findByMultipleCriteria(
        @Param("status") Supplier.SupplierStatus status,
        @Param("tier") Supplier.SupplierTier tier,
        @Param("country") String country,
        @Param("industry") String industry,
        @Param("minRiskScore") Integer minRiskScore,
        @Param("maxRiskScore") Integer maxRiskScore,
        Pageable pageable
    );
    
    // Full-text search using search vector
    @Query(value = "SELECT * FROM suppliers WHERE search_vector @@ plainto_tsquery('english', :searchTerm)", 
           nativeQuery = true)
    List<Supplier> fullTextSearch(@Param("searchTerm") String searchTerm);
    
    // Get distinct countries
    @Query("SELECT DISTINCT s.country FROM Supplier s WHERE s.country IS NOT NULL ORDER BY s.country")
    List<String> findDistinctCountries();
    
    // Get distinct industries
    @Query("SELECT DISTINCT s.industry FROM Supplier s WHERE s.industry IS NOT NULL ORDER BY s.industry")
    List<String> findDistinctIndustries();
}