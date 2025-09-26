package faang.school.postservice.repository;

import faang.school.postservice.config.redis.entity.UserRedis;
import org.springframework.data.repository.CrudRepository;

public interface UserRedisRepository extends CrudRepository<UserRedis, Long> {
}
