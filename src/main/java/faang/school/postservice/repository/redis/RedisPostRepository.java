package faang.school.postservice.repository.redis;

import faang.school.postservice.model.CachedPostDto;
import org.springframework.data.repository.CrudRepository;

// todo use redis from https://faang-school.atlassian.net/browse/BJS2-43429
public interface RedisPostRepository extends CrudRepository<CachedPostDto, Long> {

}