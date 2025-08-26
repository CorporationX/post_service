package faang.school.postservice.service.feed;

import java.util.List;

public interface FeedCachePort {

    void addToFeed(Long userId, Long postId, double score);

    void trimToMaxSize(Long userId, int maxSize);

    List<Long> getTop(Long userId, int limit);

    List<Long> getAfter(Long userId, Long afterPostId, int limit);
}
