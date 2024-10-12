package faang.school.postservice.config.redis;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory connectionFactory) {
        return buildRedisTemplate(connectionFactory, Object.class);
    }

    @Bean
    public RedisTemplate<String, String> stringValueRedisTemplate(JedisConnectionFactory connectionFactory) {
        return buildRedisTemplate(connectionFactory, String.class);
    }

    @Bean
    public ZSetOperations<String, String> stringZSetOperations(RedisTemplate<String, String> stringValueRedisTemplate) {
        return stringValueRedisTemplate.opsForZSet();
    }

    @Bean
    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory connectionFactory) {
        return buildRedisTemplate(connectionFactory, PostCacheDto.class);
    }

    private <T> RedisTemplate<String, T> buildRedisTemplate(JedisConnectionFactory connectionFactory, Class<T> clazz) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(clazz);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        template.setEnableTransactionSupport(true);

        return template;
    }
}
