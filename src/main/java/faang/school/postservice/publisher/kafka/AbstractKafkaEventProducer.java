package faang.school.postservice.publisher.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaEventProducer<T> {
    private final KafkaTemplate<String, Object> multiTypeKafkaTemplate;
    private final String kafkaTopic;

    public void sendEvent(T kafkaEvent) {
        try {
            multiTypeKafkaTemplate.send(kafkaTopic, kafkaEvent);
        } catch (KafkaException e) {
            String errorMessage = "An unexpected error occurred while publishing event to kafka: " + kafkaEvent;
            log.error(errorMessage, e);
            throw new KafkaException(errorMessage, e);
        }
    }
}
