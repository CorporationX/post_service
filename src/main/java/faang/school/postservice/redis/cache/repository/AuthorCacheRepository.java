package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.AuthorCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRepository extends KeyValueRepository<AuthorCache, Long> {
}
