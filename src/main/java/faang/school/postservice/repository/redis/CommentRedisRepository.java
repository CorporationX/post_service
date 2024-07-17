package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.CommentRedisCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRedisRepository extends KeyValueRepository<CommentRedisCache, Long> {
}
