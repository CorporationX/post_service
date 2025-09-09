package faang.school.postservice.cache.author;

import faang.school.postservice.dto.user.UserDto;

public interface AuthorCache {
    void set(UserDto dto);

    UserDto get(long id);
}
