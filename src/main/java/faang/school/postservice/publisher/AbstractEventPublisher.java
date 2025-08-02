package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventPublisher<T> {

    private final String topic;
    private final KafkaTemplate<String, T> kafkaTemplate;

    public void publish(T event) {
        kafkaTemplate.send(topic, event)
                .whenComplete((result, e) -> {
                    if (e != null) {
                        log.error("Failed to send message: {}", e.getMessage(), e);
                        if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                            log.error("Topic not exist: {}", topic);
                        }
                    } else {
                        log.info("Message is sent: {}", result.getRecordMetadata());
                    }
                });
    }
}
