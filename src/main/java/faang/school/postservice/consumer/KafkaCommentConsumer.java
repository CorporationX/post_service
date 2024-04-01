package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostCommentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@KafkaListener
@Component
public class KafkaCommentConsumer extends KafkaAbstractConsumer<KafkaPostCommentEvent> {
    public KafkaCommentConsumer(ObjectMapper objectMapper) {
        super(objectMapper);
    }

}
