package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.producer.timeout-seconds:3}")
    private long timeoutSeconds;

    @Value("${app.kafka.topics.posts-create-topic}")
    private String postsTopicName;

    public void publishPostCreate(PostEventDto event) {
        log.info("Publishing post create event: postId={}, authorId={}, followerCount={}",
                event.postId(), event.authorId(), event.followerIds() == null ? 0 : event.followerIds().size());
        try {
            SendResult<String, Object> result = kafkaTemplate.send(postsTopicName, event)
                    .get(timeoutSeconds, TimeUnit.SECONDS);
            log.info("Successfully published: postId={}, offset={}, partition={}", event.postId(),
                    result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Kafka publish interrupted for postId={}", event.postId(), e);
            throw new RuntimeException("Kafka publish interrupted", e);
        } catch (ExecutionException e) {
            log.error("Failed to publish post create event: postId={}, authorId={}, cause={}", event.postId(),
                    event.authorId(), e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
            throw new RuntimeException("Failed to publish post event", e);
        } catch (TimeoutException e) {
            log.error("Kafka publish timeout for postId={} after {} seconds",
                    event.postId(), timeoutSeconds, e);
            throw new RuntimeException("Kafka publish timeout", e);
        }
    }
}