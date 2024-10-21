package faang.school.postservice.config;

import faang.school.postservice.listener.HashtagListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${redis.channels.hashtag}")
    private String hashtagTopic;

    @Value("${redis.channels.like_post}")
    private String likeEventTopic;

    @Value("${redis.channels.post_view}")
    protected String postViewTopic;

    @Value("${redis.channels.comment_channel}")
    private String topicNameComment;

    @Value("${redis.channels.ad_bought}")
    private String adBoughtChannel;

    @Value("${redis.channels.user_ban}")
    private String bannedUserTopic;

    public interface MessagePublisher<T> {
        void publish(T redisEvent);
    }

    @Bean
    public ChannelTopic hashtagTopic() {
        return new ChannelTopic(hashtagTopic);
    }

    @Bean
    public ChannelTopic likeEventTopic() {
        return new ChannelTopic(likeEventTopic);
    }

    @Bean
    public ChannelTopic viewProfileTopic() {
        return new ChannelTopic(postViewTopic);
    }

    @Bean
    public ChannelTopic adBoughtTopic() {
        return new ChannelTopic(adBoughtChannel);
    }

    @Bean
    public ChannelTopic postViewTopic() {
        return new ChannelTopic(postViewTopic);
    }

    @Bean
    public ChannelTopic bannedUserTopic() {
        return new ChannelTopic(bannedUserTopic);
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
    public ChannelTopic commentTopic() {
        return new ChannelTopic(topicNameComment);
    }

    @Bean
    public MessageListenerAdapter hashtagListenerAdapter(HashtagListener hashtagListener) {
        return new MessageListenerAdapter(hashtagListener);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(
            LettuceConnectionFactory lettuceConnectionFactory,
            HashtagListener hashtagListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(lettuceConnectionFactory);
        container.addMessageListener(hashtagListenerAdapter(hashtagListener), hashtagTopic());
        return container;
    }
}
