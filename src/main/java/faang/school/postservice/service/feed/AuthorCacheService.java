package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.feed.AuthorCacheMapper;
import faang.school.postservice.model.feed.AuthorCache;
import faang.school.postservice.repository.feed.AuthorCacheRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;

@RequiredArgsConstructor
@Service
public class AuthorCacheService {
    private final AuthorCacheRedisRepository repository;
    private final AuthorCacheMapper authorCacheMapper;
    private final UserServiceClient userServiceClient;

    public CompletableFuture<Void> saveAllAuthorsInCache(List<UserDto> allUsers) {
        if (allUsers == null || allUsers.isEmpty()) {
            return completedFuture(null);
        }

        List<AuthorCache> authorCaches = allUsers.stream()
                .map(authorCacheMapper::toAuthorCache)
                .toList();

        if (!authorCaches.isEmpty()) {
            return runAsync(() -> repository.saveAll(authorCaches));
        }
        return completedFuture(null);
    }

    public void saveAuthorCache(Long postAuthorId) {
        UserDto author = userServiceClient.getUser(postAuthorId);
        AuthorCache authorCache = authorCacheMapper.toAuthorCache(author);
        repository.save(authorCache);
    }
}