package com.supplychain.risk.controller;

import com.supplychain.risk.dto.ApiResponse;
import com.supplychain.risk.dto.UserDto;
import com.supplychain.risk.enums.UserRole;
import com.supplychain.risk.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for user management operations.
 * 
 * This controller handles CRUD operations for users, role management,
 * and user statistics for the Smart Supply Chain Risk Intelligence platform.
 * Access is controlled by role-based security annotations.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "https://smart-supply-chain.netlify.app"})
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * Creates a new user. Only accessible by ADMIN role.
     * 
     * @param userDto the user data
     * @return ResponseEntity with created user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Creating new user: {}", userDto.getEmail());
        
        try {
            UserDto createdUser = userService.createUser(userDto);
            return ResponseEntity.ok(ApiResponse.created("User created successfully", createdUser));
            
        } catch (IllegalArgumentException e) {
            logger.warn("User creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest(e.getMessage()));
                    
        } catch (Exception e) {
            logger.error("Unexpected error creating user", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to create user"));
        }
    }
    
    /**
     * Retrieves all users with pagination. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortDir the sort direction (asc/desc)
     * @return ResponseEntity with paginated users
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        logger.debug("Retrieving users - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                    page, size, sortBy, sortDir);
        
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDir);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<UserDto> users = userService.getAllUsers(pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
            
        } catch (Exception e) {
            logger.error("Error retrieving users", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to retrieve users"));
        }
    }
    
    /**
     * Retrieves all active users. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @return ResponseEntity with active users
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllActiveUsers() {
        logger.debug("Retrieving all active users");
        
        try {
            List<UserDto> users = userService.getAllActiveUsers();
            return ResponseEntity.ok(ApiResponse.success("Active users retrieved successfully", users));
            
        } catch (Exception e) {
            logger.error("Error retrieving active users", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to retrieve active users"));
        }
    }
    
    /**
     * Retrieves a user by ID. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @param id the user ID
     * @return ResponseEntity with user details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        logger.debug("Retrieving user by ID: {}", id);
        
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
            
        } catch (Exception e) {
            logger.error("Error retrieving user by ID: {}", id, e);
            return ResponseEntity.status(404)
                    .body(ApiResponse.notFound("User not found"));
        }
    }
    
    /**
     * Updates a user. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * SUPPLY_MANAGER can only update certain fields.
     * 
     * @param id the user ID
     * @param userDto the updated user data
     * @return ResponseEntity with updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, 
                                                           @Valid @RequestBody UserDto userDto) {
        logger.info("Updating user with ID: {}", id);
        
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
            
        } catch (IllegalArgumentException e) {
            logger.warn("User update failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest(e.getMessage()));
                    
        } catch (Exception e) {
            logger.error("Error updating user with ID: {}", id, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to update user"));
        }
    }
    
    /**
     * Updates a user's role. Only accessible by ADMIN role.
     * 
     * @param id the user ID
     * @param role the new role
     * @return ResponseEntity with updated user
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUserRole(@PathVariable Long id, 
                                                               @RequestParam UserRole role) {
        logger.info("Updating role for user ID: {} to {}", id, role);
        
        try {
            UserDto updatedUser = userService.updateUserRole(id, role);
            return ResponseEntity.ok(ApiResponse.success("User role updated successfully", updatedUser));
            
        } catch (Exception e) {
            logger.error("Error updating user role for ID: {}", id, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to update user role"));
        }
    }
    
    /**
     * Updates a user's active status. Only accessible by ADMIN role.
     * 
     * @param id the user ID
     * @param active the new active status
     * @return ResponseEntity with updated user
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> updateUserStatus(@PathVariable Long id, 
                                                                 @RequestParam boolean active) {
        logger.info("Updating status for user ID: {} to {}", id, active ? "active" : "inactive");
        
        try {
            UserDto updatedUser = userService.updateUserStatus(id, active);
            return ResponseEntity.ok(ApiResponse.success("User status updated successfully", updatedUser));
            
        } catch (Exception e) {
            logger.error("Error updating user status for ID: {}", id, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to update user status"));
        }
    }
    
    /**
     * Deletes a user. Only accessible by ADMIN role.
     * 
     * @param id the user ID
     * @return ResponseEntity with deletion confirmation
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
            
        } catch (Exception e) {
            logger.error("Error deleting user with ID: {}", id, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to delete user"));
        }
    }
    
    /**
     * Retrieves users by role. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @param role the user role
     * @return ResponseEntity with users of the specified role
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersByRole(@PathVariable UserRole role) {
        logger.debug("Retrieving users by role: {}", role);
        
        try {
            List<UserDto> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
            
        } catch (Exception e) {
            logger.error("Error retrieving users by role: {}", role, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to retrieve users by role"));
        }
    }
    
    /**
     * Searches users by name. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @param name the search term for user names
     * @return ResponseEntity with matching users
     */
    @GetMapping("/search/name")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<List<UserDto>>> searchUsersByName(@RequestParam String name) {
        logger.debug("Searching users by name: {}", name);
        
        try {
            List<UserDto> users = userService.searchUsersByName(name);
            return ResponseEntity.ok(ApiResponse.success("Users found", users));
            
        } catch (Exception e) {
            logger.error("Error searching users by name: {}", name, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to search users"));
        }
    }
    
    /**
     * Searches users by email. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @param email the search term for user emails
     * @return ResponseEntity with matching users
     */
    @GetMapping("/search/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<List<UserDto>>> searchUsersByEmail(@RequestParam String email) {
        logger.debug("Searching users by email: {}", email);
        
        try {
            List<UserDto> users = userService.searchUsersByEmail(email);
            return ResponseEntity.ok(ApiResponse.success("Users found", users));
            
        } catch (Exception e) {
            logger.error("Error searching users by email: {}", email, e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to search users"));
        }
    }
    
    /**
     * Gets user statistics by role. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @return ResponseEntity with user statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<Map<UserRole, Long>>> getUserStatistics() {
        logger.debug("Retrieving user statistics");
        
        try {
            Map<UserRole, Long> statistics = userService.getUserStatisticsByRole();
            return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", statistics));
            
        } catch (Exception e) {
            logger.error("Error retrieving user statistics", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to retrieve statistics"));
        }
    }
    
    /**
     * Gets recently created users. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @param days the number of days to look back (default: 7)
     * @return ResponseEntity with recently created users
     */
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getRecentUsers(@RequestParam(defaultValue = "7") int days) {
        logger.debug("Retrieving users created in the last {} days", days);
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
            List<UserDto> users = userService.getUsersCreatedAfter(cutoffDate);
            return ResponseEntity.ok(ApiResponse.success("Recent users retrieved successfully", users));
            
        } catch (Exception e) {
            logger.error("Error retrieving recent users", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to retrieve recent users"));
        }
    }
    
    /**
     * Gets the count of active users. Accessible by ADMIN and SUPPLY_MANAGER roles.
     * 
     * @return ResponseEntity with active user count
     */
    @GetMapping("/count/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPLY_MANAGER')")
    public ResponseEntity<ApiResponse<Long>> getActiveUserCount() {
        logger.debug("Retrieving active user count");
        
        try {
            long count = userService.getActiveUserCount();
            return ResponseEntity.ok(ApiResponse.success("Active user count retrieved", count));
            
        } catch (Exception e) {
            logger.error("Error retrieving active user count", e);
            return ResponseEntity.status(500)
                    .body(ApiResponse.internalError("Failed to retrieve user count"));
        }
    }
}