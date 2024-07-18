package faang.school.postservice.repository.cache;

import faang.school.postservice.model.redis.RedisPost;
import org.springframework.data.repository.CrudRepository;


public interface PostCacheRepository extends CrudRepository<RedisPost, Long> {
    //how should I update
}
