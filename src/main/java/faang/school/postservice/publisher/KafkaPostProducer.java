package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.producer.timeout-seconds:3}")
    private long timeoutSeconds;

    @Value("${kafka.topics.posts}")
    private String postsTopicName;

    public void publishPostCreate(PostEventDto event) {
        log.info("Publishing post create event: postId={}, authorId={}, followerCount={}",
                event.postId(), event.authorId(), event.followerIds() == null ? 0 : event.followerIds().size());
        try {
            SendResult<String, Object> result = kafkaTemplate.send(postsTopicName, event)
                    .get(timeoutSeconds, TimeUnit.SECONDS);
            log.info("Successfully published: postId={}, offset={}", event.postId(),
                    result.getRecordMetadata().offset());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Kafka publish interrupted", e);
        } catch (Exception e) {
            log.error("Failed to publish post create event: {}", event, e);
            throw new RuntimeException("Failed to publish post event", e);
        }
    }
}