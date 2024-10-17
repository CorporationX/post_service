package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class RedisConfigTemplate {

    @Bean
    public RedisTemplate<String, Object> redisTemplateConfig(RedisConnectionFactory connection) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        var serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setConnectionFactory(connection);
        redisTemplate.setValueSerializer(serializer);
        return redisTemplate;
    }
}
