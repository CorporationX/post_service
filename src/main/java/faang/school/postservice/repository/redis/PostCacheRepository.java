package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostCache;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@EnableRedisRepositories
public interface PostCacheRepository extends CrudRepository<PostCache, Long> {

}