package faang.school.postservice.service.hash;

import faang.school.postservice.dto.hash.FeedHash;
import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.repository.hash.FeedHashRepository;
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
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedHashService {
    private final FeedHashRepository feedHashRepository;
    private final RedisKeyValueTemplate redisKVTemplate;

    @Value("${feed.size}")
    private int feetSize;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void updateFeed(PostEvent postEvent, Acknowledgment acknowledgment) {
        List<Long> followerIds = postEvent.getFollowerIds();

        followerIds.forEach(userId -> {
            FeedHash feedHash = feedHashRepository.findById(userId).orElseGet(() -> new FeedHash(userId, new LinkedHashSet<>()));
            boolean add = feedHash.getPostIds().add(postEvent.getPostId());

            if (add && feedHash.getPostIds().size() > feetSize) {
                Iterator<Long> iterator = feedHash.getPostIds().iterator();
                iterator.next();
                iterator.remove();
            }
            redisKVTemplate.update(feedHash);
        });
        acknowledgment.acknowledge();
    }
}
