package faang.school.postservice.initializer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CachePost;
import faang.school.postservice.dto.user.CacheUser;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import faang.school.postservice.service.PostService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CacheInitializer {
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final RedisPostRepository redisPostRepository;

    @Value("${spring.post.cache.ttl}")
    private int postTtl;

    @PostConstruct
    public void init() {
        List<UserDto> usersDtos = userServiceClient.getAllUsers();
        List<CacheUser> cacheUsers = convertUserDtosToCacheUsers(usersDtos);

        redisUserRepository.saveAll(cacheUsers);
    }

    private List<CacheUser> convertUserDtosToCacheUsers(List<UserDto> usersDtos) {
        return usersDtos.stream().map(userDto -> {
            List<CachePost> userFeed = getPostToFeed(userDto.getId());

            return CacheUser.builder()
                    .id(userDto.getId())
                    .feed(new LinkedHashSet<>(userFeed.stream().map(CachePost::getId).toList()))
                    .ttl(postTtl).build();
        }).toList();
    }

    private List<CachePost> getPostToFeed(long userId) {
        List<CachePost> userFeed = postService.getPostsByAuthorsIds(
                userServiceClient.getFollowingIds(userId));
        redisPostRepository.saveAll(userFeed);
         return userFeed;
    }
}
