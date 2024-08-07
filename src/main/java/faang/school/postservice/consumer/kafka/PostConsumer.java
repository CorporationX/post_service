package faang.school.postservice.consumer.kafka;

import faang.school.postservice.cache.redis.FeedCache;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.exception.NonRetryableException;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @KafkaListener(topics = "${spring.kafka.topics-name.posts}", groupId = "${spring.kafka.consumer.group-id}",
    containerFactory = "containerFactory")
    public void listenPostEvent(PostEvent postEvent, Acknowledgment ack) {
        List<CompletableFuture<Void>> futures = postEvent.getSubscriberIds().stream()
                .map(subscriberId ->
                        CompletableFuture.runAsync(
                                () -> addPostToFeed(postEvent, subscriberId), newsFeedExecutor))
                .toList();
        if (futures.isEmpty()) {
            log.info(" there are no subscribers for event {}", postEvent);
            throw new NonRetryableException(String.format("подписчики для события %s отсутствуют", postEvent));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("event {} was sent to all subscribers", postEvent);
        ack.acknowledge();
    }

    @Transactional
    public void addPostToFeed(PostEvent postEvent, Long userId) {
        feedCache.save(userId, postEvent.getId());
    }
}