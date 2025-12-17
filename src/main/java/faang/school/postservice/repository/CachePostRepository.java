package faang.school.postservice.repository;

import faang.school.postservice.dto.redis.RedisPostDto;
import org.springframework.data.repository.CrudRepository;

public interface CachePostRepository extends CrudRepository<RedisPostDto, Long> {
}
