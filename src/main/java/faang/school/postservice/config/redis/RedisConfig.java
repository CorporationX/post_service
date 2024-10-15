package faang.school.postservice.config.redis;

import faang.school.postservice.dto.post.PostHashtagDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, Object.class);
    }

    @Bean
    public RedisTemplate<String, PostHashtagDto> hashtagRedisTemplate(JedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(redisConnectionFactory, PostHashtagDto.class);
    }

    @Bean
    public RedisTemplate<String, Long> feedRedisTemplate(JedisConnectionFactory redisConnectionFactory){
        return createRedisTemplate(redisConnectionFactory, Long.class);
    }

    @Bean
    ChannelTopic likeTopic(@Value("${spring.data.redis.channels.like_post_channel.name}") String topicName) {
        return new ChannelTopic(topicName);
    }

    private <T> RedisTemplate<String, T> createRedisTemplate(RedisConnectionFactory redisConnectionFactory, Class<T> clazz) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}
