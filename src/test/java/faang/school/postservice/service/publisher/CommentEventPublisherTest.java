package faang.school.postservice.service.publisher;

import faang.school.postservice.service.publisher.messagePublishers.CommentEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> template;
    private CommentEventPublisher publisher;
    private final String topicName = "topic";

    @BeforeEach
    void setUp() {
        publisher = new CommentEventPublisher(template, topicName);
    }

    @Test
    void testPublishEvent() {
        // given
        String jsonEvent = """ 
                {
                "id": 1,
                "authorId": 2,
                "postId": 3,
                "content": "content",
                }""";
        // when
        publisher.publish(jsonEvent);
        // then
        verify(template, times(1)).convertAndSend(topicName, jsonEvent);
    }
}