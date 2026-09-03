package com.college.complaint.config;

import com.college.complaint.entity.*;
import com.college.complaint.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintCategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Ensure Primary Admin User exists
        if (!userRepository.existsByEmail("eswarrawsr2006@gmail.com")) {
            User admin = new User(
                    "System Administrator",
                    "eswarrawsr2006@gmail.com",
                    passwordEncoder.encode("Eswar2310@"),
                    Role.ADMIN,
                    "System Administration",
                    "+91-9876543210"
            );
            userRepository.save(admin);
            logger.info("👑 Primary Admin initialized: eswarrawsr2006@gmail.com / Eswar2310@");
        }

        // Initialize Default Categories if none exist
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new ComplaintCategory("Electrical & Maintenance", "Issues related to wiring, lighting, fans, AC, and power backup"));
            categoryRepository.save(new ComplaintCategory("Plumbing & Sanitation", "Issues related to water supply, leaks, drainage, and restrooms"));
            categoryRepository.save(new ComplaintCategory("IT & Network Services", "Issues related to Wi-Fi connectivity, LAN ports, and laboratory PCs"));
            categoryRepository.save(new ComplaintCategory("Hostel & Infrastructure", "Issues related to furniture, room maintenance, locks, and doors"));
            categoryRepository.save(new ComplaintCategory("Academic & Classroom Facilities", "Issues related to projectors, smartboards, desks, and podiums"));
            logger.info("🏷️ Default complaint categories created successfully.");
        }
    }
}
