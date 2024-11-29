package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.CachedAuthor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedAuthorRepository extends CrudRepository<CachedAuthor, Long> {
}
