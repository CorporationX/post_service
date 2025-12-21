package faang.school.postservice.repository.cache;

import faang.school.postservice.config.redis.AuthorCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRepository extends CrudRepository<AuthorCache, Long> {
}
