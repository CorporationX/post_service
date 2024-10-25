package faang.school.postservice.config;

import faang.school.postservice.cache.entity.PostCache;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.channels.postLike}")
    private String postLike;

    @Value("${spring.data.redis.channels.like_post_analytics}")
    private String likePostAnalyticsChannel;

    @Value("${spring.data.redis.channels.likeChannel}")
    private String likeChannel;

    private final RedisCredentials credentials;

    @Bean("postChannelTopic")
    public ChannelTopic postChannelTopic() {
        return new ChannelTopic(credentials.getChannels().getPost());
    }

    @Bean("postLikeChannelTopic")
    public ChannelTopic postLikeTopic() {
        return new ChannelTopic(postLike);
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
    public ChannelTopic likeEventTopic() {
        return new ChannelTopic(likeChannel);
    }

    @Bean("commentAchievementTopic")
    public ChannelTopic commentAchievementTopic() {
        return new ChannelTopic(credentials.getChannels().getCommentAchievement());
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

    @Bean
    public RedisTemplate<String, PostCache> postCacheRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, PostCache> postCacheRedisTemplate = new RedisTemplate<>();
        postCacheRedisTemplate.setConnectionFactory(redisConnectionFactory);
        postCacheRedisTemplate.setKeySerializer(new StringRedisSerializer());
        postCacheRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return postCacheRedisTemplate;
    }

    @Bean
    public RedisTemplate<String, Long> feedRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Long> feedRedisTemplate = new RedisTemplate<>();
        feedRedisTemplate.setConnectionFactory(redisConnectionFactory);
        feedRedisTemplate.setKeySerializer(new StringRedisSerializer());
        feedRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return feedRedisTemplate;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://%s:%d".formatted(credentials.getHost(), credentials.getPort()));
        return Redisson.create(config);
    }
}
