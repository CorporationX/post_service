package faang.school.postservice.publisher;

import faang.school.postservice.exception.EventPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;


@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {
    protected final KafkaTemplate<String, Object> kafkaTemplate;
    protected final String topic;

    protected void publishEvent(String key, T event, String eventType) {
        try {
            Message<T> message = MessageBuilder
                    .withPayload(event)
                    .setHeader(KafkaHeaders.TOPIC, topic)
                    .setHeader(KafkaHeaders.KEY, key)
                    .setHeader("eventType", eventType)
                    .setHeader("timestamp", System.currentTimeMillis())
                    .build();

            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("{} successfully sent [topic: {}, partition: {}, offset: {}]",
                                    eventType, topic,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send {} to topic {}: {}",
                                    eventType, topic, ex.getMessage(), ex);
                        }
                    });

        } catch (Exception e) {
            log.error("Critical error when sending {}: {}", eventType, e.getMessage(), e);
            throw new EventPublishingException(String.format("Failed to publish %s ", eventType.toLowerCase()));
        }
    }
}
