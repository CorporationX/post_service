package faang.school.postservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final static String TOPIC = "likes";

    public void sendKafka(LikeEvent likeEvent) {
        try {
            String json = objectMapper.writeValueAsString(likeEvent);
            kafkaTemplate.send(TOPIC, json);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}