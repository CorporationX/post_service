package faang.school.postservice.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TestContainersConfig {

    public static final String POSTGRES_IMAGE = "postgres:13.3";
    public static final String MINIO_IMAGE = "minio/minio:latest";
    public static final String MINIO_USER = "user";
    public static final String MINIO_PASSWORD = "password";
    public static final String MINIO_COMMAND = "server /data";
    private static final String BUCKET = "corpbucket";

    public static final int POSTGRES_PORT = 5433;
    public static final int MINIO_PORT = 9000;

    @Container
    public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("postgres")
            .withUsername("user")
            .withPassword("password");

    @Container
    public static final GenericContainer<?> MINIO = new GenericContainer<>(MINIO_IMAGE)
            .withEnv("MINIO_ROOT_USER", MINIO_USER)
            .withEnv("MINIO_ROOT_PASSWORD", MINIO_PASSWORD)
            .withCommand(MINIO_COMMAND)
            .withExposedPorts(MINIO_PORT);

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        System.out.printf("Postgres: %s%n", POSTGRES.getJdbcUrl());
        System.out.printf("MinIO: %s%n", getMinioUrl());

        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("spring.liquibase.url", POSTGRES::getJdbcUrl);
        registry.add("spring.liquibase.user", POSTGRES::getUsername);
        registry.add("spring.liquibase.password", POSTGRES::getPassword);

        registry.add("cloud.aws.s3.endpoint", () -> {
            String url = "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(MINIO_PORT);
            System.out.println("MINIO: " + url);
            return url;
        });

        registry.add("cloud.aws.s3.path-style-access", () -> "true");
        registry.add("cloud.aws.stack.auto", () -> "false");

        registry.add("cloud.aws.s3.access-key", () -> MINIO_USER);
        registry.add("cloud.aws.s3.secret-key", () -> MINIO_PASSWORD);
        registry.add("cloud.aws.s3.bucket", () -> BUCKET);
    }

    private static String getMinioUrl() {
        return "http://" + MINIO.getHost() + ":" + MINIO.getMappedPort(MINIO_PORT);
    }
}
