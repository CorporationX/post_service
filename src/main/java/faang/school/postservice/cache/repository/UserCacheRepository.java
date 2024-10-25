package faang.school.postservice.cache.repository;

import faang.school.postservice.cache.entity.UserCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends JpaRepository<UserCache, Long> {
}
