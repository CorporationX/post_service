package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.PostEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private final String topic = "post-channel";
    private PostEventPublisher postEventPublisher;
    @BeforeEach
    void setUp() {
        postEventPublisher = new PostEventPublisher(redisTemplate, objectMapper, topic);
    }

    @Test
    void testPublish() throws JsonProcessingException {
        PostEventDto postEventDto = PostEventDto.builder()
                .postId(1L)
                .authorId(1L)
                .build();

        when(objectMapper.writeValueAsString(postEventDto)).thenReturn("JSON_STRING");

        postEventPublisher.publish(postEventDto);

        verify(redisTemplate).convertAndSend(topic, "JSON_STRING");
    }
}