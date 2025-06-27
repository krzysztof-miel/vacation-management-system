package com.example.vacation_system.dto;

import com.example.vacation_system.entity.VacationStatus;
import jakarta.validation.constraints.NotNull;

public class VacationStatusRequest {

    @NotNull(message = "Status jest wymagany")
    private VacationStatus status;

    private String adminComment;

    // Konstruktory
    public VacationStatusRequest() {}

    public VacationStatusRequest(VacationStatus status, String adminComment) {
        this.status = status;
        this.adminComment = adminComment;
    }

    // Gettery i settery
    public VacationStatus getStatus() {
        return status;
    }

    public void setStatus(VacationStatus status) {
        this.status = status;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }
}