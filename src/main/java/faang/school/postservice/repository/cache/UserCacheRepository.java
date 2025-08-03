package faang.school.postservice.repository.cache;

import faang.school.postservice.model.cache.UserCacheEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserCacheRepository extends CrudRepository<UserCacheEntity, Long> {
}
