package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.Feed;
import org.springframework.data.repository.CrudRepository;

public interface FeedRepository extends CrudRepository<Feed, Long>  {
}
