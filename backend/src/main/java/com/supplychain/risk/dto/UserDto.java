package com.supplychain.risk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.supplychain.risk.entity.User;
import com.supplychain.risk.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object for User entity.
 * 
 * This DTO is used for API requests and responses involving user data.
 * It provides a clean separation between the internal entity representation
 * and the external API interface.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    
    /**
     * User's unique identifier.
     */
    private Long id;
    
    /**
     * Firebase UID (excluded from JSON serialization for security).
     */
    private String firebaseUid;
    
    /**
     * User's email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * User's full name.
     */
    @NotBlank(message = "Name is required")
    private String name;
    
    /**
     * User's role in the system.
     */
    @NotNull(message = "Role is required")
    private UserRole role;
    
    /**
     * Indicates whether the user account is active.
     */
    private boolean active;
    
    /**
     * Role display name for UI purposes.
     */
    private String roleDisplayName;
    
    /**
     * Timestamp when the user was created.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user was last updated.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor.
     */
    public UserDto() {}
    
    /**
     * Constructor for creating a UserDto from request data.
     * 
     * @param email user's email
     * @param name user's name
     * @param role user's role
     */
    public UserDto(String email, String name, UserRole role) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.active = true;
        this.roleDisplayName = role != null ? role.getDisplayName() : null;
    }
    
    /**
     * Creates a UserDto from a User entity.
     * 
     * @param user the User entity to convert
     * @return a UserDto representation of the user
     */
    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        dto.setRoleDisplayName(user.getRole() != null ? user.getRole().getDisplayName() : null);
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Creates a UserDto from a User entity including Firebase UID.
     * This method should only be used for administrative purposes.
     * 
     * @param user the User entity to convert
     * @return a UserDto representation of the user with Firebase UID
     */
    public static UserDto fromEntityWithFirebaseUid(User user) {
        if (user == null) {
            return null;
        }
        
        UserDto dto = fromEntity(user);
        dto.setFirebaseUid(user.getFirebaseUid());
        
        return dto;
    }
    
    /**
     * Converts this DTO to a User entity.
     * Note: This creates a new entity and doesn't preserve ID or audit fields.
     * 
     * @return a User entity representation of this DTO
     */
    public User toEntity() {
        User user = new User();
        user.setFirebaseUid(this.firebaseUid);
        user.setEmail(this.email);
        user.setName(this.name);
        user.setRole(this.role);
        user.setActive(this.active);
        
        return user;
    }
    
    /**
     * Updates an existing User entity with values from this DTO.
     * Only updates modifiable fields (email, name, role, active status).
     * 
     * @param user the User entity to update
     */
    public void updateEntity(User user) {
        if (user != null) {
            if (this.email != null) {
                user.setEmail(this.email);
            }
            if (this.name != null) {
                user.setName(this.name);
            }
            if (this.role != null) {
                user.setRole(this.role);
            }
            user.setActive(this.active);
        }
    }
    
    /**
     * Checks if the user has administrative privileges.
     * 
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }
    
    /**
     * Checks if the user has management privileges.
     * 
     * @return true if the user can manage supply chain data, false otherwise
     */
    public boolean canManage() {
        return role != null && role.canManage();
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirebaseUid() {
        return firebaseUid;
    }
    
    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public UserRole getRole() {
        return role;
    }
    
    public void setRole(UserRole role) {
        this.role = role;
        this.roleDisplayName = role != null ? role.getDisplayName() : null;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getRoleDisplayName() {
        return roleDisplayName;
    }
    
    public void setRoleDisplayName(String roleDisplayName) {
        this.roleDisplayName = roleDisplayName;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) && 
               Objects.equals(email, userDto.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
    
    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", active=" + active +
                ", roleDisplayName='" + roleDisplayName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}