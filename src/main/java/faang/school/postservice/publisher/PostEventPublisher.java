package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonUtils jsonUtils;

    @Value("${spring.kafka.producer.topics.post}")
    private String postTopic;

    public void send(PostEvent event) {
        kafkaTemplate.send(postTopic, jsonUtils.toJson(event));
        log.info("Sent object: {}, to topic: {}", event.getClass().getName(), postTopic);
    }
}
