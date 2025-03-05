package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.event.NewCommentEvent;
import faang.school.postservice.redis.entities.CommentCache;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final PostRepository postRepository;
    private final PostCacheRepository postCacheRepository;
    private final Object lock = new Object();

    @Value("${spring.scheduler.comment.moderator.max-page-size}")
    private int maxCommentsPageSize;

    @Transactional
    @KafkaListener(topics = "new-comments", groupId = "feed-service")
    public void handleNewComment(NewCommentEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Processing comment event for post {}", event.getPostId());
            updateCommentCount(event);
            acknowledgment.acknowledge();
            log.info("Successfully processed comment event for post {}", event.getPostId());
        } catch (Exception e) {
            log.error("Error processing comment event for post {}", event.getPostId(), e);
            throw new KafkaException("Failed to process comment event", e);
        }
    }

    private void updateCommentCount(NewCommentEvent event) {
        postRepository.incrementCommentsCount(event.getPostId());

        postCacheRepository.findById(event.getPostId()).ifPresent(post -> {
            updatePostCacheComments(post, event);
            postCacheRepository.save(post);
        });
    }

    private void updatePostCacheComments(PostCache post, NewCommentEvent event) {
        long currentComments = post.getCommentsCount() != null ? post.getCommentsCount() : 0L;
        post.setCommentsCount(currentComments + 1);

        if (event.isVerified()) {
            CommentCache newComment = createCommentCache(event);
            updateLastComments(post, newComment);
        }
    }

    private CommentCache createCommentCache(NewCommentEvent event) {
        return CommentCache.builder()
                .id(event.getId())
                .authorId(event.getAuthorId())
                .content(event.getContent())
                .postId(event.getPostId())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    private void updateLastComments(PostCache post, CommentCache newComment) {
        LinkedHashSet<CommentCache> comments = post.getLastComments();

        if (comments == null) {
            comments = new LinkedHashSet<>();
        }
        comments.add(newComment);

        if (comments.size() > maxCommentsPageSize) {
            synchronized (lock) {
                if (comments.size() > maxCommentsPageSize) {
                    comments = comments.stream()
                            .sorted((c1, c2) -> c2.getId().compareTo(c1.getId()))
                            .limit(maxCommentsPageSize)
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                }
            }
            post.setLastComments(comments);
        }
    }
}