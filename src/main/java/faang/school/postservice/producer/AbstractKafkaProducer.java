package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic topic;
    private final ObjectMapper objectMapper;

    public void sendMessage(T event) {
        try {
            String msg = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic.name(), msg);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Can't serialize event");
        }
    }
}
