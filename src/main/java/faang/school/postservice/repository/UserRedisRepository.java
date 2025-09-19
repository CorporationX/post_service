package faang.school.postservice.repository;

import faang.school.postservice.config.redis.entity.UserRedis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRedisRepository extends JpaRepository<UserRedis, Long> {
}
