package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.contants.ErrorMessage;
import faang.school.postservice.contants.InfoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    protected <T> void send(String topic, T payload) {
        try {
            String message = objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topic, message);
            log.info(InfoMessage.INFO_SENT_TOPIC, topic, message);
        } catch (JsonProcessingException e) {
            log.error(ErrorMessage.getErrorSerializeMessage(topic, e.getMessage()), e);
            throw new RuntimeException(ErrorMessage.getErrorSerializeMessage(topic, e.getMessage()));
        }
    }
}
