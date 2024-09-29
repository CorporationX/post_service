package faang.school.postservice.producer.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.AbstractProducer;
import faang.school.postservice.producer.PostServiceProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostProducer extends AbstractProducer<PostEvent> implements PostServiceProducer {

    private final UserServiceClient userServiceClient;

    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        @Value("${kafka.topic.posts-topic.name}") String topicName, UserServiceClient userServiceClient) {
        super(kafkaTemplate, topicName);
        this.userServiceClient = userServiceClient;
    }

    @Override
    public void send(Post post) {
        List<Long> followers = userServiceClient.getFollowerIds(post.getAuthorId());

        PostEvent postEvent = new PostEvent(post.getId(), post.getAuthorId(), followers);

        sendEvent(postEvent);
    }
}

