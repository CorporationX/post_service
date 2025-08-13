package faang.school.postservice.publisher.post.kafka;

import faang.school.postservice.dto.event.PostCreateEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostEventPublisher implements PostEventKafkaPublisher {

    private final KafkaTemplate<String, PostCreateEventDto> kafkaTemplate;

    @Value("${spring.kafka.topics.posts-topic}")
    private String topic;

    @Override
    public void publish(PostCreateEventDto event) {
        kafkaTemplate.send(topic, event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error("Failed to send PostEvent to Kafka: {}", event, exception);
                    }
                    log.info("PostEvent successfully sent to Kafka. Body: {}", result);
                });
    }
}
