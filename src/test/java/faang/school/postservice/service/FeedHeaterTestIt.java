package faang.school.postservice.service;

import faang.school.postservice.dto.kafkaevents.FeedHeatEvent;
import faang.school.postservice.entity.User;
import faang.school.postservice.publisher.KafkaHeatFeedEventPublisher;
import faang.school.postservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("it")
@TestPropertySource(properties = "spring.liquibase.enabled=false")
public class FeedHeaterTestIt {

    @Autowired
    private FeedHeater feedHeater;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private KafkaHeatFeedEventPublisher publisher;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }



    @BeforeEach
    void setUp() {
        userRepository.saveAll(List.of(
                new User(1L), new User(2L), new User(3L)
        ));
    }

    @Test
    void testHeatFeedCache_PublishesEventsForAllUsers() throws InterruptedException {
        feedHeater.heatFeedCache();

        verify(publisher, times(3)).publish(any(FeedHeatEvent.class));
        verify(publisher).publish(argThat(event -> event.getId() == 1L));
        verify(publisher).publish(argThat(event -> event.getId() == 2L));
        verify(publisher).publish(argThat(event -> event.getId() == 3L));
    }
}
