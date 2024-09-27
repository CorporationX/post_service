package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.FeedCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCacheRedisRepository extends CrudRepository<FeedCache, Long> {}
