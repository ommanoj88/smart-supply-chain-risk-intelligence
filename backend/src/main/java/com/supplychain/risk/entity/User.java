package com.supplychain.risk.entity;

import com.supplychain.risk.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity representing a user in the Smart Supply Chain Risk Intelligence platform.
 * 
 * This entity stores user information including Firebase authentication details,
 * personal information, role-based access control, and audit timestamps.
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "firebase_uid"),
           @UniqueConstraint(columnNames = "email")
       },
       indexes = {
           @Index(name = "idx_firebase_uid", columnList = "firebase_uid"),
           @Index(name = "idx_email", columnList = "email"),
           @Index(name = "idx_role", columnList = "role")
       })
@EntityListeners(AuditingEntityListener.class)
public class User {
    
    /**
     * Primary key for the user entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Firebase UID for authentication integration.
     * This field links the user to their Firebase authentication record.
     */
    @Column(name = "firebase_uid", nullable = false, unique = true, length = 128)
    @NotBlank(message = "Firebase UID is required")
    private String firebaseUid;
    
    /**
     * User's email address (primary identifier).
     */
    @Column(name = "email", nullable = false, unique = true, length = 255)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    /**
     * User's full name.
     */
    @Column(name = "name", nullable = false, length = 255)
    @NotBlank(message = "Name is required")
    private String name;
    
    /**
     * User's role in the system for access control.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    @NotNull(message = "Role is required")
    private UserRole role = UserRole.VIEWER;
    
    /**
     * Indicates whether the user account is active.
     */
    @Column(name = "active", nullable = false)
    private boolean active = true;
    
    /**
     * Timestamp when the user was first created.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the user was last updated.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor for JPA.
     */
    public User() {}
    
    /**
     * Constructor for creating a new user with Firebase authentication.
     * 
     * @param firebaseUid the Firebase UID
     * @param email the user's email address
     * @param name the user's full name
     */
    public User(String firebaseUid, String email, String name) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.name = name;
        this.role = UserRole.VIEWER; // Default role
        this.active = true;
    }
    
    /**
     * Constructor for creating a new user with specified role.
     * 
     * @param firebaseUid the Firebase UID
     * @param email the user's email address
     * @param name the user's full name
     * @param role the user's role
     */
    public User(String firebaseUid, String email, String name, UserRole role) {
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.name = name;
        this.role = role;
        this.active = true;
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
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && 
               Objects.equals(firebaseUid, user.firebaseUid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, firebaseUid);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firebaseUid='" + firebaseUid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}