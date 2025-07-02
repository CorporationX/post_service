package faang.school.postservice.config;

import faang.school.postservice.cash.NewsFeed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.SortedSet;

@Configuration
@Slf4j
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Long> redisNewsFeedTemplate() {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
//        template.setHashKeySerializer(new GenericToStringSerializer<>(Long.class));
//        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//        template.setEnableTransactionSupport(true);
        return template;
    }

//    @Bean
//    public RedisTemplate<String, NewsFeed> redisNewsFeedTemplate() {
//        RedisTemplate<String, NewsFeed> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory());
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
//        template.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
//        return template;
//    }
}
