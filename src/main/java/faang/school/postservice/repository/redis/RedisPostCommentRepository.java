package faang.school.postservice.repository.redis;


import faang.school.postservice.model.redis.PostCommentsCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostCommentRepository extends CrudRepository<PostCommentsCache, Long> {
}
