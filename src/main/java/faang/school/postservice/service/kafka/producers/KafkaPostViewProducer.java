package faang.school.postservice.service.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewProducer implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic viewTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaPostViewEvent event) {
        log.info("KafkaPostViewProducer method sendMessage(KafkaPostViewEvent event) was called.");
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(viewTopic.name(), message);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException in KafkaPostViewProducer method sendMessage(KafkaPostViewEvent event). ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message){
        log.info("KafkaPostViewProducer method sendMessage(String message) was called.");
        kafkaTemplate.send(viewTopic.name(), message);
    }
}
