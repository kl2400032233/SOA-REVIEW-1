package com.klu.springmvc.config;

import com.klu.springmvc.model.Role;
import com.klu.springmvc.model.User;
import com.klu.springmvc.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                logger.info("Database is empty. Initializing development seed users for academic review...");

                User customer = new User();
                customer.setName("John Customer");
                customer.setEmail("customer@example.com");
                customer.setPassword(passwordEncoder.encode("Customer@123"));
                customer.setRole(Role.CUSTOMER);
                userRepository.save(customer);

                User admin = new User();
                admin.setName("Alice Admin");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);

                User staff = new User();
                staff.setName("Bob Staff");
                staff.setEmail("staff@example.com");
                staff.setPassword(passwordEncoder.encode("Staff@123"));
                staff.setRole(Role.STAFF);
                userRepository.save(staff);

                logger.info("Seed users created successfully: customer@example.com, admin@example.com, staff@example.com");
            }
        };
    }
}
