package faang.school.postservice.cache.config.redis;

import faang.school.postservice.dto.post.PostViewDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Set;

/**
 * RedisConfig — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 07.08.2025
 */
@Configuration
public class RedisConfig {
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public RedisTemplate<String, List<PostViewDto>> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, List<PostViewDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }
}
