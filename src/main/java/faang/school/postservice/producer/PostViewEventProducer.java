package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostViewEventProducer {
    @Value("${kafka.topic.post-view}")
    private String topicName;

    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(PostViewEvent event) {
        String key = event.postId().toString() + event.authorId() + event.currentTime().toString();

        try {
            String value = objectMapper.writeValueAsString(event);
            stringKafkaTemplate.send(topicName, key, value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.debug("PostViewEvent sent asynchronously: {}", event);
    }
}
