package faang.school.postservice.repository.redis;

import faang.school.postservice.config.redis.entity.PostCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostCacheRepository extends CrudRepository<PostCache, String> {

    Optional<PostCache> findByPostId(Long postId);
}
