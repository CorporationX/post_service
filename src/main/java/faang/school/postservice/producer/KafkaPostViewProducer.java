package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostViewEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic postViewTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaPostViewEvent event) {
        try {
            String msg = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(postViewTopic.name(), msg);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Can't serialize event");
        }
    }
}
