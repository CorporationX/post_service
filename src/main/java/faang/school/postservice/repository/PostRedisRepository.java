package faang.school.postservice.repository;

import faang.school.postservice.config.redis.entity.PostRedis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRedisRepository extends JpaRepository<PostRedis, Long> {
}
