package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import faang.school.postservice.dto.event.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisProperties.getHost());
        redisConfig.setPort(redisProperties.getPort());
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        RedisProperties.Pool props = redisProperties.getJedis().getPool();
        poolConfig.setMaxTotal(props.getMaxActive());
        poolConfig.setMaxIdle(props.getMaxIdle());
        poolConfig.setMinIdle(props.getMinIdle());
        poolConfig.setMaxWait(props.getMaxWait());

        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
                .usePooling()
                .poolConfig(poolConfig)
                .build();

        return new JedisConnectionFactory(redisConfig, clientConfig);
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public CommandLineRunner verifyRedisConnection(RedisTemplate<String, CommentEvent> redisTemplate) {
        return args -> {
            try {
                String result = redisTemplate.getConnectionFactory().getConnection().ping();
                if(!"PONG".equals(result)){
                    throw new IllegalArgumentException("Redis ping != PONG: " + result);
                }
                log.info("✅ Redis доступен: {}", result);
            } catch (Exception e) {
                log.error("❌ Ошибка подключения к Redis", e);
                throw new IllegalStateException("Не удалось подключиться к Redis", e);
            }
        };
    }
}
