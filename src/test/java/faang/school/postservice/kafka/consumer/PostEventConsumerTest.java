package faang.school.postservice.kafka.consumer;

import faang.school.postservice.cache.service.NewsFeedService;
import faang.school.postservice.cache.service.PostRedisService;
import faang.school.postservice.kafka.event.post.PostPublishedEvent;
import faang.school.postservice.kafka.event.post.PostViewedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostEventConsumerTest {
    @InjectMocks
    private PostEventConsumer postEventConsumer;
    @Mock
    private NewsFeedService newsFeedService;
    @Mock
    private PostRedisService postRedisService;
    @Mock
    private Acknowledgment acknowledgment;


    @Test
    void testConsumePostPublishedEvent() {
        Long postId = 1L;
        List<Long> followerIds = List.of(1L, 2L, 3L);
        PostPublishedEvent event = new PostPublishedEvent(postId, followerIds);

        postEventConsumer.consume(event, acknowledgment);

        verify(newsFeedService, times(followerIds.size())).addPostConcurrent(anyLong(), eq(postId));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumePostViewedEvent() {
        Long postId = 1L;
        Long views = 1000L;
        PostViewedEvent event = new PostViewedEvent(postId, views);

        postEventConsumer.consume(event, acknowledgment);

        verify(postRedisService, times(1)).updateViewsConcurrent(postId, views);
        verify(acknowledgment, times(1)).acknowledge();
    }
}