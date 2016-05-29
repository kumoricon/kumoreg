package org.kumoricon;

import javafx.application.Application;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.RightRepository;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
import org.kumoricon.model.user.UserFactory;
import org.kumoricon.model.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KumoregApplication {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(KumoregApplication.class, args);
    }


    @Bean CommandLineRunner loadDefaultRole(RoleRepository roleRepository, RightRepository rightRepository, UserRepository userRepository) {
        return (args) -> {
            // If there are no roles defined, create the "Admin" role with global rights
            Right adminRight = rightRepository.findByNameIgnoreCase("Admin");
            if (adminRight == null && rightRepository.findAll().size() == 0) {
                log.info("No rights found, creating 'admin' right");
                adminRight = new Right("super_admin", "Override - can do everything");
                adminRight = rightRepository.save(adminRight);
                if (roleRepository.findAll().size() == 0) {
                    log.info("Creating admin role");
                    Role adminRole = new Role("Admin");
                    adminRole.addRight(adminRight);
                    adminRole = roleRepository.save(adminRole);
                }
            }

            // If there are no users defined, create a user with the Admin role.
            if (userRepository.findAll().size() == 0) {
                log.info("No users found. Creating default user 'admin' with password 'password'");
                User defaultUser = UserFactory.newUser("Admin", "User");
                Role adminRole = roleRepository.findByNameIgnoreCase("Admin");
                defaultUser.setUsername("admin");
                defaultUser.setPassword("password");
                defaultUser.setRole(adminRole);
                defaultUser = userRepository.save(defaultUser);
            }

        };
    }
}
