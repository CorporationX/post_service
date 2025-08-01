package faang.school.postservice.config.redis.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisFeedConfiguration {

    private final RedisFeedProperties properties;

    @Bean
    public RedisConnectionFactory redisNewsFeedConnectionFactory() {
        return new LettuceConnectionFactory(properties.getHost(), properties.getPort());
    }

    @Bean("redisNewsFeedTemplate")
    public RedisTemplate<String, Object> redisNewsFeedTemplate(RedisConnectionFactory redisNewsFeedConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisNewsFeedConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}