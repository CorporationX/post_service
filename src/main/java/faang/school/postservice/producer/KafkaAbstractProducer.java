package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class KafkaAbstractProducer<E> {

    private final KafkaTemplate<String, E> kafkaTemplate;

    public void sendMessage(String topic, E event) {
        kafkaTemplate.send(topic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("The post event was sent to the topic {}: {}", topic, event);
                    } else {
                        log.error("An error occurred while sending post event", ex);
                    }
                });
    }
}
