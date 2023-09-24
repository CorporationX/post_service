package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserBannerPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ObjectMapper objectMapper;
    private final String topic = "user-banner-channel";

    private UserBannerPublisher userBannerPublisher;

    @BeforeEach
    void setUp() {
        userBannerPublisher = new UserBannerPublisher(redisTemplate, objectMapper, topic);
    }

    @Test
    void testPublish() throws JsonProcessingException {
        Long authorId = 1L;

        when(objectMapper.writeValueAsString(authorId)).thenReturn("JSON_STRING");

        userBannerPublisher.publish(authorId);

        verify(redisTemplate).convertAndSend(topic, "JSON_STRING");
    }
}