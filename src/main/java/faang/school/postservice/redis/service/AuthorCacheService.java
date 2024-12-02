package faang.school.postservice.redis.service;

import faang.school.postservice.model.dto.UserDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AuthorCacheService {

    void saveAuthorToCache(Long postAuthorId);

    CompletableFuture<Void> saveAllAuthorsToCache(List<UserDto> usersRange);
}
