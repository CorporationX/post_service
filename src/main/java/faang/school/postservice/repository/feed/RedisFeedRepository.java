package faang.school.postservice.repository.feed;

import faang.school.postservice.model.feed.FeedCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisFeedRepository extends CrudRepository<FeedCache, String> {
}
