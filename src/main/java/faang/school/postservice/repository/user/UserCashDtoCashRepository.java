package faang.school.postservice.repository.user;

import faang.school.postservice.dto.user.UserCashDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserCashDtoCashRepository extends CrudRepository<UserCashDto, Long> {
}
