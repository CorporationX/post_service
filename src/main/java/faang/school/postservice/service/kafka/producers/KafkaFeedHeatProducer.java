package faang.school.postservice.service.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaFeedHeatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaFeedHeatProducer implements KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic feedHeatTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaFeedHeatEvent event) {
        log.info("KafkaFeedHeatProducer method sendMessage(KafkaFeedHeatEvent event) was called.");
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(feedHeatTopic.name(), message);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException in KafkaFeedHeatProducer method sendMessage(KafkaFeedHeatEvent event). ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message) {
        log.info("KafkaFeedHeatProducer method sendMessage(String message) was called.");
        kafkaTemplate.send(feedHeatTopic.name(), message);
    }
}
