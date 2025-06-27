package com.example.vacation_system.dto;


import com.example.vacation_system.entity.Role;

public class JwtResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Integer totalVacationDays;
    private Integer usedVacationDays;
    private Integer availableVacationDays;

    // Konstruktory
    public JwtResponse() {}

    public JwtResponse(String token, String email, String firstName, String lastName,
                       Role role, Integer totalVacationDays, Integer usedVacationDays) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.totalVacationDays = totalVacationDays;
        this.usedVacationDays = usedVacationDays;
        this.availableVacationDays = totalVacationDays - usedVacationDays;
    }

    // Gettery i settery
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
