package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostToFeedEvent;
import faang.school.postservice.service.posts.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topics.post-to-feed}", groupId = "post-service-group")
    public void listen(String message, Acknowledgment ack) {
        try {
            PostToFeedEvent event = objectMapper.readValue(message, PostToFeedEvent.class);
            feedService.addPostToFeeds(event);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process kafka message: {}", message, e);
        }
    }
}