package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaSubscribersFeedHeatDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaSubscribersFeedEventConsumer {

    private final FeedService feedService;
    @Value("${spring.kafka.topics.subscribers-feed-heat-event}")
    public final String topic;

    @KafkaListener(topics = "#{__listener.topic}", groupId = "${spring.kafka.group-id}")
    public void listen(KafkaSubscribersFeedHeatDto dto, Acknowledgment acknowledgment) {
        log.info("Received message: {}", dto);
        try {
            feedService.fillFollowersFeed(dto);
            acknowledgment.acknowledge();
            log.info("Message for topic {} successfully processed and acknowledged: {}", topic, dto);
        } catch (Exception e) {
            log.error("Error processing message for topic {}: {}. Message will be redelivered.", topic, dto, e);
        }
    }
}
