package faang.school.postservice.config.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
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

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        return mapper;
//    }

    @Bean
    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory jedisConnectionFactory,
                                                                         ObjectMapper objectMapper) {
        RedisTemplate<String, PostCacheDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(/*objectMapper*/);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        return template;
    }

//    @Bean
//    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory jedisConnectionFactory
//            /*ObjectMapper objectMapper*/) {
//        RedisTemplate<String, PostCacheDto> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory);
//
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(serializer);
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(serializer);
//        return template;
//    }

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.activateDefaultTyping(
//                mapper.getPolymorphicTypeValidator(),
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY
//        );
//        return mapper;
//    }

//    @Bean
//    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
//        RedisTemplate<String, PostCacheDto> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(serializer);
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(serializer);
//        return template;
//    }

//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
//        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
//        //objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//
//        RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//
//
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
//                .entryTtl(Duration.ofMinutes(1));
//
//        return RedisCacheManager.builder(redisConnectionFactory)
//                .cacheDefaults(defaultRedisCacheConfiguration())
//                .build();
//    }

//    @Bean
//    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
//        RedisTemplate<String, PostCacheDto> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(serializer); // Убедитесь, что этот сериализатор правильно настроен
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(serializer);
//        return template;
//    }

//    @Bean
//    public RedisTemplate<String, PostCacheDto> postCacheDtoRedisTemplate(JedisConnectionFactory connectionFactory) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.activateDefaultTyping(
//                objectMapper.getPolymorphicTypeValidator(),
//                ObjectMapper.DefaultTyping.NON_FINAL,
//                JsonTypeInfo.As.PROPERTY
//        );
//
//        RedisTemplate<String, PostCacheDto> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(serializer);
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(serializer);
//        return template;
//    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(/*objectMapper*/);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        return template;
    }

    @Bean
    public ZSetOperations<String, String> zSetOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}
