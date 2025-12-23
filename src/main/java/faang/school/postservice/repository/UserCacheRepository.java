package faang.school.postservice.repository;

import faang.school.postservice.config.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCache, String> {

    Optional<UserCache> findByUserId(Long userId);

    List<UserCache> findByUserIdIn(List<Long> userIds);
}
