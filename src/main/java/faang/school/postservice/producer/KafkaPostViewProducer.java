package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "post-views";

    @Retryable(
            value = {RuntimeException.class, Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void sendPostViewEvent(Long postId) {
        try {
            String message = objectMapper.writeValueAsString(new PostViewEvent(postId));
            kafkaTemplate.send(TOPIC, message);
            log.info("Published post view event for post with ID: {}", postId);
        } catch (RuntimeException e) {
            log.error("Runtime exception while publishing post view event: {}", postId, e);
            throw e;
        } catch (Exception e) {
            log.error("Exception while publishing post view event: {}", postId, e);
            throw new RuntimeException("Wrapped checked exception", e);
        }
    }
    public static class PostViewEvent {
        private Long postId;

        public PostViewEvent(Long postId) {
            this.postId = postId;
        }
        public Long getPostId() {
            return postId;
        }
    }
}
