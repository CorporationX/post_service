package faang.school.postservice.listener;

import faang.school.postservice.dto.event.PostKafkaEventDto;
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
public class KafkaPostConsumer {
    private final FeedService feedService;
    @Value("${spring.kafka.topics.post.name}")
    private String topicName;

    @KafkaListener(topics = "${spring.kafka.topics.post.name}")
    public void listenPostEvent(PostKafkaEventDto eventDto, Acknowledgment ack) {
        log.info("Consume received event " + topicName);
        feedService.fillFeed(eventDto);
        ack.acknowledge();
    }
}