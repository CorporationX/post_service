package faang.school.postservice.config.redis;

import faang.school.postservice.publisher.AdBoughtEventPublisher;
import faang.school.postservice.publisher.LikeEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private String likeEventTopic;

    @Value("${redis.pubsub.topics:adBought-event}")
    private String adBoughtTopic;

    @Bean(name = "likeEventTopic")
    public ChannelTopic likeEventTopic() {
        return new ChannelTopic(likeEventTopic);
    }

    @Bean(name = "adBoughtEventTopic")
    public ChannelTopic adBoughtEventTopic() {
        return new ChannelTopic(adBoughtTopic);
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
    public LikeEventPublisher likeEventPublisher(RedisTemplate<String, Object> redisTemplate, @Qualifier("likeEventTopic") ChannelTopic likeEventTopic) {
        return new LikeEventPublisher(redisTemplate, likeEventTopic);
    }

    @Bean
    public AdBoughtEventPublisher adBoughtEventPublisher(RedisTemplate<String, Object> redisTemplate, @Qualifier("adBoughtEventTopic") ChannelTopic adBoughtTopic) {
        return new AdBoughtEventPublisher(redisTemplate, adBoughtTopic);
    }

}
