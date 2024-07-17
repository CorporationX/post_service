package faang.school.postservice.redis.cache;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<PostDto, Long> {
}
