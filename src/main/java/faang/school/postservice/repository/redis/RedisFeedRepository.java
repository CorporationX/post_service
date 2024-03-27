package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.Feed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
public interface RedisFeedRepository extends CrudRepository<Feed, Long> {
}
