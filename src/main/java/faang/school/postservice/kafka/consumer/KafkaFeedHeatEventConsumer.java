package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaFeedHeatDto;
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
public class KafkaFeedHeatEventConsumer {

    private final FeedService feedService;
    @Value("${spring.kafka.topics.feed-heat-event}")
    public final String topic;

    @KafkaListener(topics = "#{__listener.topic}", groupId = "${spring.kafka.group-id}")
    public void listen(KafkaFeedHeatDto dto, Acknowledgment acknowledgment) {
        log.info("Received message for topic {}: {}", topic, dto);
        try {
            feedService.gatherFollowersForUsers(dto.users());
            acknowledgment.acknowledge();
            log.info("Message for topic {} successfully processed and acknowledged: {}", topic, dto);
        } catch (Exception e) {
            log.error("Error processing message for topic {}: {}. Message will be redelivered.", topic, dto, e);
        }
    }
}