package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaTopicResolver;
import faang.school.postservice.publisher.kafka.events.FeedHeatEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedHeatServiceTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private KafkaTopicResolver kafkaTopicResolver;
    @InjectMocks
    private FeedHeatService feedHeatService;
    @Captor
    private ArgumentCaptor<FeedHeatEvent> eventCaptor;
    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(feedHeatService, "batchSize", 2);
    }

    @Test
    public void testStartCacheHeat() {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        when(userServiceClient.getAllUserIds()).thenReturn(userIds);

        when(kafkaTopicResolver.resolveTopic(any(FeedHeatEvent.class))).thenReturn("test-topic");

        feedHeatService.startCacheHeat();

        verify(userServiceClient, times(1)).getAllUserIds();

        verify(kafkaTemplate, times(3)).send(topicCaptor.capture(), eventCaptor.capture());

        List<FeedHeatEvent> capturedEvents = eventCaptor.getAllValues();
        List<String> capturedTopics = topicCaptor.getAllValues();

        capturedTopics.forEach(topic -> assertEquals("test-topic", topic));

        assertEquals(3, capturedEvents.size());

        FeedHeatEvent event1 = capturedEvents.get(0);
        FeedHeatEvent event2 = capturedEvents.get(1);
        FeedHeatEvent event3 = capturedEvents.get(2);

        assertEquals(Arrays.asList(1L, 2L), event1.getUserIds());
        assertEquals(Arrays.asList(3L, 4L), event2.getUserIds());
        assertEquals(List.of(5L), event3.getUserIds());
    }

    @Test
    public void testSplitListIntoBatches() {
        List<Long> userIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        List<List<Long>> batches = ReflectionTestUtils.invokeMethod(
                feedHeatService, "splitListIntoBatches", userIds, 2);

        assertNotNull(batches);
        assertEquals(3, batches.size());
        assertEquals(Arrays.asList(1L, 2L), batches.get(0));
        assertEquals(Arrays.asList(3L, 4L), batches.get(1));
        assertEquals(List.of(5L), batches.get(2));
    }
}