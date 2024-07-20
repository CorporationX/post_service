package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.CommentRedisCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRedisRepository extends KeyValueRepository<CommentRedisCache, Long> {
}
