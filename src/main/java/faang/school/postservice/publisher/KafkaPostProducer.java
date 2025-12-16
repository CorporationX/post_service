package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String POSTS_CREATE_TOPIC = "posts";

    public void publishPostCreate(PostEventDto event) {
        log.info("Publishing post create event: postId={}, authorId={}, followerCount={}",
                event.postId(), event.authorId(), event.followerIds() == null ? 0 : event.followerIds().size());
        try {
            kafkaTemplate.send(POSTS_CREATE_TOPIC, event);
            log.debug("Successfully published post create event: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish post create event: {}", event, e);
        }
    }
}