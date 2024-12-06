package faang.school.postservice.repository.user;

import faang.school.postservice.dto.redis.UserRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedisRepository extends CrudRepository<UserRedis, Long> {
}
