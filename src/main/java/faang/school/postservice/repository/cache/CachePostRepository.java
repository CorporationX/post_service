package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.CachePost;
import org.springframework.data.repository.CrudRepository;

public interface CachePostRepository extends CrudRepository<CachePost, String> {
}
