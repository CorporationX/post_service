package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.Feed;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface FeedRepository extends CrudRepository<Feed, Long> {

    public List<Feed> findByUserId(Long userId);
}
