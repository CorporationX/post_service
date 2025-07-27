package faang.school.postservice.repository;

import faang.school.postservice.model.cache.UserCacheModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCacheModel, String> {
}
