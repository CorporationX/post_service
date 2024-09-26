package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.user.CachedUserDto;
import org.springframework.data.repository.CrudRepository;

public interface RedisUserRepository extends CrudRepository<CachedUserDto, Long> {
}
