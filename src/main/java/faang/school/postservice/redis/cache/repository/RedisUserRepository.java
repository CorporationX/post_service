package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.model.RedisUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<RedisUser, Long> {
}
