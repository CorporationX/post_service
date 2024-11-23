package faang.school.postservice.repository;

import faang.school.postservice.model.Feed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCacheRepository extends CrudRepository<Feed, Long> {
}
