package faang.school.postservice.redis.cache;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisFeedCache extends CrudRepository<Feed, Long> {
}
