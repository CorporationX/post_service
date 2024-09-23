package faang.school.postservice.service.feed;

import faang.school.postservice.repository.cache.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedCacheRepository feedCacheRepository;

    public void updateFeed(Long subscriberId, Long postId) {
        feedCacheRepository.update(subscriberId, postId);
    }

    public List<Long> getFeed(Long subscriberId, int batchSize) {
        return feedCacheRepository.getTopPosts(subscriberId, batchSize);
    }
}
