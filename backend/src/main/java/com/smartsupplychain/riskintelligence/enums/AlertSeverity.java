package com.smartsupplychain.riskintelligence.enums;

public enum AlertSeverity {
    INFO("INFO"),
    WARNING("WARNING"),
    ERROR("ERROR"),
    CRITICAL("CRITICAL");

    private final String value;

    AlertSeverity(String value) {
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