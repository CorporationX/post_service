package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static faang.school.postservice.contants.ErrorMessage.FAILED_SERIALIZING_OBJECT;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.producer.topics.post}")
    private String postTopic;

    public void send (PostEvent event) {
        try {
            kafkaTemplate.send(postTopic, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(FAILED_SERIALIZING_OBJECT, e);
            throw new RuntimeException(FAILED_SERIALIZING_OBJECT);
        }

        log.info("Sent object: {}, to topic: {}", event.getClass().getName(), postTopic);
    }
}
