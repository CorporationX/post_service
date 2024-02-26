package faang.school.postservice.config;

import faang.school.postservice.dto.post.FeedHash;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Bean
    public RedisTemplate<String, FeedHash> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, FeedHash> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<FeedHash> serializer = new Jackson2JsonRedisSerializer<>(FeedHash.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());

        return template;
    }

    @Bean
    public RedisKeyValueTemplate redisKeyValueTemplate(RedisTemplate<String, FeedHash> redisTemplate) {
        RedisMappingContext mappingContext = new RedisMappingContext();
        RedisKeyValueAdapter adapter = new RedisKeyValueAdapter(redisTemplate, mappingContext);
        return new RedisKeyValueTemplate(adapter, mappingContext);
    }
}
