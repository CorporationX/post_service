package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.PostEvent;
import faang.school.postservice.dto.redis.CachedFeedDto;
import faang.school.postservice.repository.CacheFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private static final int THREADS_AMOUNT = 10;

    private final ExecutorService updateFeedExecutor = Executors.newFixedThreadPool(THREADS_AMOUNT);
    private final CacheFeedRepository cacheFeedRepository;
    private final UserServiceClient userServiceClient;

    @Value("${feed.max_size}")
    private int maxFeedSize;

    @Override
    public void updateFeeds(PostEvent postEvent) {
        try {
            List<Long> followerIds = userServiceClient.checkExistentFollowers(postEvent.followerIds());
            for (long followerId : followerIds) {
                updateFeedExecutor.submit(() -> updateFeedsByEachThread(postEvent, followerId));
            }
        } catch (RuntimeException e) {
            for (long followerId : postEvent.followerIds()) {
                updateFeedExecutor.submit(() -> updateFeedsByEachThread(postEvent, followerId));
            }
        }
    }

    private void updateFeedsByEachThread(PostEvent postEvent, long followerId) {
        CachedFeedDto cachedFeedDto = cacheFeedRepository.findById(followerId).orElse(null);
        if (cachedFeedDto == null) {
            try {
                userServiceClient.getUser(followerId);
                cachedFeedDto = new CachedFeedDto(followerId, new LinkedHashSet<>());
            } catch (RuntimeException e) {
                log.warn("Из Kafka пришел PostEvent, в котором есть несуществующий followerId: {}", followerId);
                return;
            }
        }
        try {
            updatePostIds(cachedFeedDto, postEvent.postId());
            cacheFeedRepository.save(cachedFeedDto);
        } catch (OptimisticLockingFailureException e) {
            cachedFeedDto = cacheFeedRepository.findById(followerId).orElse(null);
            if (cachedFeedDto == null) {
                log.warn("Попытка добавить пост в фид пользователя с Id: {} сначала возник ", followerId +
                        "OptimisticLockingFailureException, но после попытки получить фид заново вернулся null");
                return;
            }
            updatePostIds(cachedFeedDto, postEvent.postId());
        }
        cacheFeedRepository.save(cachedFeedDto);
    }

    private void updatePostIds(CachedFeedDto cachedFeedDto, Long postId) {
        if (cachedFeedDto.getPostIds().size() >= maxFeedSize) {
            Iterator<Long> iterator = cachedFeedDto.getPostIds().iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        cachedFeedDto.getPostIds().add(postId);
    }
}
