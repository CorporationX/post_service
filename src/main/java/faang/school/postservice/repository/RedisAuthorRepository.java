package faang.school.postservice.repository;

import faang.school.postservice.model.redis.AuthorRedis;
import org.springframework.data.repository.CrudRepository;

public interface RedisAuthorRepository extends CrudRepository<AuthorRedis, Long> {
}
