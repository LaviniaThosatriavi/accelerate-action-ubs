package com.backend.TTP;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Loads test-specific properties
public class TtpApplicationTests {
    @Test
    void contextLoads() {
    }
}
