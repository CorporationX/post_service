package faang.school.postservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaEventListener {

    @KafkaListener(topics = "comments", groupId = "post-service-group")
    public void listenComments(String message) {
        log.info("[comments] Received message: {}", message);
    }

    @KafkaListener(topics = "likes", groupId = "post-service-group")
    public void listenLikes(String message) {
        log.info("[likes] Received message: {}", message);
    }

    @KafkaListener(topics = "posts", groupId = "post-service-group")
    public void listenPosts(String message) {
        log.info("[posts] Received message: {}", message);
    }
}
