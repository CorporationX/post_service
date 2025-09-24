package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.Feed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RedisFeedRepository extends CrudRepository<Feed, Long> {

    Set<Long> getLimitPostIdsBySubscriberId(Long subscriberId, int limit);

    Set<Long> getLimitPostIdsBySubscriberIdAfterLastPostId(Long subscriberId,
                                                           Long lastPostId,
                                                           int limit);
}
