package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentProducer {

    @Value("${spring.data.kafka.topics.comments}")
    private String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendCommentEvent(CommentEvent commentEvent) {
        kafkaTemplate.send(topic, commentEvent)
                .thenRunAsync(() -> log.info("Kafka send an event comment with id {}", commentEvent.getId()));
    }

}
