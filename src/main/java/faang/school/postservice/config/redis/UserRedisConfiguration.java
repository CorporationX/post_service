package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.PostRedis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(
        basePackages = "faang.school.postservice.repository",
        redisTemplateRef = "userRedisTemplate"
)
public class UserRedisConfiguration extends RedisCacheTemplateBase<PostRedis> {

    public UserRedisConfiguration(RedisConfig redisConfig, ObjectMapper objectMapper) {
        super(redisConfig, objectMapper);
    }

    @Bean
    public RedisTemplate<String, PostRedis> userRedisTemplate(RedisConnectionFactory connectionFactory) {
        return getRedisTemplate(connectionFactory);
    }
}
