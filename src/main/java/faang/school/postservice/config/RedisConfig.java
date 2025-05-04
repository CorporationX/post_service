package faang.school.postservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.hashtags.HashtagRedisWarmUpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${app.redis.topic.comment-analytics}")
    private String commentAnalyticsTopicName;

    @Value("${app.redis.topic.comment-analytics}")
    private String userBanTopicName;

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<String> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, String.class);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ZSetOperations<String, String> zSetOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public HashOperations<String, String, String> hashOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    CommandLineRunner warmUp(HashtagRedisWarmUpService redisWarmUpService) {
        return args -> redisWarmUpService.warmUpCache();
    }

    @Bean
    public ChannelTopic commentAnalyticsTopic() {
        return new ChannelTopic(commentAnalyticsTopicName);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean
    public ChannelTopic userTopic() {
        return new ChannelTopic(userBanTopicName);
    }
}
