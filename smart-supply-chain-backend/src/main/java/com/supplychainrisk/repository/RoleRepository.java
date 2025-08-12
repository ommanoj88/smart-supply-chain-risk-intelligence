package com.supplychainrisk.repository;

import com.supplychainrisk.entity.SystemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<SystemRole, Long> {
    
    Optional<SystemRole> findByName(String name);
    
    boolean existsByName(String name);
}