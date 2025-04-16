package faang.school.postservice.kafka;

import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.service.RedisViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final RedisViewService redisViewService;

    @KafkaListener(
            topics = "post_views",
            groupId = "postServiceListenerGroup"
    )
    public void consume(PostViewEvent event, Acknowledgment acknowledgment) {
        try {
            redisViewService.incrementViewCount(event.getPostId());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process PostViewEvent: {}", event, e);
        }
    }
}