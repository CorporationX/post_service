package faang.school.postservice.listener;

import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.kafka.HeatFeedEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaHeatPostConsumer {

    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.topics.heater.name}", groupId = "post-group")
    public void listenFeedHeatEvent(HeatFeedEvent event) {
        if (event != null) {
            Long userId = event.userId();
            PostPair postPairs = event.postPair();

            log.info("Received Post heater event with User ID: {}, Post ID: {}", userId, postPairs.postId());
            feedService.saveSinglePostToFeed(userId, postPairs);
        } else {
            log.warn("Received empty heat Feed event");
        }
    }
}
