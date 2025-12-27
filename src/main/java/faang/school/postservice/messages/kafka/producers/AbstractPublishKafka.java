package faang.school.postservice.messages.kafka.producers;

import faang.school.postservice.exception.KafkaSendMessageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPublishKafka<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;
    private final String topic;

    public void publish(T message) {
        log.info("Sending to the Kafka topic {}", message);

        try {
            kafkaTemplate.send(topic, message).get();
            log.info("Message sent successfully: {}", message);
        } catch (ExecutionException e) {
            log.error("Failed to send message: {}", message, e.getCause());
            throw new KafkaSendMessageException("Error sending to kafka topic " + topic, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendMessageException("Interrupted thread to send kafka topic " + topic, e);
        }
    }
}