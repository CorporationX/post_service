package faang.school.postservice.config.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class TopicConfig {
    @Value("${spring.data.redis.channels.post_comment_channel.name}")
    private String channelTopic;

    @Value("${spring.data.redis.channels.post_view_channel}")
    private String postViewChannelTopic;

    @Value("${spring.data.redis.channels.like_topic}")
    private String likePostTopic;

    @Bean
    ChannelTopic postCommentChannel() {
        return new ChannelTopic(channelTopic);
    }

    @Bean
    ChannelTopic postViewChannel() {
        return new ChannelTopic(postViewChannelTopic);
    }

    @Bean
    ChannelTopic likePost() {
        return new ChannelTopic(likePostTopic);
    }
}
