package faang.school.postservice.repository.redis;

import faang.school.postservice.model.cache.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisPostRepository extends CrudRepository<Post, Long> {

    Optional<Post> findById(Long postId);
}
