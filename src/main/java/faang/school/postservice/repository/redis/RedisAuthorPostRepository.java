package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.AuthorPostInRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisAuthorPostRepository extends CrudRepository<AuthorPostInRedis, Long> {
}
