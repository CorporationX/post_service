package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;

import java.util.Optional;

public interface UserCacheRepository {

    Optional<UserDto> get(long authorId);

    void save(long authorId, UserDto dto);
}