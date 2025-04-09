package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.entity.PostCache;
import org.springframework.data.repository.CrudRepository;

public interface PostCacheRedisRepository extends CrudRepository<PostCache, Long> {
}
