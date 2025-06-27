package com.example.vacation_system.controller;

import com.example.vacation_system.dto.CreateUserRequest;
import com.example.vacation_system.dto.UserDto;
import com.example.vacation_system.entity.User;
import com.example.vacation_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Pobiera wszystkich użytkowników (tylko admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Pobiera aktywnych pracowników (admin i pracownicy)
     */
    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<UserDto>> getActiveEmployees() {
        List<UserDto> employees = userService.getActiveEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * Pobiera dane zalogowanego użytkownika
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            UserDto userDto = userService.getUserById(user.getId())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Błąd pobierania danych użytkownika");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Pobiera użytkownika po ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Użytkownik nie znaleziony");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Tworzy nowego użytkownika (tylko admin)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserDto newUser = userService.createUser(request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Użytkownik został utworzony pomyślnie");
            response.put("user", newUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Błąd tworzenia użytkownika");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Aktualizuje użytkownika (tylko admin)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Użytkownik został zaktualizowany pomyślnie");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Błąd aktualizacji użytkownika");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Przełącza status aktywności użytkownika (tylko admin)
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        try {
            UserDto updatedUser = userService.toggleUserStatus(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", updatedUser.getActive() ?
                    "Użytkownik został aktywowany" : "Użytkownik został dezaktywowany");
            response.put("user", updatedUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Błąd zmiany statusu użytkownika");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Usuwa użytkownika (soft delete - tylko admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Użytkownik został usunięty pomyślnie");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Błąd usuwania użytkownika");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
