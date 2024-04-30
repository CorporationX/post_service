package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Alexander Bulgakov
 */

@Repository
public interface RedisAuthorRepository extends CrudRepository<Author, Long> {
}
