package faang.school.postservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String REDIS_HOST;
    @Value("${spring.data.redis.port}")
    private int REDIS_PORT;
    @Value("${redis.topics.subscriber.user-ban-topic}")
    private String USER_BAN_TOPIC;
    @Value("${spring.data.redis.password}")
    private String REDIS_PASSWORD;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(REDIS_HOST);
        config.setPort(REDIS_PORT);
        if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
            config.setPassword(RedisPassword.of(REDIS_PASSWORD));
        }
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(USER_BAN_TOPIC);
    }
}