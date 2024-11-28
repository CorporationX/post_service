package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostLikesCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostLikeRepository extends CrudRepository<PostLikesCache, Long> {
}
