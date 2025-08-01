package faang.school.postservice.newsfeed.repository;

import faang.school.postservice.newsfeed.dto.UserCacheDto;

public interface UsersCacheRepository {
    void putUser(UserCacheDto user);
    UserCacheDto getUserById(Long id);
}
