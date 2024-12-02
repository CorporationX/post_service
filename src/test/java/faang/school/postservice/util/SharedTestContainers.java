package faang.school.postservice.util;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class SharedTestContainers {

    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3"))
            .withDatabaseName("testdb")
            .withUsername("admin")
            .withPassword("admin")
            .withInitScript("schema_for_feed-controller.sql")
            .withCreateContainerCmdModifier(cmd -> cmd.withName("feed_containers"));

    public static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"))
            .withExposedPorts(9093);

    static {
        POSTGRES_CONTAINER.start();
        KAFKA_CONTAINER.start();
        REDIS_CONTAINER.start();
    }
}
