package faang.school.postservice.redis.cache.repository;

import faang.school.postservice.redis.cache.model.RedisFeed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisFeedRepository extends CrudRepository<RedisFeed, Long> {
}
