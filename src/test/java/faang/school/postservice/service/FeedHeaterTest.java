package faang.school.postservice.service;

import faang.school.postservice.dto.kafkaevents.FeedHeatEvent;
import faang.school.postservice.publisher.KafkaHeatFeedEventPublisher;
import faang.school.postservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedHeaterTest {


    private FeedHeater feedHeater;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KafkaHeatFeedEventPublisher publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        feedHeater = new FeedHeater(userRepository, publisher, 5);

        when(userRepository.findAllUsersIds()).thenReturn(List.of(1L, 2L, 3L));
    }

    @Test
    void positiveHeatFeed() throws InterruptedException {
        feedHeater.heatFeedCache();

        verify(publisher, times(3)).publish(any(FeedHeatEvent.class));
    }
}
