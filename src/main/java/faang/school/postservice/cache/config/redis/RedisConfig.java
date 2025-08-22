package faang.school.postservice.cache.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.dto.post.PostViewDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

/**
 * Конфигурация Redis для работы с кешированием данных в приложении.
 * <p>
 * Для сериализации значений применяется {@link Jackson2JsonRedisSerializer},
 * настроенный с {@link ObjectMapper}, в который подключен модуль
 * {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule} для корректной
 * работы с типами времени и дат.
 * </p>
 *
 * @author Myrza
 * @since 07.08.2025
 */
@Configuration
public class RedisConfig {
    /**
     * Создаёт бин {@link StringRedisTemplate} для упрощённой работы с Redis,
     * где ключи и значения представлены в строковом формате.
     *
     * @param factory фабрика подключений Redis
     * @return настроенный {@link StringRedisTemplate}
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * Создаёт бин {@link RedisTemplate} для хранения в Redis списков объектов {@link PostViewDto}.
     * <p>
     * Настраивает {@link ObjectMapper} с модулем {@link JavaTimeModule} для сериализации
     * и десериализации типов Java 8 Time API, а также отключает преобразование дат в таймстампы.
     * </p>
     *
     * @param factory фабрика подключений Redis
     * @return настроенный {@link RedisTemplate} с сериализацией ключей как строк
     *         и значений в формате JSON
     */
    @Bean
    public RedisTemplate<String, List<PostViewDto>> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, List<PostViewDto>> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        var mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var postsListType = mapper.getTypeFactory()
                .constructCollectionType(List.class, PostViewDto.class);

        var serializer = new Jackson2JsonRedisSerializer<>(mapper, postsListType);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        return template;
    }
}
