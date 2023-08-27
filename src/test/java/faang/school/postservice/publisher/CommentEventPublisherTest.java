package faang.school.postservice.publisher;

import faang.school.postservice.config.redis.RedisConfig;
import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.mapper.JsonObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private JsonObjectMapper objectMapper;

    @InjectMocks
    private CommentEventPublisher commentEventPublisher;

    @Value("${spring.data.redis.channels.comment_event_channel.name}")
    private String expectedTopicName;

    @Test
    void testPublish_SuccessfulPublishing() {
        CommentEventDto eventDto = CommentEventDto.builder()
                .postId(1L)
                .authorId(2L)
                .commentId(3L)
                .build();

        String json = "{\"postId\":1,\"authorId\":2,\"commentId\":3}";
        when(objectMapper.toJson(eventDto)).thenReturn(json);

        commentEventPublisher.publish(eventDto);

        verify(objectMapper).toJson(eventDto);
        verify(redisTemplate).convertAndSend(commentEventPublisher.getCommentEventTopicName(), json);
    }

    @Test
    void getRedisTemplate() {
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.setHost("localhost");
        redisConfig.setPort(6379);

        RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(redisConfig.redisConnectionFactory());

        assertNotNull(redisTemplate);
    }

    @Test
    void getObjectMapper() {
        JsonObjectMapper objectMapper = commentEventPublisher.getObjectMapper();
        assertNotNull(objectMapper);
    }

    @Test
    void getCommentEventTopicName() {
        String topicName = commentEventPublisher.getCommentEventTopicName();

        assertEquals(expectedTopicName, topicName);
    }

    @Test
    void setCommentEventTopicName() {
        commentEventPublisher.setCommentEventTopicName("new-test-channel");

        assertEquals("new-test-channel", commentEventPublisher.getCommentEventTopicName());
    }

    @Test
    void testEquals() {
        CommentEventPublisher otherCommentEventPublisher = new CommentEventPublisher(redisTemplate, objectMapper);

        assertEquals(commentEventPublisher, commentEventPublisher);
        assertEquals(commentEventPublisher, otherCommentEventPublisher);
        assertNotEquals(commentEventPublisher, null);
    }

    @Test
    void canEqual() {
        CommentEventPublisher otherCommentEventPublisher = new CommentEventPublisher(redisTemplate, objectMapper);

        assertTrue(commentEventPublisher.canEqual(commentEventPublisher));
        assertTrue(commentEventPublisher.canEqual(otherCommentEventPublisher));
        assertFalse(commentEventPublisher.canEqual(null));
    }

    @Test
    void testHashCode() {
        CommentEventPublisher otherCommentEventPublisher = new CommentEventPublisher(redisTemplate, objectMapper);

        int hashCode1 = commentEventPublisher.hashCode();
        int hashCode2 = otherCommentEventPublisher.hashCode();

        assertEquals(hashCode1, hashCode2);

    }

    @Test
    void testToString() {
        String toString = commentEventPublisher.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("CommentEventPublisher"));
    }
}