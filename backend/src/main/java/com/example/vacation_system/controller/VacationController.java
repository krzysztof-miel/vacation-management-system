package com.example.vacation_system.controller;

import com.example.vacation_system.dto.VacationRequestDto;
import com.example.vacation_system.dto.VacationStatusRequest;
import com.example.vacation_system.entity.User;
import com.example.vacation_system.entity.VacationStatus;
import com.example.vacation_system.service.UserService;
import com.example.vacation_system.service.VacationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vacations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class VacationController {

    @Autowired
    private VacationService vacationService;

    @Autowired
    private UserService userService;

    /**
     * Pobiera wszystkie wnioski urlopowe (admin) lub własne (employee)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<VacationRequestDto>> getVacationRequests(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        List<VacationRequestDto> vacations;

        // Admin widzi wszystkie wnioski, pracownik tylko swoje
        if (user.getRole().name().equals("ADMIN")) {
            vacations = vacationService.getAllVacationRequests();
        } else {
            vacations = vacationService.getUserVacationRequests(user);
        }

        return ResponseEntity.ok(vacations);
    }

    /**
     * Pobiera wnioski o określonym statusie (tylko admin)
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VacationRequestDto>> getVacationRequestsByStatus(@PathVariable VacationStatus status) {
        List<VacationRequestDto> vacations = vacationService.getVacationRequestsByStatus(status);
        return ResponseEntity.ok(vacations);
    }

    /**
     * Pobiera wniosek po ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<?> getVacationRequestById(@PathVariable Long id, Authentication authentication) {
        try {
            VacationRequestDto vacation = vacationService.getVacationRequestById(id)
                    .orElseThrow(() -> new RuntimeException("Wniosek urlopowy nie znaleziony"));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            // Sprawdź uprawnienia - admin może wszystko, pracownik tylko swoje wnioski
            if (!user.getRole().name().equals("ADMIN") && !vacation.getUserId().equals(user.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Brak uprawnień");
                error.put("message", "Możesz przeglądać tylko własne wnioski");
                return ResponseEntity.status(403).body(error);
            }

            return ResponseEntity.ok(vacation);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Wniosek nie znaleziony");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Tworzy nowy wniosek urlopowy
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<?> createVacationRequest(@Valid @RequestBody VacationRequestDto requestDto,
                                                   Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            VacationRequestDto newVacation = vacationService.createVacationRequest(user, requestDto);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Wniosek urlopowy został złożony pomyślnie");
            response.put("vacation", newVacation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Błąd składania wniosku");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Aktualizuje status wniosku urlopowego (tylko admin)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVacationStatus(@PathVariable Long id,
                                                  @Valid @RequestBody VacationStatusRequest statusRequest,
                                                  Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User admin = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Administrator nie znaleziony"));

            VacationRequestDto updatedVacation = vacationService.updateVacationStatus(id, statusRequest, admin);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Status wniosku został zaktualizowany pomyślnie");
            response.put("vacation", updatedVacation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Błąd aktualizacji statusu");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Anuluje wniosek urlopowy (tylko twórca wniosku)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<?> cancelVacationRequest(@PathVariable Long id, Authentication authentication) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.getUserByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            VacationRequestDto cancelledVacation = vacationService.cancelVacationRequest(id, user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Wniosek urlopowy został anulowany pomyślnie");
            response.put("vacation", cancelledVacation);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Błąd anulowania wniosku");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Pobiera kalendarz z zajętymi dniami (zatwierdzone urlopy)
     */
    @GetMapping("/calendar")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<VacationRequestDto>> getVacationCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<VacationRequestDto> approvedVacations = vacationService.getApprovedVacationsInDateRange(startDate, endDate);
        return ResponseEntity.ok(approvedVacations);
    }

    /**
     * Pobiera zatwierdzone urlopy na konkretny dzień
     */
    @GetMapping("/calendar/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<List<VacationRequestDto>> getVacationsOnDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<VacationRequestDto> vacationsOnDate = vacationService.getApprovedVacationsOnDate(date);
        return ResponseEntity.ok(vacationsOnDate);
    }
}