package com.example.vacation_system.dto;

import com.example.vacation_system.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public class UserDto {

    private Long id;

    @NotBlank(message = "Imię jest wymagane")
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane")
    private String lastName;

    @Email(message = "Nieprawidłowy format email")
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @NotNull(message = "Rola jest wymagana")
    private Role role;

    @NotNull(message = "Liczba dni urlopowych jest wymagana")
    @Min(value = 0, message = "Liczba dni urlopowych nie może być ujemna")
    private Integer totalVacationDays;

    private Integer usedVacationDays;
    private Integer availableVacationDays;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Konstruktory
    public UserDto() {}

    public UserDto(String firstName, String lastName, String email, Role role, Integer totalVacationDays) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.totalVacationDays = totalVacationDays;
    }

    // Gettery i settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Integer getTotalVacationDays() {
        return totalVacationDays;
    }

    public void setTotalVacationDays(Integer totalVacationDays) {
        this.totalVacationDays = totalVacationDays;
    }

    public Integer getUsedVacationDays() {
        return usedVacationDays;
    }

    public void setUsedVacationDays(Integer usedVacationDays) {
        this.usedVacationDays = usedVacationDays;
    }

    public Integer getAvailableVacationDays() {
        return availableVacationDays;
    }

    public void setAvailableVacationDays(Integer availableVacationDays) {
        this.availableVacationDays = availableVacationDays;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
