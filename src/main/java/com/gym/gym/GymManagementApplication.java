package com.gym.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

@SpringBootApplication
@EntityScan("com.gym.gym.model") // Scans for JPA entities
@ComponentScan(basePackages = {
    "com.gym.gym.controller",
    "com.gym.gym.service",
    "com.gym.gym.config",
    "com.gym.gym.repository",
    "com.gym.gym.exception",
    "com.gym.gym.dto"
}) // Scans for controllers, services, and components
@EnableJpaRepositories("com.gym.gym.repository") // Scans for JPA repositories
public class GymManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GymManagementApplication.class, args);
    }
}