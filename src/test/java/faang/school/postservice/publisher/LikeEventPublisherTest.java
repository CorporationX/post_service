package faang.school.postservice.publisher;

import faang.school.postservice.publisher.events.LikeEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeEventPublisherTest {
    @InjectMocks
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @Test
    void testPublisherLikeEvent() {
        Long postId = 1L;
        Long postAuthorId = 2L;
        Long likeAuthorId = 3L;
        LocalDateTime createdAt = LocalDateTime.now();
        LikeEvent likeEvent = LikeEvent.builder()
                .postId(postId)
                .postAuthorId(postAuthorId)
                .likeAuthorId(likeAuthorId)
                .createdAt(createdAt)
                .build();
        likeEventPublisher.setLikeEventsChannel("like_events_channel");

        likeEventPublisher.publishLikeEvent(postId, postAuthorId, likeAuthorId, createdAt);

        verify(redisMessagePublisher, times(1)).publish("like_events_channel", likeEvent);
    }
}