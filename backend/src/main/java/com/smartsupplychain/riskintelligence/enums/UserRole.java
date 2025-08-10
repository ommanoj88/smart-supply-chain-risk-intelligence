package com.smartsupplychain.riskintelligence.enums;

public enum UserRole {
    ADMIN("ADMIN"),
    SUPPLY_MANAGER("SUPPLY_MANAGER"),
    VIEWER("VIEWER");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}