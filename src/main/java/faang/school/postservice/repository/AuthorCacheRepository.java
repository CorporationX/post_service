package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;

public interface AuthorCacheRepository {
    void set(UserDto user);

    UserDto get(long authorId);
}
