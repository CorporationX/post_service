package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostViewsCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostViewRepository extends CrudRepository<PostViewsCache, Long> {
}
