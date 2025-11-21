package faang.school.postservice.repository;

import faang.school.postservice.model.PostRedis;
import faang.school.postservice.model.PostRedisByAuthor;
import org.springframework.data.repository.CrudRepository;

public interface RedisPostRepositoryByAuthor extends CrudRepository<PostRedisByAuthor, Long> {

}
