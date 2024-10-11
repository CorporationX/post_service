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
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate(JedisConnectionFactory connectionFactory,
                                                             StringRedisSerializer stringRedisSerializer) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        return buildRedisTemplate(connectionFactory, stringRedisSerializer, serializer);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory connectionFactory,
                                                       StringRedisSerializer stringRedisSerializer) {
        Jackson2JsonRedisSerializer<String> serializer = new Jackson2JsonRedisSerializer<>(String.class);
        return buildRedisTemplate(connectionFactory, stringRedisSerializer, serializer);
    }

    @Bean
    public ZSetOperations<String, String> zSetOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory connectionFactory,
                                                                         StringRedisSerializer stringRedisSerializer) {
        Jackson2JsonRedisSerializer<PostCacheDto> serializer = new Jackson2JsonRedisSerializer<>(PostCacheDto.class);
        return buildRedisTemplate(connectionFactory, stringRedisSerializer, serializer);
    }

    private <T> RedisTemplate<String, T> buildRedisTemplate(JedisConnectionFactory connectionFactory,
                                                            StringRedisSerializer stringRedisSerializer,
                                                            Jackson2JsonRedisSerializer<T> serializer) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        template.setEnableTransactionSupport(true);

        return template;
    }
}
