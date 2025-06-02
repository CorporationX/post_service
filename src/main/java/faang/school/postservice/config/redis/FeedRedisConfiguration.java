package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.FeedRedis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class FeedRedisConfiguration extends RedisCacheTemplateBase<FeedRedis> {

    public FeedRedisConfiguration(RedisConfig redisConfig, ObjectMapper objectMapper) {
        super(redisConfig, objectMapper);
    }

    @Bean("feedRedisTemplate")
    public RedisTemplate<String, FeedRedis> feedRedisTemplate(RedisConnectionFactory connectionFactory) {
        return getRedisTemplate(connectionFactory);
    }
}
