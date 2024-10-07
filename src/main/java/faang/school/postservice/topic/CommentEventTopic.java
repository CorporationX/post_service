package faang.school.postservice.topic;

import org.springframework.data.redis.listener.ChannelTopic;

public class CommentEventTopic extends ChannelTopic {
    public CommentEventTopic(String name) {
        super(name);
    }
}
