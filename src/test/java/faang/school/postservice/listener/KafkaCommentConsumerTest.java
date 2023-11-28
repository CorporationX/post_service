package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.CommentPostEvent;
import faang.school.postservice.dto.kafka.EventAction;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaCommentConsumerTest {

    @Mock
    private FeedService feedService;
    @InjectMocks
    private KafkaCommentConsumer kafkaCommentConsumer;

    private RedisCommentDto redisCommentDto;

    private final Long redisCommentDtoId = 1L;
    private final Long postId = 1L;
    private final Long authorId = 1L;
    private final String content = "Content";

    @BeforeEach
    void setUp() {
        redisCommentDto = RedisCommentDto.builder()
                .id(redisCommentDtoId)
                .authorId(authorId)
                .content(content)
                .build();
    }

    @Test
    void listenCommentEventCreateScenarioTest() {
        CommentPostEvent commentPostEvent = CommentPostEvent.builder()
                .postId(postId)
                .commentDto(redisCommentDto)
                .eventAction(EventAction.CREATE)
                .build();

        kafkaCommentConsumer.listenCommentEvent(commentPostEvent);

        verify(feedService).addCommentToPost(postId, redisCommentDto);
    }

    @Test
    void listenCommentEventUpdateScenarioTest() {
        redisCommentDto.setContent("Updated Content");

        CommentPostEvent commentPostEvent = CommentPostEvent.builder()
                .postId(postId)
                .commentDto(redisCommentDto)
                .eventAction(EventAction.UPDATE)
                .build();

        kafkaCommentConsumer.listenCommentEvent(commentPostEvent);

        verify(feedService).updateCommentInPost(postId, redisCommentDto);
    }

    @Test
    void listenCommentEventDeleteScenarioTest() {
        CommentPostEvent commentPostEvent = CommentPostEvent.builder()
                .postId(postId)
                .commentDto(RedisCommentDto.builder().id(redisCommentDtoId).build())
                .eventAction(EventAction.DELETE)
                .build();

        kafkaCommentConsumer.listenCommentEvent(commentPostEvent);

        verify(feedService).deleteCommentFromPost(postId, redisCommentDtoId);
    }
}