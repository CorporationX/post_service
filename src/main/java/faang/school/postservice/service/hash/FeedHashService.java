package faang.school.postservice.service.hash;

import faang.school.postservice.dto.hash.FeedHash;
import faang.school.postservice.dto.post.PostEvent;
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
import java.util.List;
import java.util.Set;

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

        for (Long userId : followerIds) {
            FeedHash feedHash = feedHashRepository.findById(userId).orElseGet(() -> {
                FeedHash newFeedHash = new FeedHash();
                newFeedHash.setId(userId);
                return feedHashRepository.save(newFeedHash);
            });

            Set<Long> postIds = feedHash.getPostIds();
            boolean add = postIds.add(postEvent.getPostId());

            if (postIds.size() >= feetSize + 1 && add) {
                Iterator<Long> iterator = postIds.iterator();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                }
            }
            redisKVTemplate.update(feedHash);
        }
        acknowledgment.acknowledge();
    }
}
