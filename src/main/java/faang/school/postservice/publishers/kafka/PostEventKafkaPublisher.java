package faang.school.postservice.publishers.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.filter.UserFilterDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.events.PostEvent;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.AbstractKafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostEventKafkaPublisher extends AbstractKafkaMessagePublisher<Post, PostEvent> {
    private final PostMapper postMapper;
    private final UserServiceClient client;

    public PostEventKafkaPublisher(@Value("${kafka.topics.post_event}") String topic
            , KafkaTemplate<String, PostEvent> postEventKafkaTemplate,
                                   PostMapper postMapper,
                                   UserServiceClient client) {
        super(topic, postEventKafkaTemplate);
        this.postMapper = postMapper;
        this.client = client;
    }

    @Override
    public PostEvent mapper(Post post) {
        List<Long> followersIds = client.getFollowers(post.getAuthorId(), new UserFilterDto())
                .stream()
                .map(UserDto::getId)
                .toList();
        PostEvent postEvent = postMapper.toPostEvent(post);
        postEvent.setPostFollowersIds(followersIds);
        return postEvent;
    }
}
