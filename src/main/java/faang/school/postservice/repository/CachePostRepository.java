package faang.school.postservice.repository;

import faang.school.postservice.dto.redis.CachedPostDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachePostRepository extends CrudRepository<CachedPostDto, Long> {
}
