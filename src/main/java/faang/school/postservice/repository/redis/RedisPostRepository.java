package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.RedisPost;
import org.springframework.data.repository.CrudRepository;

public interface RedisPostRepository extends CrudRepository<RedisPost, Long> {
}
