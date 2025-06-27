package com.example.vacation_system.dto;

import com.example.vacation_system.entity.Role;
import jakarta.validation.constraints.*;

public class CreateUserRequest {

    @NotBlank(message = "Imię jest wymagane")
    @Size(min = 2, max = 50, message = "Imię musi mieć od 2 do 50 znaków")
    private String firstName;

    @NotBlank(message = "Nazwisko jest wymagane")
    @Size(min = 2, max = 50, message = "Nazwisko musi mieć od 2 do 50 znaków")
    private String lastName;

    @Email(message = "Nieprawidłowy format email")
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 6, message = "Hasło musi mieć co najmniej 6 znaków")
    private String password;

    @NotNull(message = "Rola jest wymagana")
    private Role role;

    @NotNull(message = "Liczba dni urlopowych jest wymagana")
    @Min(value = 0, message = "Liczba dni urlopowych nie może być ujemna")
    @Max(value = 365, message = "Liczba dni urlopowych nie może przekraczać 365")
    private Integer totalVacationDays = 26; // Domyślnie 26 dni

    // Konstruktory
    public CreateUserRequest() {}

    public CreateUserRequest(String firstName, String lastName, String email,
                             String password, Role role, Integer totalVacationDays) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.totalVacationDays = totalVacationDays;
    }

    // Gettery i settery
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}