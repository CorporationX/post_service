package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.redis.service.post.PostCacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Slf4j
@Component
public class KafkaPostConsumer {

    private final PostCacheService postCacheService;

    @KafkaListener(
            topics = "${spring.kafka.topic.post.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handlePost(PostCreatedEvent event, Acknowledgment ack) {
        log.info("Starting processing of PostEventKafka for Post ID: {}", event.getPostId());

        postCacheService.updateFeedsInCache(event);
        log.info("Successfully processed PostEventKafka for Post ID: {}", event.getPostId());

        ack.acknowledge();
    }
}
