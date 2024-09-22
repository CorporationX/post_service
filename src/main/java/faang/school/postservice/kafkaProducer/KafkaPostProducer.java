package faang.school.postservice.kafkaProducer;

import faang.school.postservice.dto.kafkaEvents.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPostCreatedEvent(PostCreatedEvent event) {
        kafkaTemplate.send("posts", event);
    }
}
