package faang.school.postservice.redis.cache.service.author;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.cache.entity.AuthorCache;

import java.util.concurrent.CompletableFuture;

public interface AuthorCacheService {

    CompletableFuture<UserDto> save(long id);

    CompletableFuture<UserDto> getUserDtoById(long id);
}
