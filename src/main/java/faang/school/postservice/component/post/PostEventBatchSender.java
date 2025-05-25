package faang.school.postservice.component.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.properties.BatchProperties;
import faang.school.postservice.config.kafka.properties.RetryProperties;
import faang.school.postservice.event.PostFeedEvent;
import faang.school.postservice.exception.InvalidPostDataException;
import faang.school.postservice.exception.KafkaPublishException;
import faang.school.postservice.exception.UserServiceException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventBatchSender {

    private final UserServiceClient userServiceClient;
    private final PostEventProducer postEventProducer;
    private final BatchProperties batchProperties;
    private final RetryProperties retryProperties;

    @Async("postEventExecutor")
    public void dispatchEventsForPost(Post post) {
        if (post == null || post.getId() == null || post.getAuthorId() == null || post.getPublishedAt() == null) {
            log.error("Invalid post data for event dispatching: {}", post);
            throw new InvalidPostDataException("Post or author data is invalid");
        }

        List<Long> subscriberIds = fetchSubscribers(post.getAuthorId());
        if (subscriberIds.isEmpty()) {
            log.warn("No subscribers found for author: {}", post.getAuthorId());
            return;
        }

        if (subscriberIds.size() > batchProperties.getMaxSubscribers()) {
            log.warn("Subscriber count {} exceeds maximum allowed {}, truncating", subscriberIds.size(),
                    batchProperties.getMaxSubscribers());
            subscriberIds = subscriberIds.subList(0, batchProperties.getMaxSubscribers());
        }

        List<List<Long>> batches = partitionSubscribers(subscriberIds, batchProperties.getBatchSize());
        batches.forEach(batch -> dispatchEventBatch(post, batch));
    }

    @Retryable(retryFor = KafkaPublishException.class,
            maxAttemptsExpression = "#{@retryProperties.kafka.maxAttempts}",
            backoff = @Backoff(delayExpression = "#{@retryProperties.kafka.delay}",
                    multiplierExpression = "#{@retryProperties.kafka.multiplier}"))
    private void dispatchEventBatch(Post post, List<Long> subscribers) {
        PostFeedEvent event = PostFeedEvent.builder()
                .postId(post.getId())
                .subscriberIds(subscribers)
                .publishedAt(post.getPublishedAt())
                .build();
        log.debug("PostFeedEvent created: {}", event);
        postEventProducer.sendPostFeedEvent(event);
        log.debug("PostFeedEvent sent to Kafka: {}", event);
    }

    @Retryable(retryFor = UserServiceException.class,
            maxAttemptsExpression = "#{@retryProperties.userService.maxAttempts}",
            backoff = @Backoff(delayExpression = "#{@retryProperties.userService.delay}",
                    multiplierExpression = "#{@retryProperties.userService.multiplier}"))
    private List<Long> fetchSubscribers(Long authorId) {
        try {
            return userServiceClient.getFollowerIds(authorId);
        } catch (FeignException e) {
            log.error("Failed to fetch subscribers for author {}: {}", authorId, e.getMessage());
            throw new UserServiceException("Failed to fetch subscribers for author " + authorId, e);
        }
    }

    private List<List<Long>> partitionSubscribers(List<Long> subscribers, int batchSize) {
        return IntStream.range(0, (subscribers.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> subscribers.subList(i * batchSize,
                        Math.min((i + 1) * batchSize, subscribers.size())))
                .toList();
    }
}
