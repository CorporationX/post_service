package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaFeedHeatDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaFeedHeatEventConsumer {

    private final FeedService feedService;
    @Value("${spring.kafka.topics.feed-heat-event}")
    public final String topic;

    @KafkaListener(topics = "#{__listener.topic}", groupId = "my-group")
    public void listen(KafkaFeedHeatDto dto) {
        log.info("Received message: {}", dto);
        feedService.fillCacheForUsers(dto.userIds());
    }
}
