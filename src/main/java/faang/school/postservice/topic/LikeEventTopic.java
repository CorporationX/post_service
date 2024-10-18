package faang.school.postservice.topic;

import org.springframework.data.redis.listener.ChannelTopic;

public class LikeEventTopic extends ChannelTopic {
    public LikeEventTopic(String name) {
        super(name);
    }
}
