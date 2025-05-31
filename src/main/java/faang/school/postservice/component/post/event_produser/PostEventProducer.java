package faang.school.postservice.component.post.event_produser;

import faang.school.postservice.event.PostFeedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@Validated
public class PostEventProducer extends AbstractEventProducer<PostFeedEvent> {

    private final String postTopic;

    public PostEventProducer(
            KafkaTemplate<String, PostFeedEvent> kafkaTemplate,
            @Value("${spring.kafka.topic.post.name:posts}") String postTopic) {
        super(kafkaTemplate);
        this.postTopic = postTopic;
    }

    public CompletableFuture<Void> sendPostFeedEvent(PostFeedEvent event) {
        return sendEvent(postTopic, event);
    }

    @Override
    protected String generateEventKey(PostFeedEvent event) {
        return "post-" + event.getPostId();
    }
}