package faang.school.postservice.topic;

import org.springframework.data.redis.listener.ChannelTopic;

public class PostTopic extends ChannelTopic {
    public PostTopic(String name) {
        super(name);
    }
}
