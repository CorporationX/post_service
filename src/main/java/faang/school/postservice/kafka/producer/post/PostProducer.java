package faang.school.postservice.kafka.producer.post;

import faang.school.postservice.config.properties.kafka.KafkaTopicsProperties;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.factory.post.PostPublishedEventFactory;
import faang.school.postservice.kafka.producer.AbstractKafkaProducer;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class PostProducer extends AbstractKafkaProducer<PostPublishedEvent> {

    private final KafkaTopicsProperties topics;
    private final PostRepository postRepository;
    private final PostPublishedEventFactory postPublishedEventFactory;

    @Value("${post.publish.event-subscriber-batch-size}")
    private int eventSubscriberBatchSize;

    @Value("${post.publish.db-subscriber-batch-size}")
    private int dbSubscriberBatchSize;

    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        KafkaTopicsProperties topics,
                        PostRepository postRepository,
                        PostPublishedEventFactory postPublishedEventFactory) {
        super(kafkaTemplate);
        this.topics = topics;
        this.postRepository = postRepository;
        this.postPublishedEventFactory = postPublishedEventFactory;
    }

    public void publishPostPublishedEvent(PostPublishedEvent event) {
        publishEvent(topics.post_published(), String.valueOf(event.getPostId()), event);
    }

    public void publishPostPublishedEventsBatched(@NonNull Post post) {
        long lastId = 0L;
        List<Long> dbBatch;

        do {
            dbBatch = postRepository.getFollowerIdsBatch(lastId, dbSubscriberBatchSize);
            if (!dbBatch.isEmpty()) {
                sendEventBatches(dbBatch, post);
                lastId = dbBatch.get(dbBatch.size() - 1);
            }
        } while (!dbBatch.isEmpty());
    }

    private void sendEventBatches(@NonNull List<Long> allSubscriberIds, @NonNull Post post) {
        for (int i = 0; i < allSubscriberIds.size(); i += eventSubscriberBatchSize) {
            List<Long> batch = allSubscriberIds.subList(
                    i,
                    Math.min(i + eventSubscriberBatchSize, allSubscriberIds.size())
            );
            publishPostPublishedEvent(postPublishedEventFactory.fromPostAndFollowerIds(post, batch));
        }
    }
}
