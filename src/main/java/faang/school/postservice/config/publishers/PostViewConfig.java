package faang.school.postservice.config.publishers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class PostViewConfig {

    @Value("${spring.data.redis.channels.post_view_channel.name}")
    private String postViewChannel;

    @Bean
    public ChannelTopic postViewTopic() {
        return new ChannelTopic(postViewChannel);
    }
}