package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.PostCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface PostCacheRepository extends KeyValueRepository<PostCache, Long> {
}
