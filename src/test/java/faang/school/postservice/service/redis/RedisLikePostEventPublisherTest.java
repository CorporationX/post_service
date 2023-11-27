package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.redis.LikeEventMapper;
import faang.school.postservice.mapper.redis.LikeEventMapperImpl;
import faang.school.postservice.model.Like;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisLikePostEventPublisherTest {

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ChannelTopic likeTopic;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    LikeEventMapper likeEventMapper;

    @BeforeEach
    void setUp() {
        likeEventMapper = new LikeEventMapperImpl();
        likeEventPublisher = new LikeEventPublisher(objectMapper, likeEventMapper, likeTopic, redisTemplate);
    }

    @Test
    void testPublishLikeEvent() throws JsonProcessingException {

        String message = "\"actorId\": 1,\n" +
                "    \"receiverId\": 1,\n" +
                "    \"receivedAt\": \"2023-08-17T12:34:56\"";
        Like like = Like.builder().id(0L).userId(1L).build();

        when(objectMapper.writeValueAsString(likeEventMapper.toDto(like))).thenReturn(message);
        likeEventPublisher.publish(like);
        verify(redisTemplate).convertAndSend(likeTopic.getTopic(), message);
    }
}