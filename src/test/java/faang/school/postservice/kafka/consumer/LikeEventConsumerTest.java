package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.event.like.LikeAddedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = LikeEventConsumer.class)
@ExtendWith(MockitoExtension.class)
class LikeEventConsumerTest {
    @Autowired
    private LikeEventConsumer likeEventConsumer;
    @MockBean
    private RedisTemplate<String, Object> redisTemplate;
    @MockBean
    private HashOperations<Object, Object, Object> hashOperations;
    @MockBean
    private Acknowledgment acknowledgment;

    @Value("${spring.data.redis.cache.post.prefix}")
    private String postPrefix;
    @Value("${spring.data.redis.cache.post.field.likes}")
    private String likesField;

    @Test
    void testConsumeLikeAddedEvent() {
        Long postId = 1L;
        LikeAddedEvent event = new LikeAddedEvent(postId);
        String key = postPrefix + postId;
        when(redisTemplate.hasKey(key)).thenReturn(true);
        doReturn(hashOperations).when(redisTemplate).opsForHash();
        when(hashOperations.increment(key, likesField, 1)).thenReturn(anyLong());

        likeEventConsumer.consume(event, acknowledgment);

        verify(redisTemplate, times(1)).opsForHash();
        verify(hashOperations, times(1)).increment(key, likesField, 1);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsumeLikeAddedEventWhenPostNotFound() {
        Long postId = 1L;
        LikeAddedEvent event = new LikeAddedEvent(postId);
        String key = postPrefix + postId;
        when(redisTemplate.hasKey(key)).thenReturn(false);

        likeEventConsumer.consume(event, acknowledgment);

        verify(redisTemplate, times(1)).hasKey(key);
        verify(redisTemplate, times(0)).opsForHash();
        verify(acknowledgment, times(1)).acknowledge();
    }
}