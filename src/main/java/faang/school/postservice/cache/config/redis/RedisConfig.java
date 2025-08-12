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
 * RedisConfig — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 07.08.2025
 */
@Configuration
public class RedisConfig {
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

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
