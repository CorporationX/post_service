package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory,
                                                       ObjectMapper objectMapper) {
        return buildRedisTemplate(connectionFactory, Object.class, objectMapper);
    }

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

    private <T> RedisTemplate<String, T> buildRedisTemplate(JedisConnectionFactory connectionFactory,
                                                            Class<T> clazz,
                                                            ObjectMapper objectMapper) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure serializers
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jacksonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Set key and value serializers
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jacksonSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jacksonSerializer);

        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }
}
