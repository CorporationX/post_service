package faang.school.postservice.repository.redis;

import faang.school.postservice.redis.entities.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCache, Long> {
}