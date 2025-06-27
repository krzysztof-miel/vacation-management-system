package com.example.vacation_system.config;

import com.example.vacation_system.entity.Role;
import com.example.vacation_system.entity.User;
import com.example.vacation_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        // Sprawdź czy istnieje już administrator
        if (userRepository.countByRole(Role.ADMIN) == 0) {
            User admin = new User();
            admin.setFirstName("Administrator");
            admin.setLastName("Systemu");
            admin.setEmail("admin@company.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setTotalVacationDays(30); // Admin ma więcej dni urlopowych
            admin.setUsedVacationDays(0);
            admin.setActive(true);

            userRepository.save(admin);

            System.out.println("=== UTWORZONO DOMYŚLNEGO ADMINISTRATORA ===");
            System.out.println("Email: admin@company.com");
            System.out.println("Hasło: admin123");
            System.out.println("=========================================");
        } else {
            System.out.println("Administrator już istnieje w systemie.");
        }
    }
}
