package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.event.NewLikeEvent;
import faang.school.postservice.model.LikeType;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final PostRepository postRepository;
    private final PostCacheRepository postCacheRepository;
    private final Map<Long, Object> postLocks = new ConcurrentHashMap<>();

    @Transactional
    @KafkaListener(topics = "new-likes", groupId = "feed-service")
    public void handleNewLike(NewLikeEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Processing like event for post {}, type: {}", event.getPostId(), event.getType());

            updateLikeCount(event);

            acknowledgment.acknowledge();
            log.info("Successfully processed like event for post {}", event.getPostId());
        } catch (Exception e) {
            log.error("Error processing like event for post {}", event.getPostId(), e);
            throw new KafkaException("Failed to process like event", e);
        }
    }

    private void updateLikeCount(NewLikeEvent event) {
        if (event.getType() == LikeType.LIKE) {
            postRepository.incrementLikesCount(event.getPostId());
        } else {
            postRepository.decrementLikesCount(event.getPostId());
        }

        postCacheRepository.findById(event.getPostId()).ifPresent(post -> {
            updatePostCacheLikes(post, event);
            postCacheRepository.save(post);
        });
    }

    private void updatePostCacheLikes(PostCache post, NewLikeEvent event) {
        Object lock = postLocks.computeIfAbsent(post.getId(), k -> new Object());

        synchronized (lock) {
            long currentLikes = post.getLikesCount() != null ? post.getLikesCount() : 0L;
            post.setLikesCount(event.getType() == LikeType.LIKE ?
                    currentLikes + 1 : Math.max(0, currentLikes - 1));
        }
    }
}