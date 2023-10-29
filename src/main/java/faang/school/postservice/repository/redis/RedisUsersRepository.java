package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.UserRedisDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUsersRepository extends CrudRepository<UserRedisDto, Long> {
}
