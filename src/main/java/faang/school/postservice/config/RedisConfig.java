package faang.school.postservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    @Value("${spring.data.redis.channels.postChannel}")
    private String postChannel;

    @Value("${spring.data.redis.channels.postLike}")
    private String postLike;

    @Value("${spring.data.redis.channels.like_post_analytics}")
    private String likePostAnalyticsChannel;

    @Value("${spring.data.redis.channels.postSavedChannel}")
    private String postSavedChannel;

    private final RedisCredentials credentials;

    @Bean("postChannelTopic")
    public ChannelTopic postChannelTopic() {
        return new ChannelTopic(postChannel);
    }

    @Bean("postLikeChannelTopic")
    public ChannelTopic postLikeTopic() {
        return new ChannelTopic(postLike);
    }

    @Bean("postSavedChannelTopic")
    public ChannelTopic postSavedChannelTopic() {
        return new ChannelTopic(postSavedChannel);
    }

    @Bean("albumChannelTopic")
    public ChannelTopic albumTopic() {
        return new ChannelTopic(credentials.getChannels().getAlbum());
    }

    @Bean("likePostChannelTopicAnalytics")
    public ChannelTopic likePostTopic() {
        return new ChannelTopic(likePostAnalyticsChannel);
    }

    @Bean
    public ChannelTopic commentEventTopic() {
        return new ChannelTopic(credentials.getChannels().getComment());
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(credentials.getHost(), credentials.getPort());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
