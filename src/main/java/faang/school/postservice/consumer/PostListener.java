package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostNewEvent;
import faang.school.postservice.exception.NonRetryableException;
import faang.school.postservice.exception.RetryableException;
import faang.school.postservice.service.cache.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostListener extends KafkaEventListener {
    private final ObjectMapper objectMapper;
    private final FeedCacheService feedCacheService;

    @KafkaListener(
            topics = "${spring.kafka.topic.postNew}",
            groupId = "post")
    public void consumeNew(String data, Acknowledgment ack) {
        try {
            PostNewEvent event = objectMapper.readValue(data, PostNewEvent.class);
            log.info("New post event received from Kafka: {}", data);
            feedCacheService.addPost(event.getPostId(), event.getPublishedAt(), event.getFollowees());
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
