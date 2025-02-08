package faang.school.postservice.repository.redis;

import faang.school.postservice.model.event.PostEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisPostRepository extends CrudRepository<PostEvent, Long> {

}
