package faang.school.postservice.redis.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class LikeTopic {
    @Value("${spring.data.redis.channels.like_channel}")
    private String likeChannel;
    public ChannelTopic topic(){
        return new ChannelTopic(likeChannel);
    }
}
