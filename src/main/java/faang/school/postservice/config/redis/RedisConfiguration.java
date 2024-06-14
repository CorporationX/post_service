package faang.school.postservice.config.redis;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
@Setter
@ConfigurationProperties(prefix = "spring.data.redis.channels")
public class RedisConfiguration {
    private String likesChannel;
    private String commentsChannel;

    @Bean
    ChannelTopic likeTopic() {
        return new ChannelTopic(likesChannel);
    }

    @Bean
    ChannelTopic commentTopic() {
        return new ChannelTopic(commentsChannel);
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }

    @Bean
    RedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory();
    }
}
