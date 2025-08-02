package faang.school.postservice.repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

public interface FeedCacheRepository {
    void set(long userId, ConcurrentLinkedDeque<Long> postIds);

    Optional<ConcurrentLinkedDeque<Long>> get(long userId);
}
