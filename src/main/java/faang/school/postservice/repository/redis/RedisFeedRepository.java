package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.RedisFeed;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RedisFeedRepository extends CrudRepository<RedisFeed, Long> {
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId")
    Set findPostsByUserId(@Param("userId") long userId);
}
