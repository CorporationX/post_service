package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class KafkaProducerService<T> {

    private final KafkaTemplate<String, T> kafkaTemplate;
    protected final ObjectMapper objectMapper;
    protected final String topicName;

    protected void sendMessage(T event) {
        try {
            sendSerializedMessage(event);
        } catch (Exception e) {
            log.error("Error serializing post message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected void sendSerializedMessage(T message) {

        CompletableFuture<SendResult<String, T>> future = kafkaTemplate.send(topicName, message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message {} to kafka topic {}. Error: {}",
                        message,
                        topicName,
                        ex.getMessage());
            } else {
                log.info("Successfully sent message {} to kafka topic {}, partition {}. Result: {}",
                        message,
                        topicName,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
