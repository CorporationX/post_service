package faang.school.postservice.repository.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.redis.PostRedis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRedisRepository extends CrudRepository<PostRedis, String> {
}
