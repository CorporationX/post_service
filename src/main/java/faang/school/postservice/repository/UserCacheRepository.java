package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCache, String> {

    Optional<UserCache> findByUserId(Long userId);
}
