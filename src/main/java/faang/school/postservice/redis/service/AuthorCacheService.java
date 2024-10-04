package faang.school.postservice.redis.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorCacheService {
    private final AuthorCacheRedisRepository repository;
    private final AuthorCacheMapper authorCacheMapper;
    private final UserServiceClient userServiceClient;

    public void saveAllAuthorsInCache(List<UserDto> allUsers){
        if (allUsers == null || allUsers.isEmpty()) {
            return;
        }

        var authorCaches = allUsers.stream()
                .map(authorCacheMapper::toAuthorCache)
                .toList();

        if (!authorCaches.isEmpty()) {
            repository.saveAll(authorCaches);
        }
    }

    public void saveAuthorCache(Long postAuthorId){
        var author = userServiceClient.getUser(postAuthorId);
        var authorCache = authorCacheMapper.toAuthorCache(author);
        repository.save(authorCache);
    }
}