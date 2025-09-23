package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.Feed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RedisFeedRepository extends CrudRepository<Feed, Long> {
    // Получить первые N постов из фида пользователя
    Set<Long> getFirstPostIds(Long subscriberId, int limit);

    // Получить N постов после указанного
    Set<Long> getPostIdsAfter(Long subscriberId, Long lastPostId, int limit);

}
