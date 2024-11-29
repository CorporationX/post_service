package faang.school.postservice.service.post;

import faang.school.postservice.publisher.kafka.KafkaEventProducer;
import faang.school.postservice.publisher.kafka.events.PostViewEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostViewCounterServiceTest {
    @Mock
    private KafkaEventProducer kafkaEventProducer;

    @InjectMocks
    private PostViewCounterService postViewCounterService;

    @Test
    void testIncrementViewCount() {
        postViewCounterService.incrementViewCount(1L);
        postViewCounterService.incrementViewCount(1L);
        Map<Long, Long> viewCounts = (Map<Long, Long>) ReflectionTestUtils.getField(postViewCounterService, "postViewCounts");
        assertNotNull(viewCounts);
        assertEquals(2L, viewCounts.get(1L));
    }

    @Test
    void testSendPostViewEvents_EmptyCounts() {
        postViewCounterService.sendPostViewEvents();
        verify(kafkaEventProducer, never()).sendEvent(any(PostViewEvent.class));
    }

    @Test
    void testSendPostViewEvents_WithCounts() {
        postViewCounterService.incrementViewCount(1L);
        postViewCounterService.incrementViewCount(2L);
        postViewCounterService.incrementViewCount(1L);

        postViewCounterService.sendPostViewEvents();

        ArgumentCaptor<PostViewEvent> captor = ArgumentCaptor.forClass(PostViewEvent.class);
        verify(kafkaEventProducer, times(2)).sendEvent(captor.capture());

        List<PostViewEvent> capturedEvents = captor.getAllValues();
        assertEquals(2, capturedEvents.size());

        Map<Long, Long> eventCounts = capturedEvents.stream()
                .collect(Collectors.toMap(PostViewEvent::getPostId, PostViewEvent::getViews));

        assertEquals(2L, eventCounts.get(1L));
        assertEquals(1L, eventCounts.get(2L));
    }

    @Test
    void testSendPostViewEvents_ClearAfterSend() {
        postViewCounterService.incrementViewCount(1L);
        postViewCounterService.sendPostViewEvents();
        Map<Long, Long> viewCounts = (Map<Long, Long>) ReflectionTestUtils.getField(postViewCounterService, "postViewCounts");
        assertNotNull(viewCounts);
        assertTrue(viewCounts.isEmpty());
    }
}