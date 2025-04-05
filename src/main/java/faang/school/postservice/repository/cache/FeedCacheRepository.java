package faang.school.postservice.repository.cache;

import java.util.Set;

public interface FeedCacheRepository {

    void cacheFeed(Long userId, Long postId);

    Set<Long> getPostEvents(Long userId, Long batch);

    void removePost(Long postEvent, Long userId);
}
