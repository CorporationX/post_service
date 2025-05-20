package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties producerProperties;

    public void publish(CommentEventDto event) {
        String topic = producerProperties.getTopic().getCommentCreatedNotification();
        kafkaTemplate.send(topic, event);
        log.info("Sent CommentEventDto to topic {}: {}", topic, event);
    }
}
