package faang.school.postservice.repository.redis;

import faang.school.postservice.model.redis.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}
