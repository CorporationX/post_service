package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.feed.redis.PostRedisDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends CrudRepository<PostRedisDto, Long> {
}