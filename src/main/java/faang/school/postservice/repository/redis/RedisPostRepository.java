package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.CachedPostDto;
import org.springframework.data.repository.CrudRepository;

public interface RedisPostRepository extends CrudRepository<CachedPostDto, Long> {
}
