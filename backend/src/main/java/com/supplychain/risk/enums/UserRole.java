package com.supplychain.risk.enums;

/**
 * Enumeration of user roles in the Smart Supply Chain Risk Intelligence platform.
 * 
 * This enum defines the three levels of access control:
 * - ADMIN: Full system access with user management capabilities
 * - SUPPLY_MANAGER: Can manage supply chain data and view analytics
 * - VIEWER: Read-only access to approved supply chain information
 * 
 * @author Supply Chain Risk Intelligence Team
 * @version 1.0
 * @since 2024-01-01
 */
public enum UserRole {
    
    /**
     * Administrator role with full system access.
     * Can manage users, configure system settings, and access all data.
     */
    ADMIN("Administrator"),
    
    /**
     * Supply Manager role with data management capabilities.
     * Can add, modify, and analyze supply chain data and risk assessments.
     */
    SUPPLY_MANAGER("Supply Manager"),
    
    /**
     * Viewer role with read-only access.
     * Can view approved supply chain information and basic analytics.
     */
    VIEWER("Viewer");
    
    private final String displayName;
    
    /**
     * Constructor for UserRole enum.
     * 
     * @param displayName the human-readable display name for the role
     */
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the human-readable display name for the role.
     * 
     * @return the display name of the role
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if the role has administrative privileges.
     * 
     * @return true if the role is ADMIN, false otherwise
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Checks if the role has management privileges.
     * 
     * @return true if the role is ADMIN or SUPPLY_MANAGER, false otherwise
     */
    public boolean canManage() {
        return this == ADMIN || this == SUPPLY_MANAGER;
    }
    
    /**
     * Gets the Spring Security authority name for this role.
     * 
     * @return the authority name prefixed with "ROLE_"
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}