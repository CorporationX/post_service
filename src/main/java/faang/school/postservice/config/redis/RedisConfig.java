package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(redisProperties.host(), redisProperties.port());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, List<Long>> redisTemplate() {
        final RedisTemplate<String, List<Long>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(redisProperties.usersBanTopic());
    }

    @Bean
    public MessageSender redisSender() {
        return new MessageSenderImpl(redisTemplate(), topic());
    }

    @Bean
    public ChannelTopic channelTopicForUserBan() {
        String topic = redisProperties.usersBanTopic();
        log.info("Creating ChannelTopic for User Ban with topic: {}", topic);
        return new ChannelTopic(topic);    
    }

    @Bean
    public MessageSender messageSenderForUserBan(RedisTemplate<String, List<Long>> redisTemplate, ChannelTopic channelTopicForUserBan) {
        return new MessageSenderForUserBanImpl(redisTemplate, channelTopicForUserBan);
    }
}
