package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.UserRedisDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedisRepository extends CrudRepository<UserRedisDto, Long> {
}
