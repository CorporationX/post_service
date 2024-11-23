package faang.school.postservice.repository.post;

import faang.school.postservice.model.post.CacheablePost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<CacheablePost, Long> {
}
