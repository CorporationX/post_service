package faang.school.postservice.config.redis;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.publisher.LikeEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${redis.pubsub.topics:like-event}")
    private String topic;

    @Bean
    public ChannelTopic likeEventTopic() {
        return new ChannelTopic(topic);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public LikeEventPublisher redisPublisher(RedisTemplate<String, LikeEvent> redisTemplate, ChannelTopic likeEventTopic) {
        return new LikeEventPublisher(redisTemplate, likeEventTopic);
    }

}
