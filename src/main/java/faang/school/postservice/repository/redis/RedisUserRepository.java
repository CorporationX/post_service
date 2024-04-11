package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface RedisUserRepository extends CrudRepository<UserCache, Long> {
}
