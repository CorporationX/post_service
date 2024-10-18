package faang.school.postservice.repository.redisCache;

import faang.school.postservice.dto.redisCache.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<PostCache, Long> {
}
