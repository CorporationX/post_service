package faang.school.postservice.repository.redis;

import faang.school.postservice.entity.redis.Posts;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<Posts, Long> {
}
