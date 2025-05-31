package faang.school.postservice.repository;

import faang.school.postservice.model.PostRedis;
import org.springframework.data.repository.CrudRepository;

public interface PostRedisRepository extends CrudRepository<PostRedis, Long> {
}
