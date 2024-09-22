package faang.school.postservice.repository;

import faang.school.postservice.cache.dto.CachedPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<CachedPost, Long> {

}
