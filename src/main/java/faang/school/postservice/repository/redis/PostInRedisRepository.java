package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.PostInRedis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostInRedisRepository extends JpaRepository<PostInRedis, String> {
}
