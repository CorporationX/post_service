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
    @Value("${spring.kafka.topics.post-view}")
    private String topicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(PostViewEvent event) {
        String key = event.postId().toString() + event.authorId() + event.currentTime().toString();

        kafkaTemplate.send(topicName, key, event);

        log.debug("PostViewEvent sent asynchronously: {}", event);
    }
}
