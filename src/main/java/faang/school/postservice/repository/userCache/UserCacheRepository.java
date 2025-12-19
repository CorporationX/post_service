package faang.school.postservice.repository.userCache;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCacheRepository {

    void save(UserDto user);

    Optional<UserDto> findById(long userId);
}
