package faang.school.postservice.publishers.kafka;

import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.AbstractKafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostViewEventKafkaPublisher extends AbstractKafkaMessagePublisher<Post, PostViewEvent> {
    private final PostMapper postMapper;

    public PostViewEventKafkaPublisher(@Value("${kafka.topics.post_view_event}") String topic
            , KafkaTemplate<String, PostViewEvent> postViewEventKafkaTemplate,
                                       PostMapper postMapper) {
        super(topic, postViewEventKafkaTemplate);
        this.postMapper = postMapper;
    }

    @Override
    public PostViewEvent mapper(Post post) {
        return postMapper.toEvent(post);
    }
}
