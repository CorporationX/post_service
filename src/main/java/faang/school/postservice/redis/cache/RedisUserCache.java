package faang.school.postservice.redis.cache;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserCache extends CrudRepository<UserDto, Long> {
}