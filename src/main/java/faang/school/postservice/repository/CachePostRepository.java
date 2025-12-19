package faang.school.postservice.repository;

import faang.school.postservice.dto.redis.RedisPostDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachePostRepository extends CrudRepository<RedisPostDto, Long> {
}
