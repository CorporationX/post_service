package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.FeedForCache;
import org.springframework.data.repository.CrudRepository;

public interface FeedRepository extends CrudRepository<FeedForCache, Long> {

    FeedForCache findByUserId(Long userId);
}
