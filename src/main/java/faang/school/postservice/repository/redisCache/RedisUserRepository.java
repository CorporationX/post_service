package faang.school.postservice.repository.redisCache;

import faang.school.postservice.dto.redisCache.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<UserCache, Long> {

}
