package com.backend.TTP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.backend.TTP.repository") // Add this line
public class TtpApplication {
    public static void main(String[] args) {
        SpringApplication.run(TtpApplication.class, args);
    }
}