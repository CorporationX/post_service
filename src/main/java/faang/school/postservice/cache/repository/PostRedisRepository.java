package faang.school.postservice.cache.repository;

import faang.school.postservice.cache.model.PostRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends CrudRepository<PostRedis, Long> {
}
