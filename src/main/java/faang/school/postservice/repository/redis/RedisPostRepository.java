package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.RedisPost;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<RedisPost, String> {
}
