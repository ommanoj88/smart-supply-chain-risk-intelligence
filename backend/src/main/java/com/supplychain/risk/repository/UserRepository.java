package com.supplychain.risk.repository;

import com.supplychain.risk.entity.User;
import com.supplychain.risk.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * 
 * This repository provides data access methods for user management including
 * Firebase authentication integration, role-based queries, and audit operations.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Finds a user by their Firebase UID.
     * 
     * @param firebaseUid the Firebase UID to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByFirebaseUid(String firebaseUid);
    
    /**
     * Finds a user by their email address.
     * 
     * @param email the email address to search for
     * @return an Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Finds all active users.
     * 
     * @return a list of all active users
     */
    List<User> findByActiveTrue();
    
    /**
     * Finds all users with a specific role.
     * 
     * @param role the role to search for
     * @return a list of users with the specified role
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Finds all active users with a specific role.
     * 
     * @param role the role to search for
     * @return a list of active users with the specified role
     */
    List<User> findByRoleAndActiveTrue(UserRole role);
    
    /**
     * Checks if a user exists with the given Firebase UID.
     * 
     * @param firebaseUid the Firebase UID to check
     * @return true if a user exists with the given Firebase UID, false otherwise
     */
    boolean existsByFirebaseUid(String firebaseUid);
    
    /**
     * Checks if a user exists with the given email address.
     * 
     * @param email the email address to check
     * @return true if a user exists with the given email, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Counts the total number of active users.
     * 
     * @return the count of active users
     */
    long countByActiveTrue();
    
    /**
     * Counts the number of users with a specific role.
     * 
     * @param role the role to count
     * @return the count of users with the specified role
     */
    long countByRole(UserRole role);
    
    /**
     * Finds users created after a specific date.
     * 
     * @param date the date after which to find users
     * @return a list of users created after the specified date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Finds users updated after a specific date.
     * 
     * @param date the date after which to find users
     * @return a list of users updated after the specified date
     */
    List<User> findByUpdatedAtAfter(LocalDateTime date);
    
    /**
     * Custom query to find users by name containing a search term (case-insensitive).
     * 
     * @param searchTerm the term to search for in user names
     * @return a list of users whose names contain the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND u.active = true")
    List<User> findByNameContainingIgnoreCaseAndActiveTrue(@Param("searchTerm") String searchTerm);
    
    /**
     * Custom query to find users by email containing a search term (case-insensitive).
     * 
     * @param searchTerm the term to search for in user emails
     * @return a list of users whose emails contain the search term
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND u.active = true")
    List<User> findByEmailContainingIgnoreCaseAndActiveTrue(@Param("searchTerm") String searchTerm);
    
    /**
     * Custom query to get user statistics by role.
     * 
     * @return a list of objects containing role and count information
     */
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.active = true GROUP BY u.role")
    List<Object[]> getUserStatisticsByRole();
    
    /**
     * Finds all administrators in the system.
     * 
     * @return a list of users with ADMIN role
     */
    default List<User> findAdministrators() {
        return findByRoleAndActiveTrue(UserRole.ADMIN);
    }
    
    /**
     * Finds all supply managers in the system.
     * 
     * @return a list of users with SUPPLY_MANAGER role
     */
    default List<User> findSupplyManagers() {
        return findByRoleAndActiveTrue(UserRole.SUPPLY_MANAGER);
    }
    
    /**
     * Finds all viewers in the system.
     * 
     * @return a list of users with VIEWER role
     */
    default List<User> findViewers() {
        return findByRoleAndActiveTrue(UserRole.VIEWER);
    }
}