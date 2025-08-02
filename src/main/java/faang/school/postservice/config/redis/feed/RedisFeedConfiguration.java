package faang.school.postservice.config.redis.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisFeedConfiguration {

    private final RedisFeedProperties properties;

    @Bean
    public RedisConnectionFactory redisNewsFeedConnectionFactory() {
        return new LettuceConnectionFactory(properties.getHost(), properties.getPort());
    }

    @Bean("redisNewsFeedTemplate")
    public RedisTemplate<String, Object> redisNewsFeedTemplate(RedisConnectionFactory redisNewsFeedConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisNewsFeedConnectionFactory);

        // Создаем и настраиваем ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Регистрируем модуль для поддержки Java 8 Time API
        objectMapper.registerModule(new JavaTimeModule());

        // Отключаем сериализацию полей-дат как timestamp-чисел для лучшей читаемости в Redis
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Создаем сериализатор Jackson с настроенным ObjectMapper
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Устанавливаем настроенный сериализатор для значений
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
}