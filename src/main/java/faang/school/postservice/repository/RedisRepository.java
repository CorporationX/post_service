package faang.school.postservice.repository;

import faang.school.postservice.model.PostRedisEvent;
import org.springframework.data.repository.CrudRepository;

public interface RedisRepository extends CrudRepository<PostRedisEvent, Integer> {

}
