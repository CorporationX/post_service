package faang.school.postservice.repository.cache;

import faang.school.postservice.model.redis.RedisFeed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCacheRepository extends CrudRepository<RedisFeed, Long> {
    //how should I save Posts' ids in redis feed in chronological order and configure to have a capacity
}
