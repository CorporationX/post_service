package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.entity.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRedisRepository extends CrudRepository<PostCache, Long> {}
