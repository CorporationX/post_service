package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.CommentKafkaEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaCommentConsumerTest {

    private static final Long POST_ID = 1L;
    private static final Long AUTHOR_ID = 1L;
    private static final Long COMMENT_ID = 1L;
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2024, 7, 5, 12, 3, 25);
    private static final Long NEW_COMMENT_ID = 2L;
    private static final LocalDateTime NEW_UPDATED_AT = LocalDateTime.of(2024, 7, 6, 12, 3, 25);

    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private KafkaCommentConsumer kafkaCommentConsumer;

    PostRedis postRedis;
    CommentKafkaEvent commentKafkaEvent;
    CommentRedis commentRedis;

    @BeforeEach
    public void init() {
        postRedis = PostRedis.builder()
                .id(POST_ID)
                .build();
        commentKafkaEvent = CommentKafkaEvent.builder()
                .id(COMMENT_ID)
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .build();
        commentRedis = CommentRedis.builder()
                .id(COMMENT_ID)
                .updatedAt(UPDATED_AT)
                .build();
    }

    @Test
    public void listenCommentEventWhenPostInRedisAndNoComments() {
        when(redisPostRepository.findById(POST_ID)).thenReturn(Optional.ofNullable(postRedis));
        when(commentMapper.fromKafkaEventToRedis(commentKafkaEvent)).thenReturn(commentRedis);

        kafkaCommentConsumer.listenCommentEvent(commentKafkaEvent, acknowledgment);

        verify(redisPostRepository).save(postRedis);
        verify(acknowledgment).acknowledge();
    }

    @Test
    public void listenCommentEventWhenPostInRedisWithComments() {
        TreeSet<CommentRedis> comments = new TreeSet<>(Comparator.reverseOrder());
        comments.add(commentRedis);
        postRedis.setComments(comments);

        CommentRedis newCommentRedis = CommentRedis.builder()
                .id(NEW_COMMENT_ID)
                .updatedAt(NEW_UPDATED_AT)
                .build();

        commentKafkaEvent.setId(NEW_COMMENT_ID);

        when(redisPostRepository.findById(POST_ID)).thenReturn(Optional.ofNullable(postRedis));
        when(commentMapper.fromKafkaEventToRedis(commentKafkaEvent)).thenReturn(newCommentRedis);

        kafkaCommentConsumer.listenCommentEvent(commentKafkaEvent, acknowledgment);

        verify(redisPostRepository).save(postRedis);
        verify(acknowledgment).acknowledge();
    }

    @Test
    public void listenCommentEventWhenPostNotInRedis() {
        when(redisPostRepository.findById(POST_ID)).thenReturn(Optional.empty());

        kafkaCommentConsumer.listenCommentEvent(commentKafkaEvent, acknowledgment);

        verify(redisPostRepository, never()).save(postRedis);
        verify(acknowledgment).acknowledge();
    }

}