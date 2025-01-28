package faang.school.postservice.repository;

import faang.school.postservice.model.RedisPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<RedisPost, Long> {
}
