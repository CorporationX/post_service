package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.entity.FeedCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedsCacheRepository extends CrudRepository<FeedCache,Long> {
}
