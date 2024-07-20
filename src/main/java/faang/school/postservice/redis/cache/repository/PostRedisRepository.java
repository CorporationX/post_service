package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.entity.PostRedisCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends KeyValueRepository<PostRedisCache, Long> {
}
