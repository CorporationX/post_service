package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.exception.NonRetryableException;
import faang.school.postservice.exception.RetryableException;
import faang.school.postservice.service.cache.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeListener extends KafkaEventListener {
    private final ObjectMapper objectMapper;
    private final PostCacheService cacheService;

    @KafkaListener(
            topics = "${spring.kafka.topic.postLike}",
            groupId = "like")
    public void consumeLikePost(String data, Acknowledgment ack) {
        try {
            LikeEvent event = objectMapper.readValue(data, LikeEvent.class);
            log.info("Like post event received from Kafka: {}", data);
            cacheService.like(event.postId());
            ack.acknowledge();
        } catch (RetryableException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NonRetryableException(e);
        }
    }
}
