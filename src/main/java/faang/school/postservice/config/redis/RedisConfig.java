package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.properties.cache.RedisClientProperties;
import faang.school.postservice.dto.cache.AuthorCacheDto;
import faang.school.postservice.dto.cache.PostCacheDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {

    private final RedisClientProperties clientProperties;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                clientProperties.host(), clientProperties.port());
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            JedisConnectionFactory jedisConnectionFactory,
            ObjectMapper mapper
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        configureRedisTemplate(template, mapper, Object.class);

        return template;
    }

    @Bean
    public RedisTemplate<String, PostCacheDto> redisPostDtoTemplate(
            JedisConnectionFactory jedisConnectionFactory,
            ObjectMapper mapper
    ) {
        RedisTemplate<String, PostCacheDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        configureRedisTemplate(template, mapper, PostCacheDto.class);

        return template;
    }

    @Bean
    public RedisTemplate<String, AuthorCacheDto> redisAuthorCacheDtoTemplate(
            JedisConnectionFactory jedisConnectionFactory,
            ObjectMapper mapper
    ) {
        RedisTemplate<String, AuthorCacheDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        configureRedisTemplate(template, mapper, AuthorCacheDto.class);

        return template;
    }

    private <T> void configureRedisTemplate(
            RedisTemplate<String, T> template,
            ObjectMapper mapper,
            Class<T> typeClass
    ) {
        Jackson2JsonRedisSerializer<T> jsonSerializer = new Jackson2JsonRedisSerializer<>(mapper, typeClass);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.setDefaultSerializer(jsonSerializer);
        template.afterPropertiesSet();
    }
}
