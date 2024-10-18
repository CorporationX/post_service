package faang.school.postservice.config.redis.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class PostCacheRedisConfig {
    @Bean
    public RedisTemplate<String, String> stringValueRedisTemplate(JedisConnectionFactory connectionFactory,
                                                                  ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, String.class, objectMapper);
    }

    @Bean
    public ZSetOperations<String, String> stringZSetOperations(RedisTemplate<String, String> stringValueRedisTemplate) {
        return stringValueRedisTemplate.opsForZSet();
    }

    @Bean
    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory connectionFactory,
                                                                         ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, PostCacheDto.class, objectMapper);
    }

    private <T> RedisTemplate<String, T> buildRedisTemplate(JedisConnectionFactory connectionFactory, Class<T> clazz,
                                                            ObjectMapper objectMapper) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, clazz);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        template.setEnableTransactionSupport(true);

        return template;
    }
}
