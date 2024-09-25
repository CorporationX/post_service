package faang.school.postservice.repository;

import faang.school.postservice.model.post.CachePost;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<CachePost, Long> {

    Iterable<CachePost> findAllById(Iterable<Long> ids, PageRequest pageRequest);
}
