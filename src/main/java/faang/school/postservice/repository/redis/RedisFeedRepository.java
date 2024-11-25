package faang.school.postservice.repository.redis;

import faang.school.postservice.model.CachedFeedDto;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

// todo use redis from https://faang-school.atlassian.net/browse/BJS2-43428
public interface RedisFeedRepository extends CrudRepository<CachedFeedDto, Long> {

    Optional<CachedFeedDto> findByUserId(Long userId);
}
