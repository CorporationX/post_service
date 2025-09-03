package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
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
    public RedisTemplate<String, CommentDto> redisCommentDtoTemplate(
            JedisConnectionFactory jedisConnectionFactory,
            ObjectMapper mapper
    ) {
        RedisTemplate<String, CommentDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        configureRedisTemplate(template, mapper, CommentDto.class);

        return template;
    }

    private <T> void configureRedisTemplate(
            RedisTemplate<String, T> template,
            ObjectMapper mapper,
            Class<T> tClass
    ) {
        Jackson2JsonRedisSerializer<T> jsonSerializer = new Jackson2JsonRedisSerializer<>(mapper, tClass);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.setDefaultSerializer(jsonSerializer);
    }
}
