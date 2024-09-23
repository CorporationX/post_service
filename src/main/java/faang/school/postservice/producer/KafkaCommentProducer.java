package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void send(String topic, CommentDto commentDto) {

        String ms = null;
        try {
            ms = objectMapper.writeValueAsString(commentDto);
        } catch (JsonProcessingException e) {
            log.error("kafka comment JsonProcessingException", e);
            throw new RuntimeException(e);
        }

        kafkaTemplate.send(topic, ms);
    }
}
