package faang.school.postservice.repository;

import faang.school.postservice.dto.post.CachePost;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.LinkedHashSet;
import java.util.List;

@Repository
public interface RedisPostRepository extends CrudRepository<CachePost, Long> {

    Iterable<CachePost> findAllById(Iterable<Long> ids, PageRequest pageRequest);
}
