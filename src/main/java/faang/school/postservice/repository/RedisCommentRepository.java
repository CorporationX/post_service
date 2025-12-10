package faang.school.postservice.repository;

import faang.school.postservice.model.CommentRedis;
import org.springframework.data.repository.CrudRepository;

public interface RedisCommentRepository extends CrudRepository<CommentRedis, Long> {

}
