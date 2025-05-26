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
        basePackages = "faang.school.postservice.redis_repository.post",
        redisTemplateRef = "postRedisTemplate")
public class PostRedisConfiguration extends RedisCacheTemplateBase<PostRedis> {

    public PostRedisConfiguration(RedisConfig redisConfig, ObjectMapper objectMapper) {
        super(redisConfig, objectMapper);
    }

    @Bean
    public RedisTemplate<String, PostRedis> postRedisTemplate(RedisConnectionFactory connectionFactory) {
        return getRedisTemplate(connectionFactory);
    }
}
