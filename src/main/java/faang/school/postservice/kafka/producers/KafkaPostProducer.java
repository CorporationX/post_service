package faang.school.postservice.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic kafkaPostTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaPostEvent event){
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(kafkaPostTopic.name(), message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message){
        kafkaTemplate.send(kafkaPostTopic.name(), message);
    }
}