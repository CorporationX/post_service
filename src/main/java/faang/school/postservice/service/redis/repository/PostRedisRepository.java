package faang.school.postservice.service.redis.repository;

import faang.school.postservice.service.redis.entity.PostRedis;
import org.springframework.data.repository.CrudRepository;

public interface PostRedisRepository extends CrudRepository<PostRedis, Long> {
}
