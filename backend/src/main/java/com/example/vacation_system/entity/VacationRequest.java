package com.example.vacation_system.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "vacation_requests")
public class VacationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Użytkownik jest wymagany")
    private User user;

    @NotNull(message = "Data rozpoczęcia jest wymagana")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "Data zakończenia jest wymagana")
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 500)
    private String reason; // Opcjonalny powód urlopu

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacationStatus status = VacationStatus.PENDING;

    @Column(length = 500)
    private String adminComment; // Komentarz administratora

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private User approvedBy; // Kto zatwierdził/odrzucił

    @Column
    private LocalDateTime approvedAt; // Kiedy zatwierdzono/odrzucono

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Konstruktory
    public VacationRequest() {}

    public VacationRequest(User user, LocalDate startDate, LocalDate endDate, String reason) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Metody pomocnicze
    public long getDaysCount() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public boolean isApproved() {
        return status == VacationStatus.APPROVED;
    }

    public boolean isPending() {
        return status == VacationStatus.PENDING;
    }

    public boolean isRejected() {
        return status == VacationStatus.REJECTED;
    }

    public boolean canBeModified() {
        return status == VacationStatus.PENDING;
    }

    // Gettery i settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

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

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
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
}
