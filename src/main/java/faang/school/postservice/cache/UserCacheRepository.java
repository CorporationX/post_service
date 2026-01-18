package faang.school.postservice.cache;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;
import java.util.Map;

public interface UserCacheRepository {

    Map<Long, UserDto> findAllByIds(List<Long> userIds);

    void saveAll(Map<Long, UserDto> usersById);
}