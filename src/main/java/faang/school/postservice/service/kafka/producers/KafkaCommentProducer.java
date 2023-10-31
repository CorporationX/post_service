package faang.school.postservice.service.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentProducer implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic commentTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaCommentEvent event) {
        log.info("KafkaCommentProducer method sendMessage(KafkaCommentEvent event) was called.");
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(commentTopic.name(), message);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException in KafkaCommentProducer method sendMessage(KafkaCommentEvent event). ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message) {
        log.info("KafkaCommentProducer method sendMessage(String message) was called.");
        kafkaTemplate.send(commentTopic.name(), message);
    }
}
