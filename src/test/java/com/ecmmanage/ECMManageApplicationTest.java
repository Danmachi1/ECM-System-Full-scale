package com.ecmmanage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = ECMManageApplication.class)
@ActiveProfiles("test") // ✅ Ensures we load application-test.properties
public class ECMManageApplicationTest {

    @Test
    void contextLoads() {
        assertTrue(true, "Context loads successfully.");
    }
}
