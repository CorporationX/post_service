package faang.school.postservice.component.post;

import faang.school.postservice.client.SubscriptionServiceClient;
import faang.school.postservice.component.post.event_produser.PostEventProducer;
import faang.school.postservice.config.kafka.properties.BatchProperties;
import faang.school.postservice.event.PostFeedEvent;
import faang.school.postservice.exception.InvalidPostDataException;
import faang.school.postservice.exception.UserServiceException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostEventBatchSender {

    private final SubscriptionServiceClient subscriptionServiceClient;
    private final PostEventProducer postEventProducer;
    private final BatchProperties batchProperties;
    private final RetryTemplate userServiceRetryTemplate;

    private void validatePost(Post post) {
        if (post == null || post.getId() == null || post.getAuthorId() == null || post.getPublishedAt() == null) {
            log.error("Invalid post data for event dispatching: post={}", post);
            throw new InvalidPostDataException("Post or author data is invalid");
        }
    }

    @Async("postEventExecutor")
    public CompletableFuture<Void> dispatchEventsForPost(Post post) {

        validatePost(post);

        return fetchSubscribers(post.getAuthorId())
                .thenCompose(subscriberIds -> {
                    if (subscriberIds.isEmpty()) {
                        log.warn("No subscribers found for author: authorId={}", post.getAuthorId());
                        return CompletableFuture.completedFuture(null);
                    }

                    if (subscriberIds.size() > batchProperties.getMaxSubscribers()) {
                        log.warn("Subscriber count {} exceeds maximum allowed {}, truncating", subscriberIds.size(),
                                batchProperties.getMaxSubscribers());
                        subscriberIds = subscriberIds.subList(0, batchProperties.getMaxSubscribers());
                    }

                    List<List<Long>> batches = partitionSubscribers(subscriberIds, batchProperties.getBatchSize());
                    return CompletableFuture.allOf(
                            batches.stream()
                                    .map(batch -> dispatchEventBatch(post, batch))
                                    .toArray(CompletableFuture[]::new)
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Failed to process events for post: postId={}, error={}", post.getId(),
                            throwable.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Void> dispatchEventBatch(Post post, List<Long> subscribers) {
        PostFeedEvent event = PostFeedEvent.builder()
                .postId(post.getId())
                .subscriberIds(subscribers)
                .publishedAt(post.getPublishedAt())
                .build();
        log.debug("Created PostFeedEvent: event={}", event);

        return postEventProducer.sendPostFeedEvent(event);
    }

    private CompletableFuture<List<Long>> fetchSubscribers(Long authorId) {
        return CompletableFuture.supplyAsync(() -> userServiceRetryTemplate.execute(context -> {
            try {
                List<Long> subscribers = subscriptionServiceClient.getFollowerIds(authorId);
                log.info("Successfully fetched subscribers for author: authorId={}", authorId);
                return subscribers;
            } catch (FeignException e) {
                log.error("Failed to fetch subscribers for author: authorId={}, error={}", authorId, e.getMessage());
                throw new UserServiceException("Failed to fetch subscribers for author " + authorId, e);
            }
        }));
    }

    private List<List<Long>> partitionSubscribers(List<Long> subscribers, int batchSize) {
        return IntStream.range(0, (subscribers.size() + batchSize - 1) / batchSize)
                .mapToObj(i -> subscribers.subList(i * batchSize,
                        Math.min((i + 1) * batchSize, subscribers.size())))
                .toList();
    }
}
