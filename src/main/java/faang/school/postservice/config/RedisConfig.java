package faang.school.postservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.comment_channel.name")
    private String commentTopic;
    @Value("${spring.data.redis.channels.like_channel.name}")
    private String likeTopic;
    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBannerTopic;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        log.info("Connections to Redis created on the host: {}, port: {}", host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public ChannelTopic commentChannel() {
        return new ChannelTopic(commentTopic);
    }

    @Bean
    public ChannelTopic likeChannel() {
        return new ChannelTopic(likeTopic);
    }

    @Bean
    public ChannelTopic userBannerChannel() {
        return new ChannelTopic(userBannerTopic);
    }

}