package faang.school.postservice.config.redis;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
@Setter
@ConfigurationProperties(prefix = "spring.data.redis.channels")
public class RedisConfiguration {
    private String likesChannel;

    @Bean
    ChannelTopic likeTopic() {
        return new ChannelTopic(likesChannel);
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        return new RedisTemplate<>();
    }
}
