package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentEventProducer extends KafkaProducer<CommentEvent> {
    private final KafkaProperty kafkaProps;

    public CommentEventProducer(ObjectMapper objectMapper,
                                KafkaProperty kafkaProperty,
                                KafkaTemplate<String, String> kafkaTemplate) {
        super(objectMapper, kafkaTemplate);
        this.kafkaProps = kafkaProperty;
    }

    public void sendEvent(CommentEvent event) {
        log.info("Sending event of new comment: commentId = {}", event.id());
        sendTo(kafkaProps.topic().commentNew(), event);
    }
}
