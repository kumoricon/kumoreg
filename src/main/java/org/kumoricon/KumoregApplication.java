package org.kumoricon;

import javafx.application.Application;
import org.kumoricon.model.role.Right;
import org.kumoricon.model.role.RightRepository;
import org.kumoricon.model.role.Role;
import org.kumoricon.model.role.RoleRepository;
import org.kumoricon.model.user.User;
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

    @Bean
    public CommandLineRunner loadUserData(UserRepository repository) {
        return (args) -> {
            // save a couple of users
            repository.save(new User("Jack", "Bauer"));
            repository.save(new User("Chloe", "O'Brian"));
            repository.save(new User("Kim", "Bauer"));
            repository.save(new User("David", "Palmer"));
            repository.save(new User("Michelle", "Dessler"));
            repository.save(new User("Greg", "Müller"));    // Name w/ umlaut

            // fetch all users
            log.info("users found with findAll():");
            log.info("-------------------------------");
            for (User user : repository.findAll()) {
                log.info(user.toString());
            }
            log.info("");

            // fetch an individual user by ID
            User user = repository.findOne(1);
            log.info("User found with findOne(1):");
            log.info("--------------------------------");
            log.info(user.toString());
            log.info("");

            // fetch users by last name
            log.info("user found with findByLastNameStartsWithIgnoreCase('Bauer'):");
            log.info("--------------------------------------------");
            for (User bauer : repository.findByLastNameStartsWithIgnoreCase("Bauer")) {
                log.info(bauer.toString());
            }
            log.info("");
        };
    }

    @Bean
    public CommandLineRunner loadRoleData(RoleRepository repository) {
        return (args) -> {
            // save some roles
            repository.save(new Role("Staff"));
            repository.save(new Role("Coordinator"));
            repository.save(new Role("Manager"));
            repository.save(new Role("Ops"));
            repository.save(new Role("Admin"));

            // fetch all users
            log.info("roles found with findAll():");
            log.info("-------------------------------");
            for (Role role : repository.findAll()) {
                log.info(role.toString());
            }
            log.info("");

            // fetch by ID
            Role role = repository.findOne(1);
            log.info("Role found with findOne(1):");
            log.info("--------------------------------");
            log.info(role.toString());
            log.info("");

            // fetch by name
            log.info("user found with findByNameStartsWidthIgnoreCase('Admin'):");
            log.info("--------------------------------------------");
            for (Role admin : repository.findByNameStartsWithIgnoreCase("Admin")) {
                log.info(admin.toString());
            }
            log.info("");
        };
    }

    @Bean
    public CommandLineRunner loadRightData(RightRepository repository) {
        return (args) -> {
            // save some roles
            repository.save(new Right("viewAttendee"));
            repository.save(new Right("editAttendee"));
            repository.save(new Right("search"));
            repository.save(new Right("import"));

            // fetch all users
            log.info("Rights found with findAll():");
            log.info("-------------------------------");
            for (Right right : repository.findAll()) {
                log.info(right.toString());
            }
            log.info("");

            // fetch by ID
            Right right = repository.findOne(1);
            log.info("Right found with findOne(1):");
            log.info("--------------------------------");
            log.info(right.toString());
            log.info("");
        };
    }



}
