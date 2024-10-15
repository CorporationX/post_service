package faang.school.postservice.config.redis;

import faang.school.postservice.config.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisTemplate<String, Object> restTemplate() {
        RedisTemplate<String, Object> restTemplate = new RedisTemplate<>();

        restTemplate.setConnectionFactory(redisConnectionFactory());
        restTemplate.setKeySerializer(new StringRedisSerializer());
        restTemplate.setValueSerializer(new StringRedisSerializer());

        return restTemplate;
    }
}
