package faang.school.postservice.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
@AllArgsConstructor
public class AbstractEventProducer<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private String topicName;

    public void sendEvent(T event){
        log.info("Event: {} to Kafka topic: {}", event, topicName);
        kafkaTemplate.send(topicName, event);
    }
}
