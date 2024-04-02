package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.PostRedisDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends JpaRepository<PostRedisDto, Long> {
}
