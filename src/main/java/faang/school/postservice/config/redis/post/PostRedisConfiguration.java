package faang.school.postservice.config.redis.post;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class PostRedisConfiguration {
    @Value("${spring.data.redis.channels.post_channel.name}")
    private String postChannelName;
    @Bean
    public ChannelTopic postTopic(){
        return new ChannelTopic(postChannelName);
    }
}
