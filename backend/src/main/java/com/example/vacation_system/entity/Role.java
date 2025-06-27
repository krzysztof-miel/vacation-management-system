package com.example.vacation_system.entity;

public enum Role {
    ADMIN("Administrator"),
    EMPLOYEE("Pracownik");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
