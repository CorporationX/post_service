package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.AuthorCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorCacheRepository extends CrudRepository<AuthorCache, Long> {
}
