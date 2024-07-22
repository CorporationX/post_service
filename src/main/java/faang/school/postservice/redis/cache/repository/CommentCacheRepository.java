package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.CommentCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCacheRepository extends KeyValueRepository<CommentCache, Long> {
}
