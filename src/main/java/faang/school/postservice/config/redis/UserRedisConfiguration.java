package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.UserRedis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(
        basePackages = "faang.school.postservice.redis_repository.user",
        redisTemplateRef = "userRedisTemplate")
public class UserRedisConfiguration extends RedisCacheTemplateBase<UserRedis> {

    public UserRedisConfiguration(RedisConfig redisConfig, ObjectMapper objectMapper) {
        super(redisConfig, objectMapper);
    }

    @Bean
    public RedisTemplate<String, UserRedis> userRedisTemplate(RedisConnectionFactory connectionFactory) {
        return getRedisTemplate(connectionFactory);
    }
}
