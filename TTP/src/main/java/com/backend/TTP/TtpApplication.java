package com.backend.TTP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; 

@SpringBootApplication
@EnableScheduling
public class TtpApplication {
    public static void main(String[] args) {
        SpringApplication.run(TtpApplication.class, args);
    }
}