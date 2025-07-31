package faang.school.postservice.publisher.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducer {
    private static final String TOPIC = "post_published";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Async("postExecutor")
    public void publish(PostPublishedEvent event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, String.valueOf(event.hashCode()), json);
            log.debug("Published post event: {}", event);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
