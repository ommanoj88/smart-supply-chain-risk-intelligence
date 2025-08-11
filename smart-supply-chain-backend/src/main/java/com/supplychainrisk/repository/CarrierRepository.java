package com.supplychainrisk.repository;

import com.supplychainrisk.entity.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarrierRepository extends JpaRepository<Carrier, Long> {

    Optional<Carrier> findByCode(String code);

    Optional<Carrier> findByName(String name);

    List<Carrier> findByIsActiveTrue();

    @Query("SELECT c FROM Carrier c WHERE c.isActive = true AND :service MEMBER OF c.servicesOffered")
    List<Carrier> findByServiceOffered(@Param("service") String service);

    @Query("SELECT c FROM Carrier c WHERE c.isActive = true AND :country MEMBER OF c.countriesSupported")
    List<Carrier> findByCountrySupported(@Param("country") String country);

    @Query("SELECT c FROM Carrier c WHERE c.isActive = true AND " +
           ":service MEMBER OF c.servicesOffered AND " +
           ":country MEMBER OF c.countriesSupported")
    List<Carrier> findByServiceAndCountry(@Param("service") String service, @Param("country") String country);

    @Query("SELECT c FROM Carrier c WHERE c.reliabilityScore >= :minScore ORDER BY c.reliabilityScore DESC")
    List<Carrier> findByMinReliabilityScore(@Param("minScore") Integer minScore);

    @Query("SELECT c FROM Carrier c ORDER BY c.onTimePercentage DESC")
    List<Carrier> findAllOrderByOnTimePerformance();
}