package faang.school.postservice.redis.cache;

import faang.school.postservice.dto.post.PostForFeedDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostCache extends CrudRepository<PostForFeedDto, Long> {
}
