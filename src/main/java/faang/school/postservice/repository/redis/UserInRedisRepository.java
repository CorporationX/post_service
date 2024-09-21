package faang.school.postservice.repository.redis;

import org.springframework.data.repository.CrudRepository;
import faang.school.postservice.model.redis.UserInRedis;

public interface UserInRedisRepository extends CrudRepository<UserInRedis, String> {

}
