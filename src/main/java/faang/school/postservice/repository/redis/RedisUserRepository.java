package faang.school.postservice.repository.redis;

import faang.school.postservice.model.CachedFeedUserDto;
import org.springframework.data.repository.CrudRepository;

// todo use redis from https://faang-school.atlassian.net/browse/BJS2-43433
public interface RedisUserRepository extends CrudRepository<CachedFeedUserDto, Long> {

}