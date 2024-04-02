package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.redis.UserRedisDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedisRepository extends JpaRepository<UserRedisDto, Long> {
}
