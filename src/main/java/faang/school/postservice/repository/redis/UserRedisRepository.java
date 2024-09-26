package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.UserRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedisRepository extends CrudRepository<UserRedis, Long> {
}
