package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface RedisPostRepository extends CrudRepository<PostCache, Long> {
}
