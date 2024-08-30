package faang.school.postservice.config.redis.comment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class CommentConfig {

    @Value("${spring.data.redis.channels.comment_channel.name}")
    private String commentChannel;

    @Bean
    public ChannelTopic commentChannel() {
        return new ChannelTopic(commentChannel);
    }
}
