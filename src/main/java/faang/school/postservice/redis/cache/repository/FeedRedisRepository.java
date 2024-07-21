package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.FeedRedisCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRedisRepository extends KeyValueRepository<FeedRedisCache, Long> {
}
