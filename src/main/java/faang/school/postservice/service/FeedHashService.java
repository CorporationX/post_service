package faang.school.postservice.service;

import faang.school.postservice.dto.post.FeedHash;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.repository.FeedHashRepository;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedHashService {
    private final FeedHashRepository feedHashRepository;
    private final RedisKeyValueTemplate redisKVTemplate;

    @Value("${feet.size}")
    private int feetSize;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feet.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feet.retry.maxDelay}"))
    public void updateFeed(PostEvent postEvent, Acknowledgment acknowledgment) {
        List<Long> followerIds = postEvent.getFollowerIds();

        for (Long userId : followerIds) {
            FeedHash feedHash = feedHashRepository.findById(userId).orElseGet(() -> {
                FeedHash newFeedHash = new FeedHash();
                newFeedHash.setId(userId);
                newFeedHash.setPostIds(new LinkedHashSet<>());
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
        System.out.println(feedHashRepository.findById(10L));
        acknowledgment.acknowledge();
    }
}
