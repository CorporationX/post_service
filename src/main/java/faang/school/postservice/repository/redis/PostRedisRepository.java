package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostRedisCache;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends KeyValueRepository<PostRedisCache, Long> {
}
