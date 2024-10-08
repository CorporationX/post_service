package faang.school.postservice.config;

import faang.school.postservice.publisher.MessagePublisher;
import faang.school.postservice.publisher.RedisLikeEventPublisher;
import faang.school.postservice.publisher.RedisMessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("spring.data.redis.channel.like-event")
    private String likeEventTopic;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    } // тимплейт

    @Bean
    MessagePublisher messagePublisher() {
        RedisTemplate<String, Object> template = redisTemplate();
        return new RedisMessagePublisher(template, topic());
    } // паблишер

    @Bean
    RedisLikeEventPublisher likeEventPublisher() {
        return new RedisLikeEventPublisher(redisTemplate(), likeEventTopic());
    } // паблишер


    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("messageQueue");
    }

    @Bean
    ChannelTopic likeEventTopic() {
        return new ChannelTopic(likeEventTopic);
    }
}
