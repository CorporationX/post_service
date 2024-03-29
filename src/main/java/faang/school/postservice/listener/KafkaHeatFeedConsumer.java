package faang.school.postservice.listener;

import faang.school.postservice.dto.event.HeatFeedKafkaEventDto;
import faang.school.postservice.service.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaHeatFeedConsumer {
    private final FeedHeaterService feedHeaterService;
    @Value("${spring.kafka.topics.heat-feed.name}")
    private String topicName;

    @KafkaListener(topics = "${spring.kafka.topics.heat-feed.name}")
    public void listenPostEvent(HeatFeedKafkaEventDto event) {
        log.info("Consume received event " + topicName);
        feedHeaterService.fillFeedCache(event);
    }
}