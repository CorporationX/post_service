package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.AuthorCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface AuthorCacheRepository extends KeyValueRepository<AuthorCache, Long> {
}
