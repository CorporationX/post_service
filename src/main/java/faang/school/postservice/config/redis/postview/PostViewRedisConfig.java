package faang.school.postservice.config.redis.postview;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.listener.ChannelTopic;

public class PostViewRedisConfig {

    @Value("${spring.data.redis.channels.post_view_channel.name}")
    private String postViewChannelName;

    @Bean
    public ChannelTopic postViewTopic() {
        return new ChannelTopic(postViewChannelName);
    }
}