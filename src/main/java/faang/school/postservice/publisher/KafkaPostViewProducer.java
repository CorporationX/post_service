package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafka.KafkaPostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer {
    private final KafkaTemplate<String, KafkaPostViewEvent> kafkaTemplate;
    @Value("${spring.data.kafka.topics.post}")
    private String postsTopic;

    public void publishPostViewEvent(KafkaPostViewEvent kafkaPostViewEvent) {
        kafkaTemplate.send(postsTopic, kafkaPostViewEvent);
    }
}
