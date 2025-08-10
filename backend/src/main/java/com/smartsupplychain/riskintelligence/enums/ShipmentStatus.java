package com.smartsupplychain.riskintelligence.enums;

public enum ShipmentStatus {
    PENDING("PENDING"),
    IN_TRANSIT("IN_TRANSIT"),
    DELAYED("DELAYED"),
    DELIVERED("DELIVERED"),
    CANCELLED("CANCELLED");

    private final String value;

    ShipmentStatus(String value) {
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