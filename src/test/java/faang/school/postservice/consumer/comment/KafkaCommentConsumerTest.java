package faang.school.postservice.consumer.comment;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.comment.CommentCreatedEvent;
import faang.school.postservice.repository.cache.post.PostCacheRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaCommentConsumerTest {

    @InjectMocks
    private KafkaCommentConsumer kafkaCommentConsumer;
    @Mock
    private PostCacheRepositoryImpl postCacheRepository;
    @Mock
    private Acknowledgment acknowledgment;
    @Mock
    private RedisProperties redisProperties;
    private CommentCreatedEvent commentCreatedEvent;

    @BeforeEach
    void setUp() {
        commentCreatedEvent = CommentCreatedEvent.builder()
                .commentId(25L)
                .postId(20L)
                .authorId(10L)
                .content("ABC")
                .createdAt(LocalDateTime.of(2024, 10, 10, 10, 10))
                .build();
    }

    @Test
    @DisplayName("When commentCreatedEvent caught then save newest comment on top of comments list in postCacheDto")
    public void whenCreatedEventListenedThenAddCommentOnPostCommentListOnTop() {
        CommentDto newComment = CommentDto.builder()
                .id(commentCreatedEvent.getCommentId())
                .authorId(commentCreatedEvent.getAuthorId())
                .content(commentCreatedEvent.getContent())
                .createdAt(commentCreatedEvent.getCreatedAt())
                .build();
        when(postCacheRepository.updatePostsComments(anyLong(), any()))
                .thenReturn(true);
        kafkaCommentConsumer.listenCommentEvent(commentCreatedEvent, acknowledgment);
    }
}
