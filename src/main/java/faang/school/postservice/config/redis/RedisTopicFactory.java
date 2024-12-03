package faang.school.postservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisTopicFactory {
    @Value("${spring.data.redis.topic.user-ban}")
    private String userBanTopic;

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(userBanTopic);
    }
}
