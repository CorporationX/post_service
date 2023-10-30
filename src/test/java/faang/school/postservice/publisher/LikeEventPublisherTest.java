package faang.school.postservice.publisher;

import faang.school.postservice.messaging.redis.publisher.LikeEventPublisher;
import faang.school.postservice.messaging.redis.publisher.RedisMessagePublisher;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.messaging.redis.events.LikeEvent;
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
        Post post = Post.builder()
                .id(1L)
                .authorId(2L)
                .build();
        Like like = Like.builder()
                .userId(3L)
                .createdAt(LocalDateTime.now())
                .build();

        LikeEvent likeEvent = LikeEvent.builder()
                .postId(post.getId())
                .postAuthorId(post.getAuthorId())
                .likeAuthorId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();
        likeEventPublisher.setLikeEventsChannel("like_events_channel");

        likeEventPublisher.publishLikeEvent(like, post);

        verify(redisMessagePublisher, times(1)).publish("like_events_channel", likeEvent);
    }
}