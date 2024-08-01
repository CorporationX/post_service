package faang.school.postservice.cache.redis;

import faang.school.postservice.dto.feed.Feed;
import org.springframework.data.repository.CrudRepository;

public interface FeedCache extends CrudRepository<Feed, Long> {

}
