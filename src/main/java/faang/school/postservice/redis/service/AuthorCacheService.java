package faang.school.postservice.redis.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.model.AuthorCache;
import faang.school.postservice.redis.repository.AuthorCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorCacheService {

    private final AuthorCacheRepository authorCacheRepository;
    private final UserServiceClient userServiceClient;
    private final AuthorCacheMapper authorCacheMapper;

    public void updateAuthorCache(Long authorId, Long postId) {
        AuthorCache authorCache = authorCacheRepository.findById(authorId).orElseGet(() -> saveAuthor(authorId, postId));

        authorCache.getSubscribers().addAll(updateSubscribers(authorCache));
    }

    public AuthorCache getSubscribers(Long authorId) {
        return authorCacheRepository.findById(authorId).orElse(null);
    }

    private AuthorCache saveAuthor(Long authorId, Long postId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        AuthorCache authorCache = authorCacheMapper.toAuthorCache(userDto, postId);

        return authorCacheRepository.save(authorCache);
    }

    private List<Long> updateSubscribers(AuthorCache authorId) {
        return userServiceClient.getFollowers(authorId.getId()).stream().map(UserDto::getId).toList();
    }
}
