package com.example.vacation_system.service;


import com.example.vacation_system.entity.User;
import com.example.vacation_system.entity.VacationRequest;
import com.example.vacation_system.entity.VacationStatus;
import com.example.vacation_system.repository.VacationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class VacationCalculationService {

    @Autowired
    private VacationRequestRepository vacationRequestRepository;

    /**
     * Oblicza liczbę wykorzystanych dni urlopowych dla użytkownika
     */
    public Integer calculateUsedVacationDays(User user) {
        List<VacationRequest> approvedVacations = vacationRequestRepository
                .findByUserAndStatus(user, VacationStatus.APPROVED);

        return approvedVacations.stream()
                .mapToInt(vacation -> (int) vacation.getDaysCount())
                .sum();
    }

    /**
     * Oblicza liczbę dostępnych dni urlopowych dla użytkownika
     */
    public Integer calculateAvailableVacationDays(User user) {
        Integer used = calculateUsedVacationDays(user);
        return user.getTotalVacationDays() - used;
    }

    /**
     * Sprawdza czy użytkownik ma wystarczająco dni urlopowych
     */
    public boolean hasEnoughVacationDays(User user, int requestedDays) {
        Integer available = calculateAvailableVacationDays(user);
        return available >= requestedDays;
    }

    /**
     * Aktualizuje wykorzystane dni urlopowe użytkownika w bazie danych
     */
    public void updateUsedVacationDays(User user) {
        Integer usedDays = calculateUsedVacationDays(user);
        user.setUsedVacationDays(usedDays);
    }
}
