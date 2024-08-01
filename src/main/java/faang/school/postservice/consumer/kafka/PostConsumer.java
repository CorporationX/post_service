package faang.school.postservice.consumer.kafka;

import faang.school.postservice.cache.redis.FeedCache;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.feed.Feed;
import faang.school.postservice.exception.NonRetryableException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostConsumer {
    private final FeedCache feedCache;
    @Resource(name = "newsFeedExecutor")
    private final TaskExecutor newsFeedExecutor;

    @Value("${spring.data.redis.max-feed-size}")
    private int maxSize;

    @KafkaListener(topics = "${spring.kafka.topics-name.posts}", groupId = "${spring.kafka.consumer.group-id}",
    containerFactory = "containerFactory")
    public void listenPostEvent(PostEvent postEvent, Acknowledgment ack) {
        List<CompletableFuture<Void>> futures = postEvent.getSubscriberIds().stream()
                .map(subscriberId ->
                        CompletableFuture.runAsync(
                                () -> addPostToFeed(postEvent, subscriberId), newsFeedExecutor))
                .toList();
        if (futures.size() == 0) {
            log.info(" there are no subscribers for event {}", postEvent);
            throw new NonRetryableException(String.format("подписчики для события %s отсутствуют", postEvent));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("event {} was sent to all subscribers", postEvent);
        ack.acknowledge();
    }

    @Transactional
    public void addPostToFeed(PostEvent postEvent, Long userId) {
        Feed feedOfUser = feedCache.findById(userId)
                .orElseGet(() -> new Feed(userId));
        feedOfUser.addNewPost(postEvent.getId(), maxSize);
        feedCache.save(feedOfUser);
    }
}