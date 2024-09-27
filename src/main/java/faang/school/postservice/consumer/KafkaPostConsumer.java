package faang.school.postservice.consumer;

import faang.school.postservice.dto.publishable.PostEvent;
import faang.school.postservice.exception.kafka.NonRetryableException;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.service.post.PostService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
@AllArgsConstructor
public class KafkaPostConsumer {
    private final PostService postService;
    private final RedisFeedRepository redisFeedRepository;

    @Resource(name = "newsFeedExecutor")
    private final TaskExecutor newsFeedExecutor;

    @KafkaListener(topics = "${spring.kafka.topic-name.posts}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "containerFactory")
    public void listenPostEvent(PostEvent postEvent, Acknowledgment ack){
        List<CompletableFuture<Void>> futures = postEvent.getSubscriberIds().stream()
                .map(subscriberId -> CompletableFuture.runAsync(() -> {
                    log.info("Starting async task for subscriber {}", subscriberId);
                    addPostToFeed(postEvent, subscriberId);
                }, newsFeedExecutor))
                .toList();

        if (futures.isEmpty()) {
            log.info("There are no subscribers for event {}", postEvent);
            throw new NonRetryableException(String.format("there are no subscribers for event %s", postEvent));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("event {} was sent to all subscribers", postEvent);
        ack.acknowledge();
    }

    @Transactional
    public void addPostToFeed(PostEvent postEvent, Long userId) {
        redisFeedRepository.save(userId, postEvent.getPostId());
    }
}
