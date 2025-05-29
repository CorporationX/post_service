package faang.school.postservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {

    private final RedisProperties redisProperties;

    // 1. Добавляем бин ObjectMapper с поддержкой Java Time
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Регистрируем модуль для Java 8 Date/Time
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Отключаем запись дат как timestamp
        return mapper;
    }

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

    // 2. Модифицируем RedisTemplate для использования кастомного ObjectMapper
    @Bean
    RedisTemplate<String, Object> redisTemplate(
            JedisConnectionFactory jedisConnectionFactory,
            ObjectMapper objectMapper) { // Инжектируем наш ObjectMapper

        // Создаем сериализатор с кастомным ObjectMapper
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        // 3. Добавляем сериализаторы для хэшей (важно!)
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }

    @Bean
    public CommandLineRunner verifyRedisConnection(RedisTemplate<String, Object> redisTemplate) {
        return args -> {
            try {
                String result = redisTemplate.getConnectionFactory().getConnection().ping();
                if (!"PONG".equals(result)) {
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
