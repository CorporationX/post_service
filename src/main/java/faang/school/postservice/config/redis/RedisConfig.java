package faang.school.postservice.config.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.dto.feed.FeedItemDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(
                redisProperties.host(),
                redisProperties.port());
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return template;
    }

    @Bean
    public RedisTemplate<String, UserResponseDto> userRedisTemplate(RedisConnectionFactory connectionFactory,
                                                                    ObjectMapper redisObjectMapper) {
        RedisTemplate<String, UserResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));

        return template;
    }

    @Bean
    public RedisTemplate<String, PostResponseDto> postRedisTemplate(RedisConnectionFactory connectionFactory,
                                                                    ObjectMapper redisObjectMapper) {
        RedisTemplate<String, PostResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));

        return template;
    }

    @Bean
    public RedisTemplate<String, FeedItemDto> feedItemRedisTemplate(RedisConnectionFactory connectionFactory,
                                                                    ObjectMapper redisObjectMapper) {
        RedisTemplate<String, FeedItemDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));

        return template;
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());

        return container;
    }

    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                .withCacheConfiguration("externalUserServiceCache",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(redisProperties.cache().userTtlMinutes()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer())));
    }
}