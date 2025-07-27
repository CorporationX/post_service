package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.PostCacheModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCacheModel, String> {
}
