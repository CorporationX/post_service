package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.AuthorRedisCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRedisRepository extends KeyValueRepository<AuthorRedisCache, Long> {
}
