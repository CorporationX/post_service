package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.LinkedHashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisProperties.getHost(), redisProperties.getPort());
    }

    @Bean
    public RedisTemplate<String, List<Long>> listRedisTemplate(RedisConnectionFactory connection) {
        RedisTemplate<String, List<Long>> redisTemplate = new RedisTemplate<>();
        var serializer = new Jackson2JsonRedisSerializer<>(List.class);
        redisTemplate.setConnectionFactory(connection);
        redisTemplate.setValueSerializer(serializer);
        return redisTemplate;
    }

    @Bean(name = "feedTemplate")
    public RedisTemplate<Long, LinkedHashSet<Long>> feedRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, LinkedHashSet<Long>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(LinkedHashSet.class));
        redisTemplate.setEnableTransactionSupport(true);
        return redisTemplate;
    }
}