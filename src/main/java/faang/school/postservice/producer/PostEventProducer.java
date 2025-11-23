package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventProducer {

    private static final String POST_PUBLISHED_TOPIC_NAME = "post-published-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostPublishedEvent(PostPublishedEvent event) {
        kafkaTemplate.send(POST_PUBLISHED_TOPIC_NAME, event);
        log.info("Sent post published event: {}", event);
    }
}
