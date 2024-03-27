package faang.school.postservice.publisher.kafka_producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka_events.PostKafkaEvent;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostProducer extends AbstractKafkaProducer<PostKafkaEvent> {
    private final UserServiceClient userServiceClient;
    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;
    @Value("${spring.kafka.batch-size}")
    private int batchSize;

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void publishPostKafkaEvent(Post post) {
        List<Long> subscriberIds = userServiceClient.getSubscriberIdsByUserId(post.getAuthorId());
        if (subscriberIds.size()>1000) {
            List<List<Long>> subscriberIdsLists = ListUtils.partition(subscriberIds, batchSize);
            subscriberIdsLists.forEach(subscriberIdList -> {
                PostKafkaEvent postKafkaEvent = new PostKafkaEvent(post, subscriberIdList);
                publishKafkaEvent(postKafkaEvent, postTopic);
                log.info("Published PostKafkaEvent: " + postKafkaEvent);
            });
        }else{
            PostKafkaEvent postKafkaEvent = new PostKafkaEvent(post, subscriberIds);
            publishKafkaEvent(postKafkaEvent, postTopic);
            log.info("Published PostKafkaEvent: " + postKafkaEvent);
        }
    }
}
