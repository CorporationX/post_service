package faang.school.postservice.publisher;

import faang.school.postservice.messaging.redis.publisher.CommentEventPublisher;
import faang.school.postservice.messaging.redis.publisher.RedisMessagePublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.messaging.redis.events.CommentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @InjectMocks
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @Test
    void testPublisherCommentEvent() {
        Comment comment = Comment.builder()
                .authorId(1L)
                .post(Post.builder()
                        .id(2L)
                        .build())
                .id(3L)
                .content("test")
                .build();
        CommentEvent commentEvent = CommentEvent.builder()
                .authorId(1L)
                .postId(2L)
                .commentId(3L)
                .commentText("test")
                .build();
        commentEventPublisher.setCommentEventsChannel("channel");

        commentEventPublisher.publishCommentEvent(comment);

        verify(redisMessagePublisher, times(1)).publish("channel", commentEvent);
    }
}
