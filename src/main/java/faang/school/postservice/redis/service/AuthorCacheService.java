package faang.school.postservice.redis.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.model.AuthorCache;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthorCacheService {
    private final AuthorCacheRedisRepository authorCacheRedisRepository;
    private final AuthorCacheMapper authorCacheMapper;
    private final UserServiceClient userServiceClient;

    public void saveAllAuthorsInCache(List<UserDto> allUsers){
        if (allUsers == null || allUsers.isEmpty()) {
            CompletableFuture.completedFuture(null);
            return;
        }
        List<AuthorCache> authorCaches = allUsers.stream()
                .map(authorCacheMapper::toAuthorCache)
                .toList();

        if (!authorCaches.isEmpty()) {
            CompletableFuture.runAsync(() -> authorCacheRedisRepository.saveAll(authorCaches));
            return;
        }

        CompletableFuture.completedFuture(null);
    }

    public void saveAuthorCache(Long postAuthorId){
        UserDto author = userServiceClient.getUser(postAuthorId);
        AuthorCache authorCache = authorCacheMapper.toAuthorCache(author);
        authorCacheRedisRepository.save(authorCache);
    }
}