package faang.school.postservice.kafka.publisher;

import faang.school.postservice.dto.post.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostViewEventPublisher {
    @Value("${kafka.topics.post-view-events}")
    private String TOPIC_NAME;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(PostViewEvent event) {
        String key = event.postId().toString() + event.currentTime().toString(); //todo id post + viewer id??? + post view time

        kafkaTemplate.send(TOPIC_NAME, key, event);

        log.debug("PostViewEvent sent asynchronously: {}", event);
    }
}
