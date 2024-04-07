package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.PostPublishedEvent;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
public class KafkaPostProducer extends AbstractKafkaEventProducer<PostPublishedEvent> {
    @Value("${kafka.producer.batch_size}")
    private int batchSize;
    @Value("${kafka.topics.post.name}")
    private String postTopic;
    private final UserServiceClient userServiceClient;

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate, UserServiceClient userServiceClient) {
        super(kafkaTemplate);
        this.userServiceClient = userServiceClient;
    }

    public void publish(long postId, long postOwnerId) {
        ListUtils.partition(userServiceClient.getFollowerIdsById(postOwnerId), batchSize)
                .forEach(partition -> send(
                        postTopic,
                        PostPublishedEvent.builder()
                                .postId(postId)
                                .ownerId(postOwnerId)
                                .authorSubscriberIds(new LinkedHashSet<>(partition))
                                .build())
                );
    }
}
