package faang.school.postservice.repository;

import faang.school.postservice.dto.user.CacheUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<CacheUser, Long> {
}
