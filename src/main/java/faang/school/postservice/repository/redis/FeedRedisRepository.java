package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.hash.FeedHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedRedisRepository extends CrudRepository<FeedHash, Long> {
    void saveInRedis(FeedHash feedHash);
}
