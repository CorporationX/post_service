package faang.school.postservice.repository;

import java.util.Optional;
import java.util.Set;

public interface FeedCacheRepository {
    void set(long userId, long postId);

    Optional<Set<Long>> get(long userId);

    Optional<Set<Long>> get(long userId, int offset);
}
