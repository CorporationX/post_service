package faang.school.postservice.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentProducer implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic kafkaCommentTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaCommentEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(kafkaCommentTopic.name(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message) {
        kafkaTemplate.send(kafkaCommentTopic.name(), message);
    }
}
