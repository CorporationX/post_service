package faang.school.postservice.repository;

import faang.school.postservice.model.CacheCommentAuthor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisAuthorCommentRepository extends CrudRepository<CacheCommentAuthor, Long> {
}
