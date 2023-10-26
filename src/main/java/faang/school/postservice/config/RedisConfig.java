package faang.school.postservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.LinkedHashSet;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.channels.event_channels.likePost}")
    private String likeTopicName;
    @Value("${spring.data.redis.channels.post_channel.name}")
    private String postTopicName;
    @Value("${spring.data.redis.channels.comment_event_channel.name}")
    private String commentTopicName;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
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

    @Bean("redisUsersTemplate")
    public RedisTemplate<Long, LinkedHashSet<Long>> redisUsersTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Long, LinkedHashSet<Long>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean("redisFeedTemplate")
    public RedisTemplate<Long, LinkedHashSet<Long>> redisFeedTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Long, LinkedHashSet<Long>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean("redisPostsTemplate")
    public RedisTemplate<Long, LinkedHashSet<Long>> redisPostsTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Long, LinkedHashSet<Long>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public ChannelTopic postTopic() {
        return new ChannelTopic(postTopicName);
    }

    @Bean
    public ChannelTopic likeTopic() {
        return new ChannelTopic(likeTopicName);
    }

    @Bean
    ChannelTopic commentTopic() {
        return new ChannelTopic(commentTopicName);
    }
}
