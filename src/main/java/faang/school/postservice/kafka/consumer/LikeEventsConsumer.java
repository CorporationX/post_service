package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.PostLikeEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventsConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.likes:likes}")
    void listener(PostLikeEvent event, Acknowledgment acknowledgment){
        try {
            postCacheService.incrementConcurrentPostLikes(event.id());
            acknowledgment.acknowledge();
            log.info("Like is added to post with id: " + event.id());
        } catch (Exception e) {
            log.error("Like is not added to post with id: " + event.id());
            throw e;
        }
    }
}