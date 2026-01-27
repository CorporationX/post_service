package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostPublishedEvent;
import faang.school.postservice.exception.KafkaProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventProducer {

    @Value("${app.kafka.topics.post-published.name}")
    private String postPublishedTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostPublishedEvent(PostPublishedEvent event) {
        kafkaTemplate.send(postPublishedTopicName, event)
                .thenRun(() -> log.info("Sent post published event: {}", event))
                .exceptionally(ex -> {
                    log.error("Error occur. Event did not send: {}", event);
                    throw new KafkaProcessingException(ex.getMessage());
                });
    }
}