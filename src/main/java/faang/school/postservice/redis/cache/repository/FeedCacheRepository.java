package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.FeedCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCacheRepository extends KeyValueRepository<FeedCache, Long> {
}
