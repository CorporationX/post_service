package faang.school.postservice.component.post;

import faang.school.postservice.event.PostFeedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostEventProducer extends AbstractEventProducer<PostFeedEvent> {

    private final String postTopic;

    public PostEventProducer(
            KafkaTemplate<String, PostFeedEvent> kafkaTemplate,
            @Value("${spring.kafka.topic.post.name:posts}") String postTopic) {
        super(kafkaTemplate);
        this.postTopic = postTopic;
    }

    public void sendPostFeedEvent(PostFeedEvent event) {
        sendEvent(postTopic, event);
    }

    @Override
    protected String generateEventKey(PostFeedEvent event) {
        return "post-" + event.getPostId();
    }
}