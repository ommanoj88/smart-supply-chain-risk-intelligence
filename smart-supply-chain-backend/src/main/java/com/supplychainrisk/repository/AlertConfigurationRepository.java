package com.supplychainrisk.repository;

import com.supplychainrisk.entity.AlertConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertConfigurationRepository extends JpaRepository<AlertConfiguration, Long> {
    
    // Find enabled configurations
    List<AlertConfiguration> findByEnabledTrue();
    
    // Find by alert type
    List<AlertConfiguration> findByAlertType(com.supplychainrisk.entity.Alert.AlertType alertType);
    
    // Find enabled configurations by alert type
    List<AlertConfiguration> findByAlertTypeAndEnabledTrue(com.supplychainrisk.entity.Alert.AlertType alertType);
    
    // Find by entity type
    List<AlertConfiguration> findByEntityType(String entityType);
    
    // Find configurations by name pattern
    @Query("SELECT ac FROM AlertConfiguration ac WHERE ac.name LIKE %:namePattern%")
    List<AlertConfiguration> findByNameContaining(String namePattern);
    
    // Find configurations created by specific user
    List<AlertConfiguration> findByCreatedBy(String createdBy);
}