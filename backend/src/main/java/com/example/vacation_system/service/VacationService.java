package com.example.vacation_system.service;

import com.example.vacation_system.dto.VacationRequestDto;
import com.example.vacation_system.dto.VacationStatusRequest;
import com.example.vacation_system.entity.User;
import com.example.vacation_system.entity.VacationRequest;
import com.example.vacation_system.entity.VacationStatus;
import com.example.vacation_system.repository.VacationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VacationService {

    @Autowired
    private VacationRequestRepository vacationRequestRepository;

    @Autowired
    private VacationCalculationService vacationCalculationService;

    /**
     * Pobiera wszystkie wnioski urlopowe (dla administratora)
     */
    public List<VacationRequestDto> getAllVacationRequests() {
        return vacationRequestRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Pobiera wnioski urlopowe dla użytkownika
     */
    public List<VacationRequestDto> getUserVacationRequests(User user) {
        return vacationRequestRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Pobiera wnioski o określonym statusie
     */
    public List<VacationRequestDto> getVacationRequestsByStatus(VacationStatus status) {
        return vacationRequestRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Pobiera wniosek po ID
     */
    public Optional<VacationRequestDto> getVacationRequestById(Long id) {
        return vacationRequestRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Tworzy nowy wniosek urlopowy
     */
    public VacationRequestDto createVacationRequest(User user, VacationRequestDto requestDto) {
        // Walidacja dat
        validateVacationDates(requestDto.getStartDate(), requestDto.getEndDate());

        // Sprawdź czy użytkownik ma wystarczająco dni urlopowych
        long requestedDays = calculateDays(requestDto.getStartDate(), requestDto.getEndDate());
        if (!vacationCalculationService.hasEnoughVacationDays(user, (int) requestedDays)) {
            throw new RuntimeException("Niewystarczająca liczba dostępnych dni urlopowych");
        }

        // Sprawdź kolizje z już zatwierdzonymi urlopami
        if (hasVacationConflict(user, requestDto.getStartDate(), requestDto.getEndDate())) {
            throw new RuntimeException("Masz już zatwierdzony urlop w tym okresie");
        }

        VacationRequest vacationRequest = new VacationRequest();
        vacationRequest.setUser(user);
        vacationRequest.setStartDate(requestDto.getStartDate());
        vacationRequest.setEndDate(requestDto.getEndDate());
        vacationRequest.setReason(requestDto.getReason());
        vacationRequest.setStatus(VacationStatus.PENDING);

        VacationRequest savedRequest = vacationRequestRepository.save(vacationRequest);
        return convertToDto(savedRequest);
    }

    /**
     * Aktualizuje status wniosku urlopowego (zatwierdzenie/odrzucenie)
     */
    public VacationRequestDto updateVacationStatus(Long requestId, VacationStatusRequest statusRequest, User admin) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Wniosek urlopowy nie znaleziony"));

        if (vacationRequest.getStatus() != VacationStatus.PENDING) {
            throw new RuntimeException("Można zmieniać status tylko wniosków oczekujących");
        }

        vacationRequest.setStatus(statusRequest.getStatus());
        vacationRequest.setAdminComment(statusRequest.getAdminComment());
        vacationRequest.setApprovedBy(admin);
        vacationRequest.setApprovedAt(LocalDateTime.now());

        VacationRequest savedRequest = vacationRequestRepository.save(vacationRequest);

        // Jeśli wniosek został zatwierdzony, zaktualizuj wykorzystane dni użytkownika
        if (statusRequest.getStatus() == VacationStatus.APPROVED) {
            vacationCalculationService.updateUsedVacationDays(vacationRequest.getUser());
        }

        return convertToDto(savedRequest);
    }

    /**
     * Anuluje wniosek urlopowy (tylko przez twórcę i tylko pending)
     */
    public VacationRequestDto cancelVacationRequest(Long requestId, User user) {
        VacationRequest vacationRequest = vacationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Wniosek urlopowy nie znaleziony"));

        if (!vacationRequest.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Możesz anulować tylko własne wnioski");
        }

        if (vacationRequest.getStatus() != VacationStatus.PENDING) {
            throw new RuntimeException("Można anulować tylko wnioski oczekujące na zatwierdzenie");
        }

        vacationRequest.setStatus(VacationStatus.CANCELLED);
        VacationRequest savedRequest = vacationRequestRepository.save(vacationRequest);

        return convertToDto(savedRequest);
    }

    /**
     * Pobiera zatwierdzone urlopy w kalendarzu (zajęte dni)
     */
    public List<VacationRequestDto> getApprovedVacationsInDateRange(LocalDate startDate, LocalDate endDate) {
        return vacationRequestRepository.findApprovedVacationsInDateRange(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Pobiera zatwierdzone urlopy na konkretny dzień
     */
    public List<VacationRequestDto> getApprovedVacationsOnDate(LocalDate date) {
        return vacationRequestRepository.findApprovedVacationsOnDate(date).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Sprawdza czy jest kolizja z innymi urlopami
     */
    private boolean hasVacationConflict(User user, LocalDate startDate, LocalDate endDate) {
        List<VacationRequest> conflicts = vacationRequestRepository
                .findUserApprovedVacationsInDateRange(user, startDate, endDate);
        return !conflicts.isEmpty();
    }

    /**
     * Waliduje daty urlopu
     */
    private void validateVacationDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Daty rozpoczęcia i zakończenia są wymagane");
        }

        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Data rozpoczęcia nie może być późniejsza niż data zakończenia");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("Nie można składać wniosków na przeszłe daty");
        }
    }

    /**
     * Oblicza liczbę dni urlopu
     */
    private long calculateDays(LocalDate startDate, LocalDate endDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Konwertuje encję VacationRequest na VacationRequestDto
     */
    private VacationRequestDto convertToDto(VacationRequest vacation) {
        VacationRequestDto dto = new VacationRequestDto();
        dto.setId(vacation.getId());
        dto.setUserId(vacation.getUser().getId());
        dto.setUserFullName(vacation.getUser().getFullName());
        dto.setUserEmail(vacation.getUser().getEmail());
        dto.setStartDate(vacation.getStartDate());
        dto.setEndDate(vacation.getEndDate());
        dto.setReason(vacation.getReason());
        dto.setStatus(vacation.getStatus());
        dto.setAdminComment(vacation.getAdminComment());

        if (vacation.getApprovedBy() != null) {
            dto.setApprovedById(vacation.getApprovedBy().getId());
            dto.setApprovedByName(vacation.getApprovedBy().getFullName());
        }

        dto.setApprovedAt(vacation.getApprovedAt());
        dto.setCreatedAt(vacation.getCreatedAt());
        dto.setUpdatedAt(vacation.getUpdatedAt());
        dto.setDaysCount(vacation.getDaysCount());

        return dto;
    }
}