package faang.school.postservice.cache.repository;

import faang.school.postservice.cache.model.CacheablePost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheablePostRepository extends CrudRepository<CacheablePost, Long> {
}
