package faang.school.postservice.producer;

import faang.school.postservice.dto.post.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostViewEventProducer {
    @Value("${spring.kafka.topics.post-view}")
    private String topicName;

    @Qualifier("postViewEventKafkaTemplate")
    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    public void publish(PostViewEvent event) {
        String key = String.format("%d%d%s",
                event.postId(),
                event.author().id(),
                event.currentTime());

        try {
            kafkaTemplate.send(topicName, key, event);
        } catch (Exception e) {
            log.error("Error sending PostViewEvent: {}", e.getMessage());
        }
    }
}
