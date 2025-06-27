package com.example.vacation_system.service;

import com.example.vacation_system.dto.CreateUserRequest;
import com.example.vacation_system.dto.UserDto;
import com.example.vacation_system.entity.Role;
import com.example.vacation_system.entity.User;
import com.example.vacation_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VacationCalculationService vacationCalculationService;

    /**
     * Pobiera wszystkich użytkowników
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Pobiera wszystkich aktywnych pracowników
     */
    public List<UserDto> getActiveEmployees() {
        return userRepository.findActiveEmployees().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Pobiera użytkownika po ID
     */
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDto);
    }

    /**
     * Pobiera użytkownika po email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Tworzy nowego użytkownika
     */
    public UserDto createUser(CreateUserRequest request) {
        // Sprawdź czy email już istnieje
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Użytkownik z tym emailem już istnieje");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setTotalVacationDays(request.getTotalVacationDays());
        user.setUsedVacationDays(0);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * Aktualizuje użytkownika
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Sprawdź czy email nie jest zajęty przez innego użytkownika
        if (!user.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Użytkownik z tym emailem już istnieje");
        }

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());
        user.setTotalVacationDays(userDto.getTotalVacationDays());
        user.setActive(userDto.getActive());

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * Aktywuje/dezaktywuje użytkownika
     */
    public UserDto toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setActive(!user.getActive());
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * Usuwa użytkownika (soft delete - dezaktywacja)
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new RuntimeException("Nie można usunąć ostatniego administratora");
            }
        }

        user.setActive(false);
        userRepository.save(user);
    }

    /**
     * Aktualizuje wykorzystane dni urlopowe użytkownika
     */
    public void updateUserVacationDays(User user) {
        vacationCalculationService.updateUsedVacationDays(user);
        userRepository.save(user);
    }

    /**
     * Sprawdza czy użytkownik ma wystarczająco dni urlopowych
     */
    public boolean hasEnoughVacationDays(User user, int requestedDays) {
        return vacationCalculationService.hasEnoughVacationDays(user, requestedDays);
    }

    /**
     * Konwertuje encję User na UserDto
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setTotalVacationDays(user.getTotalVacationDays());
        dto.setActive(user.getActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        // Oblicz aktualne wykorzystane dni
        Integer usedDays = vacationCalculationService.calculateUsedVacationDays(user);
        dto.setUsedVacationDays(usedDays);
        dto.setAvailableVacationDays(user.getTotalVacationDays() - usedDays);

        return dto;
    }
}