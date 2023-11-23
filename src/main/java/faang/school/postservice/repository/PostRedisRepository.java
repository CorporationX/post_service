package faang.school.postservice.repository;

import faang.school.postservice.dto.post.RedisPostDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends CrudRepository<RedisPostDto, Long> {
}
