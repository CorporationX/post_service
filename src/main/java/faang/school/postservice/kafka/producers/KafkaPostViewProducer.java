package faang.school.postservice.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostViewEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic kafkaViewTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaPostViewEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(kafkaViewTopic.name(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message){
        kafkaTemplate.send(kafkaViewTopic.name(), message);
    }
}
