package faang.school.postservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AbstractKafkaProducer<T> {

    protected KafkaTemplate<String, Object> kafkaTemplate;
    private ObjectMapper objectMapper;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void publishKafkaEvent(T event, String topic) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
            log.info("Сообщение {} отправлено в Kafka топик {}", eventJson, topic);
        } catch (JsonProcessingException e) {
            log.error("Не удалось сериализовать сообщение  в JSON", e);
        }

    }

}