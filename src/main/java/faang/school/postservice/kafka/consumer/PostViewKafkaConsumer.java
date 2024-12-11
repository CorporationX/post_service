package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class PostViewKafkaConsumer extends AbstractKafkaConsumer<PostViewKafkaEvent> {
    private final RedisPostService redisPostService;

    public PostViewKafkaConsumer(ObjectMapper objectMapper, RedisPostService redisPostService) {
        super(objectMapper, PostViewKafkaEvent.class);
        this.redisPostService = redisPostService;
    }

    @Override
    protected void processEvent(PostViewKafkaEvent event) {
        redisPostService.incrementPostViewsWithTransaction(event.getPostId(), event.getViewerId(), event.getViewDateTime());
    }

    @KafkaListener(
            topics = "${kafka.topics.post-view}",
            groupId = "${kafka.consumer.groups.post-service.group-id}",
            concurrency = "${kafka.consumer.groups.post-service.concurrency}"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        consume(record, acknowledgment);
    }

    @Override
    protected void handleError(String eventJson, Exception e, Acknowledgment acknowledgment) {
        throw new RuntimeException(String.format("Failed to deserialize post view event: %s and add to post", eventJson), e);
    }
}
