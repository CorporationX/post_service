package faang.school.postservice.listener;

import faang.school.postservice.service.post.FeedHeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedHeatListener {

    private final FeedHeatService feedHeatService;

    @KafkaListener(topics = "feed-heat-topic", groupId = "feed-heat-group")
    public void processHeatTask(String userId) {
        log.info("Received message [{}] in feed-heat-group", userId);
        feedHeatService.processHeatTask(userId);
    }
}
