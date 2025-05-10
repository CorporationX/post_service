package faang.school.postservice.messaging;

import faang.school.postservice.service.feed.FeedServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedListener {
    private final FeedServiceImpl feedService;

    @KafkaListener(
            topics = "${app.feed.heat-topic}",
            groupId = "${app.feed.heat-group}"
    )
    public void heatFeedConsumer(String data) {
        log.info("Received heating cache user id {} from kafka", data);
        feedService.heatFeedConsumer(Long.valueOf(data));
    }
}
