package faang.school.postservice.publisher;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.PostKafkaEvent;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer extends AbstractKafkaPublisher<PostKafkaEvent> {
    private final UserServiceClient userServiceClient;

    @Value("${spring.kafka.batch-size}")
    private int batchSize;

    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void publishPostKafkaEvent(Post post) {
        List<Long> subscriberIds = userServiceClient.getSubscriberIdsByUserId(post.getAuthorId());
        if (subscriberIds.size() > 1000) {
            List<List<Long>> subscriberIdsLists = ListUtils.partition(subscriberIds, batchSize);
            subscriberIdsLists.forEach(subscriberIdList -> {
                PostKafkaEvent postKafkaEvent = new PostKafkaEvent(post, subscriberIdList);
                publishKafkaEvent(postKafkaEvent, postTopic);
            });
        } else {
            PostKafkaEvent postKafkaEvent = new PostKafkaEvent(post, subscriberIds);
            publishKafkaEvent(postKafkaEvent, postTopic);
        }
    }
}
