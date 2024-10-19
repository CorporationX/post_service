package faang.school.postservice.kafka.consumer;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.service.PostRedisService;
import faang.school.postservice.kafka.event.comment.CommentAddedEvent;
import faang.school.postservice.mapper.CommentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventConsumerTest {
    @InjectMocks
    private CommentEventConsumer commentEventConsumer;
    @Mock
    private PostRedisService postRedisService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void testCommentAddedEvent() {
        CommentRedis commentRedis = CommentRedis.builder()
                .id(2L)
                .content("content")
                .author(UserRedis.builder().id(12L).build())
                .postId(24L)
                .build();
        CommentAddedEvent event = CommentAddedEvent.builder()
                .commentId(commentRedis.getId())
                .content(commentRedis.getContent())
                .authorId(commentRedis.getAuthor().getId())
                .postId(commentRedis.getPostId())
                .build();
        when(commentMapper.toRedis(event)).thenReturn(commentRedis);

        commentEventConsumer.consume(event, acknowledgment);

        verify(postRedisService, times(1)).addCommentConcurrent(commentRedis);
        verify(acknowledgment).acknowledge();
    }
}