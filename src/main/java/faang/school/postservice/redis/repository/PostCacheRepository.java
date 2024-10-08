package faang.school.postservice.redis.repository;

import faang.school.postservice.redis.model.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCache, Long>{}