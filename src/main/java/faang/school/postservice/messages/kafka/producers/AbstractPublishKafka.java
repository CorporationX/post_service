package faang.school.postservice.messages.kafka.producers;

import faang.school.postservice.exception.KafkaSendMessageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPublishKafka {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    @Async
    public void publish(Object message) {
        log.info("Sending to the Kafka topic {}", message);

        kafkaTemplate.send(topic, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message: {}", message, ex);
                        throw new KafkaSendMessageException(
                                "Error sending message to topic " + topic + ": " + ex.getMessage(), ex
                        );
                    } else {
                        log.info("Message sent successfully: {}", message);
                    }
                });
    }
}