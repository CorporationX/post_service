package faang.school.postservice.service.kafka.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaPostLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostLikeProducer implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic kafkaLikeTopic;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaPostLikeEvent event) {
        log.info("KafkaPostLikeProducer method sendMessage((KafkaPostLikeEvent) was called.");

        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(kafkaLikeTopic.name(), message);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException in KafkaPostLikeProducer method sendMessage(KafkaPostLikeEvent event). ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(String message){
        log.info("KafkaPostLikeProducer method sendMessage(String message) was called.");
        kafkaTemplate.send(kafkaLikeTopic.name(), message);
    }
}
