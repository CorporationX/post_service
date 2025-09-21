package faang.school.postservice.repository.redis.user;

import faang.school.postservice.model.redis.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCache, String>, UserCacheRepositoryCustom {}
