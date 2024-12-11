package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import faang.school.postservice.service.FeedService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class PostKafkaConsumer extends AbstractKafkaConsumer<PostPublishedKafkaEvent> {
    private final FeedService feedService;

    public PostKafkaConsumer(ObjectMapper objectMapper, FeedService feedService) {
        super(objectMapper, PostPublishedKafkaEvent.class);
        this.feedService = feedService;
    }

    @Override
    protected void processEvent(PostPublishedKafkaEvent event) {
        feedService.addPost(event);
    }

    @KafkaListener(
            topics = "${kafka.topics.post}",
            groupId = "${kafka.consumer.groups.post-service.group-id}",
            concurrency = "${kafka.consumer.groups.post-service.concurrency}"
    )
    public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        consume(record, acknowledgment);
    }

    @Override
    protected void handleError(String eventJson, Exception e, Acknowledgment acknowledgment) {
        throw new RuntimeException(String.format("Failed to deserialize post event: %s and add to feed", eventJson), e);
    }
}
