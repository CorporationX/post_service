package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedis;
import org.springframework.data.repository.CrudRepository;

public interface RedisPostRepository extends CrudRepository<PostRedis, Long> {

}
