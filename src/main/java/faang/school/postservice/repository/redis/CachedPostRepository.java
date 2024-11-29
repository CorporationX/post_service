package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.CachedPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedPostRepository extends CrudRepository<CachedPost, Long> {
}
