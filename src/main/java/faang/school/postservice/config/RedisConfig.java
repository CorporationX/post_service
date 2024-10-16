package faang.school.postservice.config;

import faang.school.postservice.service.messaging.HashtagListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.hashtags}")
    private String topicNameHashtags;

    @Value("${spring.data.redis.channels.like_post}")
    private String topicNameLike;

    @Value("${spring.data.redis.channels.post_view_channel}")
    String postViewTopic;

    @Value("${spring.data.redis.channels.ad_bought_channel}")
    String adBoughtChannel;

    public interface MessagePublisher<T> {
        void publish(T redisEvent);
    }

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        log.info("redis host {}, port {} ", host, port);
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public ChannelTopic hashtagTopic() {
        return new ChannelTopic(topicNameHashtags);
    }

    @Bean
    public ChannelTopic likeTopic() {
        return new ChannelTopic(topicNameLike);
    }

    @Bean
    public MessageListenerAdapter hashtagListenerAdapter(HashtagListener hashtagListener) {
        return new MessageListenerAdapter(hashtagListener);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(MessageListenerAdapter hashtagListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(hashtagListenerAdapter, hashtagTopic());
        return container;
    }

    @Bean
    public ChannelTopic postViewTopic() {
        return new ChannelTopic(postViewTopic);
    }

    @Bean
    ChannelTopic adBoughtTopic() {
        return new ChannelTopic(adBoughtChannel);
    }
}
