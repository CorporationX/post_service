package faang.school.postservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public class ApplicationContextTest {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    static {
        postgres.start();
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @Test
    void contextLoads() {

    }
}
