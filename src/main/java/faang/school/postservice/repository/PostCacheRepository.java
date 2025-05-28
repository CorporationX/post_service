package faang.school.postservice.repository;

import faang.school.postservice.redisModel.PostCache;
import org.springframework.data.repository.CrudRepository;

public interface PostCacheRepository extends CrudRepository<PostCache, Long> {
}
