package com.supplychain.risk.enums;

public enum UserRole {
    ADMIN("Admin"),
    SUPPLY_MANAGER("Supply Manager"),
    VIEWER("Viewer");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}