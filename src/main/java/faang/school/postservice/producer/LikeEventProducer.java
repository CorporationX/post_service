package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeEventProducer {
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.likeTopic}")
    private String topic;

    public void publish(LikeEventDto likeEventDto) {
        try {
            String jsonText = objectMapper.writeValueAsString(likeEventDto);
            kafkaProducer.sendMessage(topic, jsonText);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
