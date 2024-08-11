package faang.school.postservice.cache.redis;

import faang.school.postservice.dto.post.CachedPostDto;
import org.springframework.data.repository.CrudRepository;

public interface PostCache extends CrudRepository<CachedPostDto, Long> {
}