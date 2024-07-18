package faang.school.postservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBannerTopic;
    @Value("${spring.data.redis.channels.notification_like_channel.name}")
    private String notificationLikeTopic;
    @Value("${spring.data.redis.channels.comment_channel.name}")
    private String commentTopic;


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
    public ChannelTopic userBannerChannel(){
        return new ChannelTopic(userBannerTopic);
    }

    @Bean
    public ChannelTopic notificationLikeChannel(){
        return new ChannelTopic(notificationLikeTopic);
    }

    @Bean
    public ChannelTopic getCommentTopic() {
        return new ChannelTopic(commentTopic);
    }

}