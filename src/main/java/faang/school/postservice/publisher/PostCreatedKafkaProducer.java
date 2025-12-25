package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostCreatedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostCreatedKafkaProducer {

    private final KafkaTemplate<String, PostCreatedEventDto> kafkaTemplate;

    @Value("${app.kafka.topics.posts-created-topic:posts-created-topic}")
    private String postCreatedEventTopic;

    public void send(PostCreatedEventDto event) {
        String key = String.valueOf(event.postId());
        kafkaTemplate.send(postCreatedEventTopic, key, event)
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send PostCreatedEvent: postId={}, eventId={}", event.postId(), event.eventId(), ex);
                    }
                });
        log.info("PostCreatedEvent sent to Kafka: postId={}, eventId={}", event.postId(), event.eventId());
    }
}