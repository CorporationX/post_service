package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.ViewEvent;
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
public class ViewListener extends KafkaEventListener {
    private final ObjectMapper objectMapper;
    private final PostCacheService postCacheService;

    @KafkaListener(
            topics = "${spring.kafka.topic.postView}",
            groupId = "view")
    public void consumeViewPost(String data, Acknowledgment ack) {
        try {
            ViewEvent event = objectMapper.readValue(data, ViewEvent.class);
            log.info("Post view event received from Kafka: {}", data);
            postCacheService.view(event.postId());
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
