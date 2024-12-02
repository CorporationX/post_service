package faang.school.postservice.listener.kafka;

import faang.school.postservice.model.event.kafka.PostEventKafka;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
@KafkaListener(topics = "${spring.kafka.topics.posts}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaPostConsumer {

    private final PostCacheService postCacheService;

    @KafkaHandler
    public void handlePost(PostEventKafka event) {
        postCacheService.updateFeedsInCache(event);
    }
}
