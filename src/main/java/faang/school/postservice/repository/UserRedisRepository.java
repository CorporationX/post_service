package faang.school.postservice.repository;

import faang.school.postservice.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRedisRepository extends CrudRepository<User, Long> {
}
