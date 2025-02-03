package faang.school.postservice.utils;

import faang.school.postservice.exception.KafkaValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSender {

    private KafkaTemplate<String, PublishedPostMessage> postKafkaTemplate;

    public void send(PublishedPostMessage post, String topicName) {
        try {
            log.info("Sending JSON serializer : {}", post.getPost().getId());
            postKafkaTemplate.send(topicName, post).get();
        } catch (Exception e) {
            log.error("Failed to send message to Kafka topic: {}", topicName, e);
            throw new KafkaValidationException(e.getMessage());
        }
    }
}
