package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.events.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.UserForCache;
import faang.school.postservice.service.redis.UserCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostEventService {

    private final UserCacheService userCacheService;
    private final UserServiceClient userServiceClient;

    public PostEvent getPostEventFromPost(Post post) {
        Long authorId = post.getAuthorId();
        List<Long> followerIds = getFollowerIds(authorId);
        return PostEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .authorFollowerIds(followerIds)
                .build();
    }

    public List<Long> getFollowerIds(Long authorId) {
        List<Long> followerIds = new ArrayList<>();
        Optional<UserForCache> userFromCacheOptional = userCacheService.getUserFromCache(authorId);
        if (userFromCacheOptional.isPresent()) {
            UserForCache userFromCache = userFromCacheOptional.get();
            followerIds = userFromCache.getFollowerIds();
        } else {
            UserDto userDto = userServiceClient.getUser(authorId);
            followerIds = userDto.getFollowerIds();
        }
        return followerIds;
    }
}
