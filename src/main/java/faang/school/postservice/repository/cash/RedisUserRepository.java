package faang.school.postservice.repository.cash;

import faang.school.postservice.dto.cash.CashUserDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisUserRepository extends CrudRepository<CashUserDto, Long> {

}
