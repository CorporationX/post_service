package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.events.Event;
import faang.school.postservice.model.redis.Post;
import faang.school.postservice.model.redis.User;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.TreeSet;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }

    @Bean
    Jedis jedis(){
        return new Jedis(host,port);
    }

    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        return Redisson.create(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setValueSerializer(RedisSerializer.string());
        return template;
    }

    @Bean
    public RedisTemplate<String, Event> eventRedisTemplate(JedisConnectionFactory jedisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Event> redisEventTemplate = new RedisTemplate<>();
        redisEventTemplate.setConnectionFactory(jedisConnectionFactory);
        redisEventTemplate.setKeySerializer(RedisSerializer.string());

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        redisEventTemplate.setValueSerializer(serializer);

        return redisEventTemplate;
    }

    @Bean
    public RedisTemplate<String, Long> feedRedisTemplate(JedisConnectionFactory jedisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, Long> redisFeedTemplate = new RedisTemplate<>();
        redisFeedTemplate.setConnectionFactory(jedisConnectionFactory);
        redisFeedTemplate.setKeySerializer(RedisSerializer.string());
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        redisFeedTemplate.setValueSerializer(serializer);
        return redisFeedTemplate;
    }

    @Bean
    public RedisTemplate<Long, User> userRedisTemplate(JedisConnectionFactory jedisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<Long, User> redisFeedTemplate = new RedisTemplate<>();
        redisFeedTemplate.setConnectionFactory(jedisConnectionFactory);
        redisFeedTemplate.setKeySerializer(RedisSerializer.string());
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        redisFeedTemplate.setValueSerializer(serializer);
        return redisFeedTemplate;
    }

    @Bean
    public RedisTemplate<Long, Post> postRedisTemplate(JedisConnectionFactory jedisConnectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<Long, Post> redisFeedTemplate = new RedisTemplate<>();
        redisFeedTemplate.setConnectionFactory(jedisConnectionFactory);
        redisFeedTemplate.setKeySerializer(RedisSerializer.string());
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        redisFeedTemplate.setValueSerializer(serializer);
        return redisFeedTemplate;
    }
}


