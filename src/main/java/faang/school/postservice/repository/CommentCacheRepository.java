package faang.school.postservice.repository;

import faang.school.postservice.redis.CommentCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCacheRepository extends CrudRepository<CommentCache, String> {}