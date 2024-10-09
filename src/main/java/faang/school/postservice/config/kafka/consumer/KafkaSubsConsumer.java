package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.post.SubsDto;
import faang.school.postservice.service.redis.NewsFeedRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaSubsConsumer {
    private final NewsFeedRedisService newsFeedRedisService;

    @KafkaListener(topics = "subs_theme", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(SubsDto event,
                       Acknowledgment acknowledgment) {
        newsFeedRedisService.createFeed(event);
        log.info(event.toString());

        acknowledgment.acknowledge();
    }
}
