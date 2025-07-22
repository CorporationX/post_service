package faang.school.postservice.kafka;

import faang.school.postservice.dto.kafka.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer {

    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.post-views}")
    private String postViewsTopic;

    public void send(PostViewEvent event) {
        kafkaTemplate.send(postViewsTopic, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Post view event sent successfully for postId: {}", event.getPostId());
                    } else {
                        log.error("Failed to send post view event for postId: {}", event.getPostId(), ex);
                    }
                });
    }
}