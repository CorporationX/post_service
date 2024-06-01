package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.AuthorInRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisAuthorRepository extends CrudRepository<AuthorInRedis, Long> {
}
