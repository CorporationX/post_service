package faang.school.postservice.publishers.redis;

import faang.school.postservice.events.Event;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewPublisher extends AbstractMessagePublisher<Post, PostViewEvent> {
    private final PostMapper postMapper;

    public PostViewPublisher(ChannelTopic postViewChannel,
                             RedisTemplate<String, Event> redisTemplate,
                             PostMapper postMapper) {
        super(postViewChannel, redisTemplate);
        this.postMapper = postMapper;
    }

    @Override
    PostViewEvent mapper(Post post) {
        return postMapper.toEvent(post);
    }
}
