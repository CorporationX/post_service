package faang.school.postservice.service.feed.warmup;

import faang.school.postservice.client.FollowFeignClient;
import faang.school.postservice.config.properties.cache.feed.FeedWarmupProperties;
import faang.school.postservice.dto.user.feed.CacheWarmupTask;
import faang.school.postservice.dto.user.follower.FollowersPage;
import faang.school.postservice.kafka.producer.warmup.FeedWarmupProducer;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedWarmupStarterImpl implements FeedWarmupStarter {

    private static final String LOCK_KEY = "feed:heat:lock";

    private final PostRepository postRepository;
    private final FollowFeignClient followFeignClient;
    private final FeedWarmupProducer feedWarmupProducer;
    private final StringRedisTemplate redis;
    private final FeedWarmupProperties props;

    @Override
    public void startWarmup() {
        boolean locked = Boolean.TRUE.equals(
                redis.opsForValue().setIfAbsent(LOCK_KEY, "1", Duration.ofHours(props.lockTtlHours()))
        );
        if (!locked) {
            throw new IllegalStateException("Feed warmup is already running");
        }
        try {
            List<Long> authorIds = postRepository.findDistinctAuthorIdsOfPublished();
            if (authorIds == null || authorIds.isEmpty()) {
                log.info("No authors found to derive users for warmup");
                return;
            }

            Set<Long> users = new HashSet<>(authorIds);
            for (Long authorId : authorIds) {
                if (authorId == null) {
                    continue;
                }
                String cursor = null;
                do {
                    FollowersPage page = followFeignClient.getFollowerIds(authorId, cursor, 1000);
                    if (page == null || page.ids() == null || page.ids().isEmpty()) {
                        break;
                    }
                    users.addAll(page.ids());
                    cursor = page.nextCursor();
                } while (cursor != null);
            }

            if (users.isEmpty()) {
                log.info("No users to warm up");
                return;
            }

            List<Long> userIds = new ArrayList<>(users);
            int batch = props.batchSize();
            for (int i = 0; i < userIds.size(); i += batch) {
                feedWarmupProducer.publishBatch(
                        new CacheWarmupTask(userIds.subList(i, Math.min(i + batch, userIds.size())))
                );
            }
            log.info("Feed warmup batches published: totalUsers={}, batchSize={}", userIds.size(), batch);

        } finally {
            try {
                redis.delete(LOCK_KEY);
            } catch (Exception e) {
                log.warn("Failed to release warmup lock key={}", LOCK_KEY, e);
            }
        }
    }
}