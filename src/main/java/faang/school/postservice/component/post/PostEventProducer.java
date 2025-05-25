package faang.school.postservice.component.post;

import faang.school.postservice.event.PostFeedEvent;
import faang.school.postservice.exception.KafkaPublishException;
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
        try {
            sendEvent(postTopic, event);
            log.debug("Successfully sent PostFeedEvent: {}", event);
        } catch (Exception e) {
            log.error("Failed to send PostFeedEvent for postId {}: {}", event.getPostId(), e.getMessage());
            throw new KafkaPublishException("Failed to send event", e);
        }
    }

    @Override
    protected String generateEventKey(PostFeedEvent event) {
        return "post-" + event.getPostId();
    }
}