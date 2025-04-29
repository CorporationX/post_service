package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.ExceptionMessage;
import faang.school.postservice.exception.KafkaMessageSerializationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Slf4j
public class AbstractEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    protected void sendMessage(Object publisher, String topic) {
        String message;
        try {
            message = objectMapper.writeValueAsString(publisher);
        } catch (Exception e) {
            throw new KafkaMessageSerializationException(ExceptionMessage.SERIALIZE_EXCEPTION, e);
        }

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka send failed {}", ex.getMessage());
            }
        });
    }
}