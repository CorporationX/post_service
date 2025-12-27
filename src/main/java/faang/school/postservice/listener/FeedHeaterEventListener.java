
package faang.school.postservice.listener;

import faang.school.postservice.cache.FeedHeater;
import faang.school.postservice.event.FeedHeaterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeaterEventListener {

    private final FeedHeater feedHeater;

    @KafkaListener(topics = "${kafka.topic.feed-heater}", groupId = "${kafka.consumer-group.feed-heater}")
    public void handleFeedHeaterReceivedEvent(FeedHeaterEvent feedHeaterEvent, Acknowledgment ack) {
        try {
            log.info("Successfully listen event from a feed heater topic: {}", feedHeaterEvent);

            if (feedHeaterEvent.userIds() != null && !feedHeaterEvent.userIds().isEmpty())
                feedHeater.heat(feedHeaterEvent.userIds());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process feed heater event {}", feedHeaterEvent, e);
        }
    }
}