package faang.school.postservice;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresContainerTest {

    private static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    static {
        postgresContainer.start();
    }
}

