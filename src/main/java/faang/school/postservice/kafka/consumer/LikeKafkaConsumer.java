package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.LikeKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikeKafkaConsumer extends AbstractKafkaConsumer<LikeKafkaEvent> {
    private final RedisPostService redisPostService;

    public LikeKafkaConsumer(ObjectMapper objectMapper, RedisPostService redisPostService) {
        super(objectMapper, LikeKafkaEvent.class);
        this.redisPostService = redisPostService;
    }

    @Override
    protected void processEvent(LikeKafkaEvent event) {
        redisPostService.incrementLikesWithTransaction(event.getPostId(), event.getLikeId());
    }

    @KafkaListener(
            topics = "${kafka.topics.like}",
            groupId = "${kafka.consumer.groups.post-service.group-id}",
            concurrency = "${kafka.consumer.groups.post-service.concurrency}"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        consume(record, acknowledgment);
    }

    @Override
    protected void handleError(String eventJson, Exception e, Acknowledgment acknowledgment) {
        throw new RuntimeException(String.format("Failed to deserialize like event: %s and add it to post", eventJson), e);
    }
}

