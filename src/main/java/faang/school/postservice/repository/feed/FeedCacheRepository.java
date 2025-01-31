package faang.school.postservice.repository.feed;

import faang.school.postservice.dto.feed.FeedDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCacheRepository extends CrudRepository<FeedDto, String> {
}
