package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostViewEventPublisher {

    private static final String TOPIC_NAME = "post-view-events";

    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    public PostViewEventPublisher(KafkaTemplate<String, PostViewEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PostViewEvent event) {
        String key = event.getAuthorId().toString();

        kafkaTemplate.send(TOPIC_NAME, key, event);

        log.debug("PostViewEvent sent asynchronously: {}", event);
    }
}
