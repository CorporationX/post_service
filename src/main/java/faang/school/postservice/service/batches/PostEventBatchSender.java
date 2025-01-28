package faang.school.postservice.service.batches;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.properties.BatchProperties;
import faang.school.postservice.dto.event.PostFeedEvent;
import faang.school.postservice.exception.KafkaPublishPostException;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.post.PostEventProducer;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostEventBatchSender {

    private final UserServiceClient userServiceClient;
    private final PostEventProducer postEventProducer;
    private final BatchProperties batchProperties;

    @Async("postEventExecutor")
    public void sendBatch(Post post){
        List<Long> subscriberIds = getSubscriberIds(post.getAuthorId());

        if (subscriberIds.isEmpty()) {
            log.warn("No subscribers found for post's author: {}", post.getAuthorId());
            return;
        }


        if (subscriberIds.size() > batchProperties.getBatchSizeSubscribers()) {
            List<List<Long>> partitionsFollowers = ListUtils.partition(subscriberIds, batchProperties.getBatchSizeSubscribers());
            partitionsFollowers.forEach(list -> sendEvent(post, list));
        } else {
            sendEvent(post, subscriberIds);
        }
    }

    @Retryable(retryFor = KafkaPublishPostException.class, backoff = @Backoff(
            delayExpression = "${spring.kafka.topic.post.retry.delay}",
            multiplierExpression = "${spring.kafka.topic.post.retry.multiplier}"))
    private void sendEvent(Post post, List<Long> subscribers){
        PostFeedEvent event = PostFeedEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .subscribersIds(subscribers)
                .publishedAt(post.getPublishedAt())
                .build();
        log.debug("PostFeedEvent created: {}", event.toString());

        try {
            postEventProducer.sendEvent(event);
            log.debug("PostEvent has been sent to Kafka topic: {}", event);
        } catch (Exception e){
            log.error("Failed to send postEvent for postId {}: {}", event, e.getMessage());
            throw new KafkaPublishPostException("Failed to send postEvent for postId " + event, e);
        }
    }

    @Retryable(retryFor = FeignException.class, backoff = @Backoff(
            delayExpression = "${spring.kafka.topic.post.retry.delay}",
            multiplierExpression = "${spring.kafka.topic.post.retry.multiplier}"))
    private List<Long> getSubscriberIds(long authorId) {
        return userServiceClient.getFollowerIds(authorId);
    }

}
