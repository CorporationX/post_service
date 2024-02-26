package faang.school.postservice.service.hash;

import faang.school.postservice.dto.event_broker.CommentEvent;
import faang.school.postservice.dto.event_broker.LikePostEvent;
import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.dto.event_broker.PostViewEvent;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.repository.hash.PostHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class PostHashService {
    private final PostHashRepository postHashRepository;
    private final PostEventMapper postEventMapper;
    private final RedisKeyValueTemplate redisKVTemplate;
    @Value("${feed.comment_size}")
    private int commentSize;

    @Value("${feed.ttl}")
    private long ttl;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void savePost(PostEvent postEvent, Acknowledgment acknowledgment) {
        boolean exists = postHashRepository.findById(postEvent.getPostId()).isPresent();
        if (exists) {
            acknowledgment.acknowledge();
            return;
        }

        PostHash postHash = postEventMapper.toPostHash(postEvent);
        postHash.setTtl(ttl);
        postHashRepository.save(postHash);
        acknowledgment.acknowledge();
    }

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void updatePostViews(PostViewEvent postViewEvent, Acknowledgment acknowledgment) {
        postHashRepository.findById(postViewEvent.getPostId()).ifPresentOrElse(
                postHash -> {
                    postHash.getViews().add(postViewEvent);
                    redisKVTemplate.update(postHash);
                    acknowledgment.acknowledge();
                },
                acknowledgment::acknowledge
        );
    }

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void updateLikePost(LikePostEvent likeEvent, Acknowledgment acknowledgment) {
        postHashRepository.findById(likeEvent.getPostId()).ifPresentOrElse(
                postHash -> {
                    postHash.getLikes().add(likeEvent);
                    redisKVTemplate.update(postHash);
                    acknowledgment.acknowledge();
                },
                acknowledgment::acknowledge
        );
    }

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void updateComment(CommentEvent commentEvent, Acknowledgment acknowledgment) {
        postHashRepository.findById(commentEvent.getPostId()).ifPresentOrElse(postHash -> {
            boolean add = postHash.getComments().add(commentEvent);

            if (add && postHash.getComments().size() > commentSize) {
                Iterator<CommentEvent> iterator = postHash.getComments().iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            }
            redisKVTemplate.update(postHash);
        }, acknowledgment::acknowledge);

        acknowledgment.acknowledge();
    }
}
