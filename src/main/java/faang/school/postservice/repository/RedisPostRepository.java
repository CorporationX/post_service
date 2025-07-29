package faang.school.postservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import faang.school.postservice.model.RedisPost;

@Repository
public interface RedisPostRepository extends CrudRepository<RedisPost, Long> {

}
