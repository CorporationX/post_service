package faang.school.postservice.repository.feed;

import faang.school.postservice.model.feed.AuthorCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRedisRepository extends CrudRepository<AuthorCache, Long> {
}
