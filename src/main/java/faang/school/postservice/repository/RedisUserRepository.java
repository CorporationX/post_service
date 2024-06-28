package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.data.repository.CrudRepository;

public interface RedisUserRepository extends CrudRepository<UserDto, Long> {
}
