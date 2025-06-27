package com.example.vacation_system.entity;


public enum VacationStatus {
    PENDING("Oczekuje na zatwierdzenie"),
    APPROVED("Zatwierdzony"),
    REJECTED("Odrzucony"),
    CANCELLED("Anulowany");

    private final String displayName;

    VacationStatus(String displayName) {
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
