package faang.school.postservice.repository.feed;

import faang.school.postservice.model.feed.UserCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<UserCache, String> {
}
