package faang.school.postservice.cache.repository;

import faang.school.postservice.cache.model.CacheableUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheableUserRepository extends CrudRepository<CacheableUser, Long> {
}
