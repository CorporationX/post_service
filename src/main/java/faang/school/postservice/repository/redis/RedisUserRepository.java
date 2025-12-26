package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.user.CacheUserDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<CacheUserDto, Long> {

}
