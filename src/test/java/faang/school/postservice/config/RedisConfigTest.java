package faang.school.postservice.config;


import faang.school.postservice.service.messaging.HashtagListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RedisConfigTest {

    @Mock
    private HashtagListener hashtagListener;

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @InjectMocks
    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(redisConnectionFactory);
        assertNotNull(redisTemplate);
        assertEquals(redisConnectionFactory, redisTemplate.getConnectionFactory());
        assertTrue(redisTemplate.getKeySerializer() instanceof StringRedisSerializer);
        assertTrue(redisTemplate.getValueSerializer() instanceof StringRedisSerializer);
    }

    @Test
    void testHashtagListenerAdapter() {
        MessageListenerAdapter adapter = redisConfig.hashtagListenerAdapter(hashtagListener);
        assertNotNull(adapter);
        assertEquals(hashtagListener, adapter.getDelegate());
    }

    @Test
    void testPostViewTopic() {
        redisConfig.postViewTopic = "test_channel";
        ChannelTopic topic = redisConfig.postViewTopic();
        assertEquals("test_channel", topic.getTopic());
    }
}