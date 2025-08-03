package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.model.RedisPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<RedisPost, Long> {
}
