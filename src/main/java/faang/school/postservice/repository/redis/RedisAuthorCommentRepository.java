package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.AuthorCommentInRedis;
import org.springframework.data.repository.CrudRepository;

public interface RedisAuthorCommentRepository extends CrudRepository<AuthorCommentInRedis, Long> {
}
