package faang.school.postservice.repository;

import faang.school.postservice.config.redis.entity.PostRedis;
import org.springframework.data.repository.CrudRepository;

public interface PostRedisRepository extends CrudRepository<PostRedis, Long> {
}
