package faang.school.postservice.repository;

import faang.school.postservice.dto.redis.CachedFeedDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheFeedRepository extends CrudRepository<CachedFeedDto, Long> {
}
