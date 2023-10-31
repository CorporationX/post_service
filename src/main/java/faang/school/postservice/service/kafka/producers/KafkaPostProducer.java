package faang.school.postservice.service.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducer implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic postTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaPostEvent event){
        log.info("KafkaPostProducer method sendMessage(KafkaPostEvent event) was called.");
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(postTopic.name(), message);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException in KafkaPostProducer method sendMessage(KafkaPostEvent event). ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message){
        log.info("KafkaPostProducer method sendMessage(String message) was called.");
        kafkaTemplate.send(postTopic.name(), message);
    }
}
