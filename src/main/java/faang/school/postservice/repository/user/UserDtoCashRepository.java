package faang.school.postservice.repository.user;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserDtoCashRepository extends CrudRepository<UserDto, Long> {
}
