package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.AuthorRedisCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRedisRepository extends KeyValueRepository<AuthorRedisCache, Long> {
}
