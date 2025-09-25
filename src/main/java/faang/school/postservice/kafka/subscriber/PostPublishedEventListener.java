package faang.school.postservice.kafka.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostPublishedEventListener {

    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topic.post-published}")
    public void listen(String eventText) {
        log.info("Received new event: {}", eventText);
        PostPublishedEvent event = null;
        try {
            event = objectMapper.readValue(eventText, PostPublishedEvent.class);
        } catch (Exception e) {
            log.error("Error parsing event to DTO", e);
            throw new RuntimeException("Failed to parse event", e);
        }
        feedService.updateUserFeeds(event);
        log.info("Processed event: {}", event);
    }
}
