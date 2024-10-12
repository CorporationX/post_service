package faang.school.postservice.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class RedisTopicsFactory {
    @Value("${spring.data.redis.channels.comment_event_channel.name}")
    private String commentEvent;

    @Bean
    public Topic commentEventTopic() {
        return new ChannelTopic(commentEvent);
    }
}
