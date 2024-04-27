package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.hash.PostHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends CrudRepository<PostHash, Long> {
    void saveInRedis(PostHash postHash);
}
