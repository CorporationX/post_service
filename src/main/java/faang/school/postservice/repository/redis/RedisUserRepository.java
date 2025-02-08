package faang.school.postservice.repository.redis;

import faang.school.postservice.model.event.UserEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<UserEvent, Long> {

}
