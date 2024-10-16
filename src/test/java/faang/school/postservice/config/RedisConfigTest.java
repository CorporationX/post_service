package faang.school.postservice.config;


import faang.school.postservice.listener.HashtagListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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
    private LettuceConnectionFactory lettuceConnectionFactory;

    @InjectMocks
    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = redisConfig.redisTemplate(lettuceConnectionFactory);
        assertNotNull(redisTemplate);
        assertEquals(lettuceConnectionFactory, redisTemplate.getConnectionFactory());
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
    void testViewProfileTopic() {
        redisConfig.postViewTopic = "test_channel";
        ChannelTopic topic = redisConfig.viewProfileTopic();
        assertEquals("test_channel", topic.getTopic());
    }
}