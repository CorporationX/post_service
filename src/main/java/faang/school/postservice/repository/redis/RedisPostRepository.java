package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.RedisPost;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.Optional;


public interface RedisPostRepository extends KeyValueRepository<RedisPost, Long> {
    Optional<RedisPost> findById(Long id);
}
