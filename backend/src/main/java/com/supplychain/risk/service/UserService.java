package com.supplychain.risk.service;

import com.supplychain.risk.dto.UserDto;
import com.supplychain.risk.entity.User;
import com.supplychain.risk.enums.UserRole;
import com.supplychain.risk.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for User entity operations.
 * 
 * This service provides business logic for user management operations including
 * CRUD operations, role management, user statistics, and profile management
 * for the Smart Supply Chain Risk Intelligence platform.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Creates a new user.
     * 
     * @param userDto the user data
     * @return the created user DTO
     * @throws IllegalArgumentException if user data is invalid
     */
    public UserDto createUser(UserDto userDto) {
        logger.debug("Creating new user: {}", userDto.getEmail());
        
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("User with email " + userDto.getEmail() + " already exists");
        }
        
        if (userDto.getFirebaseUid() != null && userRepository.existsByFirebaseUid(userDto.getFirebaseUid())) {
            throw new IllegalArgumentException("User with Firebase UID already exists");
        }
        
        User user = userDto.toEntity();
        user = userRepository.save(user);
        
        logger.info("Created new user: {} with ID: {}", user.getEmail(), user.getId());
        return UserDto.fromEntity(user);
    }
    
    /**
     * Retrieves all users with pagination.
     * 
     * @param pageable pagination information
     * @return page of user DTOs
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        logger.debug("Retrieving all users with pagination: {}", pageable);
        
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserDto::fromEntity);
    }
    
    /**
     * Retrieves all active users.
     * 
     * @return list of active user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllActiveUsers() {
        logger.debug("Retrieving all active users");
        
        List<User> users = userRepository.findByActiveTrue();
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves a user by ID.
     * 
     * @param id the user ID
     * @return the user DTO
     * @throws EntityNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        logger.debug("Retrieving user by ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        
        return UserDto.fromEntity(user);
    }
    
    /**
     * Retrieves a user by email.
     * 
     * @param email the user email
     * @return the user DTO
     * @throws EntityNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        logger.debug("Retrieving user by email: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        
        return UserDto.fromEntity(user);
    }
    
    /**
     * Retrieves a user by Firebase UID.
     * 
     * @param firebaseUid the Firebase UID
     * @return the user DTO
     * @throws EntityNotFoundException if user not found
     */
    @Transactional(readOnly = true)
    public UserDto getUserByFirebaseUid(String firebaseUid) {
        logger.debug("Retrieving user by Firebase UID: {}", firebaseUid);
        
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Firebase UID: " + firebaseUid));
        
        return UserDto.fromEntity(user);
    }
    
    /**
     * Updates an existing user.
     * 
     * @param id the user ID
     * @param userDto the updated user data
     * @return the updated user DTO
     * @throws EntityNotFoundException if user not found
     * @throws IllegalArgumentException if update data is invalid
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        logger.debug("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        
        // Check if email is being changed and if it's already taken
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new IllegalArgumentException("Email " + userDto.getEmail() + " is already taken");
            }
        }
        
        // Update fields
        userDto.updateEntity(existingUser);
        existingUser = userRepository.save(existingUser);
        
        logger.info("Updated user: {} with ID: {}", existingUser.getEmail(), existingUser.getId());
        return UserDto.fromEntity(existingUser);
    }
    
    /**
     * Updates a user's role.
     * 
     * @param id the user ID
     * @param role the new role
     * @return the updated user DTO
     * @throws EntityNotFoundException if user not found
     */
    public UserDto updateUserRole(Long id, UserRole role) {
        logger.debug("Updating role for user ID: {} to {}", id, role);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        
        UserRole oldRole = user.getRole();
        user.setRole(role);
        user = userRepository.save(user);
        
        logger.info("Updated user role: {} from {} to {}", user.getEmail(), oldRole, role);
        return UserDto.fromEntity(user);
    }
    
    /**
     * Activates or deactivates a user account.
     * 
     * @param id the user ID
     * @param active the active status
     * @return the updated user DTO
     * @throws EntityNotFoundException if user not found
     */
    public UserDto updateUserStatus(Long id, boolean active) {
        logger.debug("Updating status for user ID: {} to {}", id, active ? "active" : "inactive");
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        
        user.setActive(active);
        user = userRepository.save(user);
        
        logger.info("Updated user status: {} to {}", user.getEmail(), active ? "active" : "inactive");
        return UserDto.fromEntity(user);
    }
    
    /**
     * Deletes a user by ID.
     * 
     * @param id the user ID
     * @throws EntityNotFoundException if user not found
     */
    public void deleteUser(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        
        userRepository.delete(user);
        logger.info("Deleted user: {} with ID: {}", user.getEmail(), id);
    }
    
    /**
     * Retrieves users by role.
     * 
     * @param role the user role
     * @return list of user DTOs with the specified role
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(UserRole role) {
        logger.debug("Retrieving users by role: {}", role);
        
        List<User> users = userRepository.findByRoleAndActiveTrue(role);
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Searches users by name (case-insensitive).
     * 
     * @param searchTerm the search term
     * @return list of matching user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByName(String searchTerm) {
        logger.debug("Searching users by name: {}", searchTerm);
        
        List<User> users = userRepository.findByNameContainingIgnoreCaseAndActiveTrue(searchTerm);
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Searches users by email (case-insensitive).
     * 
     * @param searchTerm the search term
     * @return list of matching user DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> searchUsersByEmail(String searchTerm) {
        logger.debug("Searching users by email: {}", searchTerm);
        
        List<User> users = userRepository.findByEmailContainingIgnoreCaseAndActiveTrue(searchTerm);
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets user statistics by role.
     * 
     * @return map of role to user count
     */
    @Transactional(readOnly = true)
    public Map<UserRole, Long> getUserStatisticsByRole() {
        logger.debug("Retrieving user statistics by role");
        
        List<Object[]> stats = userRepository.getUserStatisticsByRole();
        return stats.stream()
                .collect(Collectors.toMap(
                    row -> (UserRole) row[0],
                    row -> (Long) row[1]
                ));
    }
    
    /**
     * Gets users created after a specific date.
     * 
     * @param date the date threshold
     * @return list of user DTOs created after the date
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUsersCreatedAfter(LocalDateTime date) {
        logger.debug("Retrieving users created after: {}", date);
        
        List<User> users = userRepository.findByCreatedAtAfter(date);
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the total count of active users.
     * 
     * @return the count of active users
     */
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        logger.debug("Getting active user count");
        
        return userRepository.countByActiveTrue();
    }
    
    /**
     * Checks if a user exists by email.
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Checks if a user exists by Firebase UID.
     * 
     * @param firebaseUid the Firebase UID to check
     * @return true if user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByFirebaseUid(String firebaseUid) {
        return userRepository.existsByFirebaseUid(firebaseUid);
    }
    
    /**
     * Gets or creates a user based on Firebase authentication data.
     * This method is used by the authentication filter.
     * 
     * @param firebaseUid the Firebase UID
     * @param email the user's email
     * @param name the user's name
     * @return the user DTO
     */
    public UserDto getOrCreateUserFromFirebase(String firebaseUid, String email, String name) {
        logger.debug("Getting or creating user from Firebase: {}", email);
        
        Optional<User> existingUser = userRepository.findByFirebaseUid(firebaseUid);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            
            // Update user information if it has changed
            boolean updated = false;
            if (email != null && !email.equals(user.getEmail())) {
                user.setEmail(email);
                updated = true;
            }
            if (name != null && !name.equals(user.getName())) {
                user.setName(name);
                updated = true;
            }
            
            if (updated) {
                user = userRepository.save(user);
                logger.debug("Updated existing user from Firebase: {}", email);
            }
            
            return UserDto.fromEntity(user);
        } else {
            // Create new user with default VIEWER role
            User newUser = new User();
            newUser.setFirebaseUid(firebaseUid);
            newUser.setEmail(email != null ? email : "unknown@example.com");
            newUser.setName(name != null ? name : "Unknown User");
            newUser.setRole(UserRole.VIEWER);
            newUser.setActive(true);
            
            newUser = userRepository.save(newUser);
            logger.info("Created new user from Firebase: {} with role: {}", email, UserRole.VIEWER);
            
            return UserDto.fromEntity(newUser);
        }
    }
}