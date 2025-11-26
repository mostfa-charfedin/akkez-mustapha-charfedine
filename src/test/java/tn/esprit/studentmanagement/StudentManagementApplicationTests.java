package tn.esprit.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // Activate test profile
class StudentManagementApplicationTests {
    @Test
    void contextLoads() {
        // Default smoke test
    }
}
