package faang.school.postservice.config.redis.like;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class LikeConfig {

    @Value("${spring.data.redis.channels.like_channel.name}")
    private String likeChannel;

    @Bean
    public ChannelTopic likeChannel() {
        return new ChannelTopic(likeChannel);
    }
}
