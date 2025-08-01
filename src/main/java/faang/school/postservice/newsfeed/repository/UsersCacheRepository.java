package faang.school.postservice.newsfeed.repository;

import faang.school.postservice.newsfeed.dto.UserCacheDto;

import java.util.Optional;

public interface UsersCacheRepository {
    void putUser(UserCacheDto user);
    Optional<UserCacheDto> getUserById(Long id);
}
