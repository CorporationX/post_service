package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class CommentKafkaConsumer extends AbstractKafkaConsumer<CommentSentKafkaEvent> {
    private final RedisPostService redisPostService;

    public CommentKafkaConsumer(ObjectMapper objectMapper, RedisPostService redisPostService) {
        super(objectMapper, CommentSentKafkaEvent.class);
        this.redisPostService = redisPostService;
    }

    @Override
    protected void processEvent(CommentSentKafkaEvent event) {
        redisPostService.addComment(event.getPostId(), event.getCommentId(), event.getCommentAuthorId(), event.getCommentContent());
    }

    @KafkaListener(
            topics = "${kafka.topics.comment}",
            groupId = "${kafka.consumer.groups.post-service.group-id}",
            concurrency = "${kafka.consumer.groups.post-service.concurrency}"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        consume(record, acknowledgment);
    }

    @Override
    protected void handleError(String eventJson, Exception e, Acknowledgment acknowledgment) {
        throw new RuntimeException(String.format("Failed to deserialize comment: %s and add to post", eventJson), e);
    }
}
