package faang.school.postservice.repository.cache;

import faang.school.postservice.model.chache.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCacheRepository extends CrudRepository<UserCache,Long>, PagingAndSortingRepository<UserCache,Long> {
}
