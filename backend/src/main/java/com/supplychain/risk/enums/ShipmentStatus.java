package com.supplychain.risk.enums;

public enum ShipmentStatus {
    PENDING("Pending"),
    IN_TRANSIT("In Transit"),
    DELAYED("Delayed"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    LOST("Lost");

    private final String displayName;

    ShipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}