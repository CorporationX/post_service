package faang.school.postservice.repository;

import faang.school.postservice.dto.post.CachePost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<CachePost, Long> {
}
