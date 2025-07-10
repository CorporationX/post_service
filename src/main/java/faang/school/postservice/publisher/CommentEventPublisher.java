package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.commentTopic}")
    private String topic;

    public void publish(CommentEventDto commentEventDto) {
        try {
            String jsonText = objectMapper.writeValueAsString(commentEventDto);
            kafkaProducer.sendMessage(topic, jsonText);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
