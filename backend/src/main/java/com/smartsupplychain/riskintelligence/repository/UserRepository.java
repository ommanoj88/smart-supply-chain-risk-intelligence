package com.smartsupplychain.riskintelligence.repository;

import com.smartsupplychain.riskintelligence.model.User;
import com.smartsupplychain.riskintelligence.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByFirebaseUid(String firebaseUid);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findByIsActiveTrue();
    boolean existsByFirebaseUid(String firebaseUid);
    boolean existsByEmail(String email);
}