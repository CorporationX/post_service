package faang.school.postservice.config.redis.user_ban;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class UserBanTopic {

    @Bean
    public ChannelTopic premiumBoughtChannel(@Value("${post.user-ban.channel.name}") String topicName) {
        return new ChannelTopic(topicName);
    }
}
