package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.UserRedis;
import org.springframework.data.repository.CrudRepository;

public interface RedisUserRepository extends CrudRepository<UserRedis, Long> {
}
