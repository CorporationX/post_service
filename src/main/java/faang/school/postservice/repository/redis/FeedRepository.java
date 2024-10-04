package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.RedisFeed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface FeedRepository extends CrudRepository<RedisFeed, Long> {

    public List<RedisFeed> findByUserId(Long userId);
}
