package faang.school.postservice.repository;

import faang.school.postservice.model.UserRedis;
import org.springframework.data.repository.CrudRepository;

public interface UserRedisRepository extends CrudRepository<UserRedis, Long> {
}

