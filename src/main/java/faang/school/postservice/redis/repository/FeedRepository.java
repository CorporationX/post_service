package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.Feed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRepository extends CrudRepository<Feed, Long> {
}
