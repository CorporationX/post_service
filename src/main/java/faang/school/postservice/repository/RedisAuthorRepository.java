package faang.school.postservice.repository;

import faang.school.postservice.model.CacheAuthor;
import faang.school.postservice.model.RedisAuthor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisAuthorRepository extends CrudRepository<RedisAuthor, String> {
}
